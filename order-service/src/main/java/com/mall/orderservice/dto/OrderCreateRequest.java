

import com.mall.orderservice.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    @NotNull(message = "支付方式不能为空")
    private Order.PaymentType paymentType;
    
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;
    
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;
    
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;
    
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverZip;
    
    private String remark;
    
    // 优惠券ID
    private Long couponId;
    
    // 积分抵扣数量
    private Integer pointsDeduction;
    
    // 发票信息
    private InvoiceRequest invoice;
    
    @Data
    public static class InvoiceRequest {
        private String type;        // 发票类型：个人/公司
        private String title;       // 发票抬头
        private String taxNumber;   // 税号
        private String email;       // 接收发票的邮箱
    }
} 