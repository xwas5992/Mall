

import com.mall.orderservice.model.RefundOrder;
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
public interface RefundOrderRepository extends JpaRepository<RefundOrder, Long> {
    
    // 根据退款单号查询
    Optional<RefundOrder> findByRefundNo(String refundNo);
    
    // 根据订单ID查询
    List<RefundOrder> findByOrderId(Long orderId);
    
    // 根据订单编号查询
    List<RefundOrder> findByOrderNo(String orderNo);
    
    // 根据用户ID查询
    Page<RefundOrder> findByUserId(Long userId, Pageable pageable);
    
    // 根据退款状态查询
    Page<RefundOrder> findByStatus(RefundOrder.RefundStatus status, Pageable pageable);
    
    // 根据用户ID和退款状态查询
    Page<RefundOrder> findByUserIdAndStatus(Long userId, RefundOrder.RefundStatus status, Pageable pageable);
    
    // 根据退款类型查询
    Page<RefundOrder> findByRefundType(RefundOrder.RefundType refundType, Pageable pageable);
    
    // 根据创建时间范围查询
    Page<RefundOrder> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // 查询待审核的退款申请
    @Query("SELECT r FROM RefundOrder r WHERE r.status = 'PENDING_APPROVE' AND r.createdAt <= :checkTime")
    List<RefundOrder> findPendingApproveRefunds(@Param("checkTime") LocalDateTime checkTime);
    
    // 查询待退货的退款申请
    @Query("SELECT r FROM RefundOrder r WHERE r.status = 'PENDING_RETURN' AND r.approveTime <= :checkTime")
    List<RefundOrder> findPendingReturnRefunds(@Param("checkTime") LocalDateTime checkTime);
    
    // 查询待收货的退款申请
    @Query("SELECT r FROM RefundOrder r WHERE r.status = 'PENDING_RECEIVE' AND r.returnTime <= :checkTime")
    List<RefundOrder> findPendingReceiveRefunds(@Param("checkTime") LocalDateTime checkTime);
    
    // 查询退款中的申请
    @Query("SELECT r FROM RefundOrder r WHERE r.status = 'REFUNDING' AND r.receiveTime <= :checkTime")
    List<RefundOrder> findRefundingOrders(@Param("checkTime") LocalDateTime checkTime);
    
    // 统计用户退款订单数量
    @Query("SELECT COUNT(r) FROM RefundOrder r WHERE r.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // 统计各状态退款订单数量
    @Query("SELECT r.status, COUNT(r) FROM RefundOrder r GROUP BY r.status")
    List<Object[]> countByStatus();
    
    // 统计时间段内的退款订单数量
    @Query("SELECT COUNT(r) FROM RefundOrder r WHERE r.createdAt BETWEEN :startTime AND :endTime")
    long countByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 统计时间段内的退款金额
    @Query("SELECT SUM(r.refundAmount) FROM RefundOrder r WHERE r.createdAt BETWEEN :startTime AND :endTime")
    BigDecimal sumAmountByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 检查退款单是否存在
    boolean existsByRefundNo(String refundNo);
    
    // 检查订单是否有未完成的退款申请
    @Query("SELECT COUNT(r) > 0 FROM RefundOrder r WHERE r.orderId = :orderId AND r.status IN ('PENDING_APPROVE', 'APPROVED', 'PENDING_RETURN', 'RETURNED', 'PENDING_RECEIVE', 'REFUNDING')")
    boolean existsUnfinishedRefund(@Param("orderId") Long orderId);
} 