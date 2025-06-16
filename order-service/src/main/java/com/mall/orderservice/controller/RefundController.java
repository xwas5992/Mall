

import com.mall.orderservice.dto.RefundRequest;
import com.mall.orderservice.model.RefundOrder;
import com.mall.orderservice.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    /**
     * 申请退款
     */
    @PostMapping
    public ResponseEntity<RefundOrder> applyRefund(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RefundRequest request) {
        return ResponseEntity.ok(refundService.applyRefund(userId, request));
    }

    /**
     * 审核退款申请
     */
    @PostMapping("/{refundId}/approve")
    public ResponseEntity<RefundOrder> approveRefund(
            @PathVariable Long refundId,
            @RequestHeader("X-Operator-Id") String operatorId,
            @RequestHeader("X-Operator-Name") String operatorName,
            @RequestParam(required = false) String remark) {
        return ResponseEntity.ok(refundService.approveRefund(refundId, operatorId, operatorName, remark));
    }

    /**
     * 拒绝退款申请
     */
    @PostMapping("/{refundId}/reject")
    public ResponseEntity<RefundOrder> rejectRefund(
            @PathVariable Long refundId,
            @RequestHeader("X-Operator-Id") String operatorId,
            @RequestHeader("X-Operator-Name") String operatorName,
            @RequestParam String reason) {
        return ResponseEntity.ok(refundService.rejectRefund(refundId, operatorId, operatorName, reason));
    }

    /**
     * 提交退货信息
     */
    @PostMapping("/{refundId}/return")
    public ResponseEntity<RefundOrder> submitReturnInfo(
            @PathVariable Long refundId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String logisticsCompany,
            @RequestParam String logisticsNo) {
        return ResponseEntity.ok(refundService.submitReturnInfo(refundId, userId, logisticsCompany, logisticsNo));
    }

    /**
     * 确认收货
     */
    @PostMapping("/{refundId}/receive")
    public ResponseEntity<RefundOrder> confirmReceive(
            @PathVariable Long refundId,
            @RequestHeader("X-Operator-Id") String operatorId,
            @RequestHeader("X-Operator-Name") String operatorName) {
        return ResponseEntity.ok(refundService.confirmReceive(refundId, operatorId, operatorName));
    }

    /**
     * 取消退款
     */
    @PostMapping("/{refundId}/cancel")
    public ResponseEntity<RefundOrder> cancelRefund(
            @PathVariable Long refundId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(refundService.cancelRefund(refundId, userId));
    }

    /**
     * 获取退款单详情
     */
    @GetMapping("/{refundId}")
    public ResponseEntity<RefundOrder> getRefundOrder(@PathVariable Long refundId) {
        return ResponseEntity.ok(refundService.getRefundOrder(refundId));
    }

    /**
     * 获取退款单列表
     */
    @GetMapping
    public ResponseEntity<Page<RefundOrder>> getRefundOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) RefundOrder.RefundStatus status,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            Pageable pageable) {
        return ResponseEntity.ok(refundService.getRefundOrders(userId, status, startTime, endTime, pageable));
    }
} 