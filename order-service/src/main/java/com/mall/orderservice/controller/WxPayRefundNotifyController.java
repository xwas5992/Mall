

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.orderservice.service.WxPayService;
import com.mall.orderservice.util.WxPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/pay/refund/notify")
@RequiredArgsConstructor
public class WxPayRefundNotifyController {

    private final WxPayService wxPayService;
    private final WxPayUtil wxPayUtil;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<String> handleRefundNotify(@RequestBody String notifyData) {
        log.info("收到退款回调通知: {}", notifyData);
        try {
            // 1. 验证签名
            if (!wxPayUtil.verifySign(notifyData)) {
                log.error("退款回调签名验证失败");
                return ResponseEntity.badRequest().body("签名验证失败");
            }

            // 2. 解密回调数据
            JsonNode rootNode = objectMapper.readTree(notifyData);
            String ciphertext = rootNode.path("resource").path("ciphertext").asText();
            String associatedData = rootNode.path("resource").path("associated_data").asText();
            String nonce = rootNode.path("resource").path("nonce").asText();
            
            String decryptedData = wxPayUtil.decryptRefundNotifyData(ciphertext, associatedData, nonce);
            log.info("解密后的退款回调数据: {}", decryptedData);

            // 3. 解析退款结果
            JsonNode refundNode = objectMapper.readTree(decryptedData);
            String refundStatus = refundNode.path("refund_status").asText();
            String orderNo = refundNode.path("out_trade_no").asText();
            String refundNo = refundNode.path("out_refund_no").asText();
            String successTime = refundNode.path("success_time").asText();
            String refundAmount = refundNode.path("amount").path("refund").asText();
            String totalAmount = refundNode.path("amount").path("total").asText();
            String payerTotal = refundNode.path("amount").path("payer_total").asText();
            String payerRefund = refundNode.path("amount").path("payer_refund").asText();

            // 4. 处理退款结果
            wxPayService.handleRefundNotify(
                orderNo,
                refundNo,
                refundStatus,
                successTime,
                refundAmount,
                totalAmount,
                payerTotal,
                payerRefund
            );

            // 5. 返回成功响应
            Map<String, String> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("message", "成功");
            return ResponseEntity.ok(objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            log.error("处理退款回调通知失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("code", "FAIL");
            response.put("message", "处理失败");
            try {
                return ResponseEntity.ok(objectMapper.writeValueAsString(response));
            } catch (Exception ex) {
                return ResponseEntity.internalServerError().body("处理失败");
            }
        }
    }
} 