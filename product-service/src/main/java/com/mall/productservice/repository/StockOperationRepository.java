

import com.mall.productservice.model.StockOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockOperationRepository extends JpaRepository<StockOperation, Long> {
    List<StockOperation> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    List<StockOperation> findByOrderId(String orderId);
    
    @Query("SELECT SUM(so.quantity) FROM StockOperation so WHERE so.product.id = ?1 AND so.type = ?2")
    Integer sumQuantityByProductIdAndType(Long productId, StockOperation.OperationType type);
    
    boolean existsByOrderIdAndType(String orderId, StockOperation.OperationType type);
} 