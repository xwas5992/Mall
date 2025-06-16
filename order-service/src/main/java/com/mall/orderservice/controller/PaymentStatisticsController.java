

import com.mall.orderservice.service.PaymentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment/statistics")
@RequiredArgsConstructor
public class PaymentStatisticsController {

    private final PaymentStatisticsService paymentStatisticsService;

    /**
     * 获取今日支付统计
     */
    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayStatistics() {
        return ResponseEntity.ok(paymentStatisticsService.getTodayStatistics());
    }

    /**
     * 获取本周支付统计
     */
    @GetMapping("/week")
    public ResponseEntity<Map<String, Object>> getWeekStatistics() {
        return ResponseEntity.ok(paymentStatisticsService.getWeekStatistics());
    }

    /**
     * 获取本月支付统计
     */
    @GetMapping("/month")
    public ResponseEntity<Map<String, Object>> getMonthStatistics() {
        return ResponseEntity.ok(paymentStatisticsService.getMonthStatistics());
    }

    /**
     * 获取指定时间范围的支付统计
     */
    @GetMapping("/range")
    public ResponseEntity<Map<String, Object>> getStatisticsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(paymentStatisticsService.getStatisticsByTimeRange(startTime, endTime));
    }

    /**
     * 获取用户支付统计
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentStatisticsService.getUserStatistics(userId));
    }
} 