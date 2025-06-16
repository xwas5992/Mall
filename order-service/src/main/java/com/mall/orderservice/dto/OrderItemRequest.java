

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class OrderItemRequest {
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotNull(message = "商品SKU不能为空")
    private String productSku;
    
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;
    
    // 商品属性，如：颜色、尺寸等
    private Map<String, String> properties;
    
    // 商品优惠信息
    private ItemDiscountRequest discount;
    
    @Data
    public static class ItemDiscountRequest {
        private String type;        // 优惠类型：满减、折扣等
        private String activityId;  // 活动ID
        private String couponId;    // 优惠券ID
        private Double discountRate;// 折扣率
        private Long discountAmount;// 优惠金额
    }
} 