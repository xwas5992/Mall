

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WxPayRequest {
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;
    
    @NotBlank(message = "商品描述不能为空")
    private String description;
    
    @NotBlank(message = "客户端IP不能为空")
    private String clientIp;
    
    @NotBlank(message = "回调通知地址不能为空")
    private String notifyUrl;
    
    // 用户openId，用于JSAPI支付
    private String openId;
    
    // 场景信息，用于H5支付
    private SceneInfo sceneInfo;
    
    @Data
    public static class SceneInfo {
        private String type = "Wap";  // 场景类型：Wap
        private String wapUrl;        // WAP网站URL地址
        private String wapName;       // WAP网站名称
    }
} 