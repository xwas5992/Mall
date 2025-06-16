

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayConfig {
    
    /**
     * 商户号
     */
    private String mchId;
    
    /**
     * 商户API证书序列号
     */
    private String mchSerialNo;
    
    /**
     * 商户APIv3密钥
     */
    private String apiV3Key;
    
    /**
     * 商户私钥文件路径
     */
    private String privateKeyPath;
    
    /**
     * 微信支付平台证书路径
     */
    private String wechatPayCertPath;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 支付回调通知地址
     */
    private String notifyUrl;
    
    /**
     * 支付超时时间（分钟）
     */
    private Integer timeoutMinutes = 30;
    
    /**
     * 是否沙箱环境
     */
    private Boolean sandbox = false;
} 