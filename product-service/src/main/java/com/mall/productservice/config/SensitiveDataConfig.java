

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class SensitiveDataConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule module = new SimpleModule();
        
        // 注册敏感数据序列化器
        module.addSerializer(String.class, new SensitiveDataSerializer());
        objectMapper.registerModule(module);
        
        return objectMapper;
    }

    public static class SensitiveDataSerializer extends com.fasterxml.jackson.databind.JsonSerializer<String> {
        @Override
        public void serialize(String value, com.fasterxml.jackson.core.JsonGenerator gen,
                            com.fasterxml.jackson.databind.SerializerProvider provider) throws java.io.IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            // 根据字段名判断是否需要脱敏
            String fieldName = gen.getOutputContext().getCurrentName();
            String maskedValue = maskSensitiveData(value, fieldName);
            gen.writeString(maskedValue);
        }

        private String maskSensitiveData(String value, String fieldName) {
            if (value == null || value.isEmpty()) {
                return value;
            }

            // 根据字段名和值类型进行不同的脱敏处理
            switch (fieldName.toLowerCase()) {
                case "phone":
                case "mobile":
                case "telephone":
                    return maskPhone(value);
                case "email":
                    return maskEmail(value);
                case "idcard":
                case "identity":
                    return maskIdCard(value);
                case "bankcard":
                case "cardno":
                    return maskBankCard(value);
                case "address":
                    return maskAddress(value);
                case "name":
                    return maskName(value);
                default:
                    return value;
            }
        }

        private String maskPhone(String phone) {
            if (phone == null || phone.length() < 7) {
                return phone;
            }
            return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
        }

        private String maskEmail(String email) {
            if (email == null || !email.contains("@")) {
                return email;
            }
            int atIndex = email.indexOf("@");
            String name = email.substring(0, atIndex);
            String domain = email.substring(atIndex);
            
            if (name.length() <= 2) {
                return name.charAt(0) + "***" + domain;
            }
            return name.charAt(0) + "***" + name.charAt(name.length() - 1) + domain;
        }

        private String maskIdCard(String idCard) {
            if (idCard == null || idCard.length() < 15) {
                return idCard;
            }
            return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
        }

        private String maskBankCard(String bankCard) {
            if (bankCard == null || bankCard.length() < 8) {
                return bankCard;
            }
            return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
        }

        private String maskAddress(String address) {
            if (address == null || address.length() < 8) {
                return address;
            }
            return address.substring(0, 4) + "****" + address.substring(address.length() - 4);
        }

        private String maskName(String name) {
            if (name == null || name.isEmpty()) {
                return name;
            }
            if (name.length() == 1) {
                return "*";
            }
            if (name.length() == 2) {
                return name.charAt(0) + "*";
            }
            return name.charAt(0) + "*" + name.charAt(name.length() - 1);
        }
    }
} 