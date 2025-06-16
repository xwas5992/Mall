

import com.mall.orderservice.model.RefundOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RefundRequest {
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotNull(message = "退款类型不能为空")
    private RefundOrder.RefundType refundType;
    
    @NotEmpty(message = "退款商品不能为空")
    @Valid
    private List<RefundItemRequest> items;
    
    @NotNull(message = "退款金额不能为空")
    private String reason;  // 退款原因
    
    private String description;  // 退款说明
    
    private String logisticsCompany;  // 退货物流公司（仅退款退货时需要）
    
    private String logisticsNo;  // 退货物流单号（仅退款退货时需要）
    
    @Data
    public static class RefundItemRequest {
        @NotNull(message = "订单项ID不能为空")
        private Long orderItemId;
        
        @NotNull(message = "退款数量不能为空")
        private Integer quantity;
        
        private String reason;  // 退款原因
        
        private String description;  // 退款说明
        
        private List<String> images;  // 退款图片URL列表
    }
} 