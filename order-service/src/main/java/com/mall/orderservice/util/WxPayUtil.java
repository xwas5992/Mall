import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.orderservice.config.WxPayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WxPayUtil {

    private final WxPayConfig wxPayConfig;
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;

    /**
     * 生成签名
     */
    public String generateSignature(String method, String url, String body, String timestamp, String nonce) 
            throws Exception {
        String message = method + "\n" + url + "\n" + timestamp + "\n" + nonce + "\n" + body + "\n";
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(wxPayConfig.getMerchantPrivateKey());
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 验证签名
     */
    public boolean verifySignature(String serialNo, String signature, String body, String timestamp, String nonce) 
            throws Exception {
        String message = timestamp + "\n" + nonce + "\n" + body + "\n";
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(wxPayConfig.getWechatPayCertificate());
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        return sign.verify(Base64.getDecoder().decode(signature));
    }

    /**
     * 加密敏感信息
     */
    public String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, wxPayConfig.getWechatPayCertificate().getPublicKey());
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密敏感信息
     */
    public String decrypt(String ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, wxPayConfig.getMerchantPrivateKey());
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 发送POST请求
     */
    public Map<String, Object> post(String url, Map<String, Object> params) throws IOException {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String nonce = UUID.randomUUID().toString().replaceAll("-", "");
        String body = objectMapper.writeValueAsString(params);
        
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        httpPost.setHeader("Authorization", generateAuthorizationHeader("POST", url, body, timestamp, nonce));
        httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(responseBody, Map.class);
        }
    }

    /**
     * 发送GET请求
     */
    public Map<String, Object> get(String url) throws IOException {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String nonce = UUID.randomUUID().toString().replaceAll("-", "");
        
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        httpGet.setHeader("Authorization", generateAuthorizationHeader("GET", url, "", timestamp, nonce));

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(responseBody, Map.class);
        }
    }

    /**
     * 生成认证头
     */
    private String generateAuthorizationHeader(String method, String url, String body, 
            String timestamp, String nonce) throws Exception {
        String signature = generateSignature(method, url, body, timestamp, nonce);
        return String.format("WECHATPAY2-SHA256-RSA2048 mchid=\"%s\",nonce_str=\"%s\"," +
                "timestamp=\"%s\",serial_no=\"%s\",signature=\"%s\"",
                wxPayConfig.getMchId(), nonce, timestamp, wxPayConfig.getMchSerialNo(), signature);
    }

    /**
     * 生成随机字符串
     */
    public String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成时间戳
     */
    public String generateTimestamp() {
        return String.valueOf(Instant.now().getEpochSecond());
    }

    /**
     * 解密退款回调数据
     * @param ciphertext 密文
     * @param associatedData 附加数据
     * @param nonce 随机串
     * @return 解密后的数据
     */
    public String decryptRefundNotifyData(String ciphertext, String associatedData, String nonce) {
        try {
            // 1. 对密文进行base64解码
            byte[] cipherBytes = Base64.getDecoder().decode(ciphertext);
            
            // 2. 对随机串进行base64解码
            byte[] nonceBytes = Base64.getDecoder().decode(nonce);
            
            // 3. 对附加数据进行base64解码（如果有）
            byte[] associatedDataBytes = associatedData != null && !associatedData.isEmpty() 
                ? Base64.getDecoder().decode(associatedData) 
                : null;

            // 4. 使用AES-256-GCM解密
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonceBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            
            if (associatedDataBytes != null) {
                cipher.updateAAD(associatedDataBytes);
            }
            
            byte[] decryptedBytes = cipher.doFinal(cipherBytes);
            
            // 5. 返回解密后的字符串
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密退款回调数据失败", e);
            throw new RuntimeException("解密退款回调数据失败", e);
        }
    }
} 