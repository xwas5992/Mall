

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class EncryptionConfig {

    @Value("${encryption.key}")
    private String encryptionKey;

    @Value("${encryption.salt}")
    private String encryptionSalt;

    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.text(encryptionKey, encryptionSalt);
    }

    @Bean
    public AesEncryptor aesEncryptor() throws Exception {
        return new AesEncryptor(encryptionKey);
    }

    public static class AesEncryptor {
        private final SecretKey key;
        private final Cipher cipher;
        private final SecureRandom secureRandom;

        public AesEncryptor(String keyString) throws Exception {
            // 生成密钥
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, new SecureRandom(keyString.getBytes()));
            this.key = keyGen.generateKey();
            
            // 初始化加密器
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.secureRandom = new SecureRandom();
        }

        public String encrypt(String plaintext) throws Exception {
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // 将IV和加密后的数据组合在一起
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        }

        public String decrypt(String encryptedText) throws Exception {
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // 提取IV
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 提取加密数据
            byte[] encrypted = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        }
    }
} 