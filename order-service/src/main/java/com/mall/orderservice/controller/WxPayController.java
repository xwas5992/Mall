
import com.mall.orderservice.dto.WxPayRequest;
import com.mall.orderservice.service.WxPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "微信支付", description = "微信支付相关接口")
@RestController
@RequestMapping("/api/v1/pay/wx")
@RequiredArgsConstructor
public class WxPayController {

    private final WxPayService wxPayService;

    @Operation(summary = "创建H5支付订单")
    @PostMapping("/h5")
    public ResponseEntity<Map<String, String>> createH5Pay(@Valid @RequestBody WxPayRequest request) {
        try {
            Map<String, String> result = wxPayService.createH5Pay(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "支付回调通知")
    @PostMapping("/notify")
    public String handlePayNotify(@RequestBody String notifyData) {
        try {
            wxPayService.handlePayNotify(notifyData);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

    @Operation(summary = "查询支付订单")
    @GetMapping("/query/{orderNo}")
    public ResponseEntity<Map<String, Object>> queryPayOrder(@PathVariable String orderNo) {
        try {
            Map<String, Object> result = wxPayService.queryPayOrder(orderNo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "关闭支付订单")
    @PostMapping("/close/{orderNo}")
    public ResponseEntity<Void> closePayOrder(@PathVariable String orderNo) {
        try {
            wxPayService.closePayOrder(orderNo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/{orderNo}")
    public ResponseEntity<Map<String, Object>> refund(
            @PathVariable String orderNo,
            @RequestParam Long refundAmount,
            @RequestParam String reason) {
        try {
            Map<String, Object> result = wxPayService.refund(orderNo, refundAmount, reason);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "查询退款")
    @GetMapping("/refund/query/{refundNo}")
    public ResponseEntity<Map<String, Object>> queryRefund(@PathVariable String refundNo) {
        try {
            Map<String, Object> result = wxPayService.queryRefund(refundNo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 