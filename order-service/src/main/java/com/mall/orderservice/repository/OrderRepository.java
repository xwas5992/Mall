import com.mall.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // 根据订单编号查询
    Optional<Order> findByOrderNo(String orderNo);
    
    // 根据用户ID查询订单列表
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    // 根据订单状态查询
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    // 根据用户ID和订单状态查询
    Page<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable);
    
    // 根据支付状态查询
    Page<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus, Pageable pageable);
    
    // 根据创建时间范围查询
    Page<Order> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // 根据支付时间范围查询
    Page<Order> findByPayTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // 查询待支付超时订单
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.createdAt < :timeoutTime")
    List<Order> findTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    // 查询待收货超时订单
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_RECEIVE' AND o.deliveryTime < :timeoutTime")
    List<Order> findDeliveryTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    // 根据订单编号模糊查询
    Page<Order> findByOrderNoContaining(String orderNo, Pageable pageable);
    
    // 根据收货人姓名或电话查询
    Page<Order> findByReceiverNameContainingOrReceiverPhoneContaining(
            String receiverName, String receiverPhone, Pageable pageable);
    
    // 统计用户订单数量
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // 统计各状态订单数量
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();
    
    // 统计时间段内的订单数量
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startTime AND :endTime")
    long countByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 统计时间段内的订单金额
    @Query("SELECT SUM(o.payAmount) FROM Order o WHERE o.createdAt BETWEEN :startTime AND :endTime")
    BigDecimal sumAmountByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 检查订单是否存在
    boolean existsByOrderNo(String orderNo);
    
    // 检查用户是否有未完成的订单
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.userId = :userId AND o.status IN ('PENDING_PAYMENT', 'PENDING_DELIVERY', 'PENDING_RECEIVE')")
    boolean existsUnfinishedOrder(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.paymentStatus = 'UNPAID' " +
           "AND o.createdAt <= :checkTime")
    List<Order> findPendingPaymentOrders(@Param("checkTime") LocalDateTime checkTime);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.paymentStatus = 'UNPAID' " +
           "AND o.createdAt <= :timeoutTime")
    List<Order> findTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);

    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED' AND o.updatedAt <= :timeoutTime")
    List<Order> findDeliveryTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.userId = :userId " +
           "AND o.status NOT IN ('COMPLETED', 'CANCELLED', 'REFUNDED')")
    boolean existsUnfinishedOrder(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status " +
           "AND o.createdAt BETWEEN :startTime AND :endTime")
    Page<Order> findByUserIdAndStatusAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("status") Order.OrderStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 统计指定状态和时间范围内的订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startTime AND :endTime")
    long countByStatusAndCreatedAtBetween(
        @Param("status") Order.OrderStatus status,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定状态和时间范围内的订单总金额
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startTime AND :endTime")
    BigDecimal sumTotalAmountByStatusAndCreatedAtBetween(
        @Param("status") Order.OrderStatus status,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定状态和时间范围内的退款总金额
     */
    @Query("SELECT COALESCE(SUM(o.refundAmount), 0) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startTime AND :endTime")
    BigDecimal sumRefundAmountByStatusAndCreatedAtBetween(
        @Param("status") Order.OrderStatus status,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内的订单总数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startTime AND :endTime")
    long countByCreatedAtBetween(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定用户和状态的订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    long countByUserIdAndStatus(
        @Param("userId") Long userId,
        @Param("status") Order.OrderStatus status);

    /**
     * 统计指定用户和状态的订单总金额
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    BigDecimal sumTotalAmountByUserIdAndStatus(
        @Param("userId") Long userId,
        @Param("status") Order.OrderStatus status);

    /**
     * 统计指定用户和状态的退款总金额
     */
    @Query("SELECT COALESCE(SUM(o.refundAmount), 0) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    BigDecimal sumRefundAmountByUserIdAndStatus(
        @Param("userId") Long userId,
        @Param("status") Order.OrderStatus status);
} 