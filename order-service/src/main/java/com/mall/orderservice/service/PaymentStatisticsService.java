

import com.mall.orderservice.model.Order;
import com.mall.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatisticsService {

    private final OrderRepository orderRepository;

    /**
     * 获取今日支付统计
     */
    @Cacheable(value = "paymentStatistics", key = "'today'")
    public Map<String, Object> getTodayStatistics() {
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return getStatisticsByTimeRange(today, LocalDateTime.now());
    }

    /**
     * 获取本周支付统计
     */
    @Cacheable(value = "paymentStatistics", key = "'week'")
    public Map<String, Object> getWeekStatistics() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
                .with(LocalTime.MIN);
        return getStatisticsByTimeRange(weekStart, LocalDateTime.now());
    }

    /**
     * 获取本月支付统计
     */
    @Cacheable(value = "paymentStatistics", key = "'month'")
    public Map<String, Object> getMonthStatistics() {
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
        return getStatisticsByTimeRange(monthStart, LocalDateTime.now());
    }

    /**
     * 获取指定时间范围的支付统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatisticsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取支付统计 - 开始时间: {}, 结束时间: {}", startTime, endTime);

        Map<String, Object> statistics = new HashMap<>();

        // 1. 支付订单数
        long paidOrderCount = orderRepository.countByStatusAndCreatedAtBetween(
            Order.OrderStatus.PAID, startTime, endTime);
        statistics.put("paidOrderCount", paidOrderCount);

        // 2. 支付总金额
        BigDecimal totalAmount = orderRepository.sumTotalAmountByStatusAndCreatedAtBetween(
            Order.OrderStatus.PAID, startTime, endTime);
        statistics.put("totalAmount", totalAmount);

        // 3. 退款订单数
        long refundOrderCount = orderRepository.countByStatusAndCreatedAtBetween(
            Order.OrderStatus.REFUNDED, startTime, endTime);
        statistics.put("refundOrderCount", refundOrderCount);

        // 4. 退款总金额
        BigDecimal refundAmount = orderRepository.sumRefundAmountByStatusAndCreatedAtBetween(
            Order.OrderStatus.REFUNDED, startTime, endTime);
        statistics.put("refundAmount", refundAmount);

        // 5. 净收入（支付总额 - 退款总额）
        BigDecimal netAmount = totalAmount.subtract(refundAmount != null ? refundAmount : BigDecimal.ZERO);
        statistics.put("netAmount", netAmount);

        // 6. 支付成功率
        long totalOrderCount = orderRepository.countByCreatedAtBetween(startTime, endTime);
        double successRate = totalOrderCount > 0 ? 
            (double) paidOrderCount / totalOrderCount * 100 : 0;
        statistics.put("successRate", String.format("%.2f%%", successRate));

        // 7. 平均订单金额
        BigDecimal averageOrderAmount = paidOrderCount > 0 ? 
            totalAmount.divide(BigDecimal.valueOf(paidOrderCount), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
        statistics.put("averageOrderAmount", averageOrderAmount);

        // 8. 退款率
        double refundRate = paidOrderCount > 0 ? 
            (double) refundOrderCount / paidOrderCount * 100 : 0;
        statistics.put("refundRate", String.format("%.2f%%", refundRate));

        log.info("支付统计结果: {}", statistics);
        return statistics;
    }

    /**
     * 获取用户支付统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics(Long userId) {
        log.info("获取用户支付统计 - 用户ID: {}", userId);

        Map<String, Object> statistics = new HashMap<>();

        // 1. 用户订单总数
        long totalOrderCount = orderRepository.countByUserId(userId);
        statistics.put("totalOrderCount", totalOrderCount);

        // 2. 用户支付订单数
        long paidOrderCount = orderRepository.countByUserIdAndStatus(userId, Order.OrderStatus.PAID);
        statistics.put("paidOrderCount", paidOrderCount);

        // 3. 用户支付总金额
        BigDecimal totalAmount = orderRepository.sumTotalAmountByUserIdAndStatus(userId, Order.OrderStatus.PAID);
        statistics.put("totalAmount", totalAmount);

        // 4. 用户退款订单数
        long refundOrderCount = orderRepository.countByUserIdAndStatus(userId, Order.OrderStatus.REFUNDED);
        statistics.put("refundOrderCount", refundOrderCount);

        // 5. 用户退款总金额
        BigDecimal refundAmount = orderRepository.sumRefundAmountByUserIdAndStatus(userId, Order.OrderStatus.REFUNDED);
        statistics.put("refundAmount", refundAmount);

        // 6. 用户净支出
        BigDecimal netAmount = totalAmount.subtract(refundAmount != null ? refundAmount : BigDecimal.ZERO);
        statistics.put("netAmount", netAmount);

        // 7. 用户平均订单金额
        BigDecimal averageOrderAmount = paidOrderCount > 0 ? 
            totalAmount.divide(BigDecimal.valueOf(paidOrderCount), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
        statistics.put("averageOrderAmount", averageOrderAmount);

        log.info("用户支付统计结果: {}", statistics);
        return statistics;
    }
} 