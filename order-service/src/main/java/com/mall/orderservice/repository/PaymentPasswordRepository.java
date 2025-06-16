

import com.mall.orderservice.model.PaymentPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentPasswordRepository extends JpaRepository<PaymentPassword, Long> {
    
    Optional<PaymentPassword> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    void deleteByUserId(Long userId);
} 