

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserService {
    
    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    Map<String, Object> getUser(@PathVariable("userId") Long userId);
    
    /**
     * 检查用户是否存在
     */
    @GetMapping("/{userId}/exists")
    boolean existsById(@PathVariable("userId") Long userId);
    
    /**
     * 获取用户地址信息
     */
    @GetMapping("/{userId}/addresses/{addressId}")
    Map<String, Object> getUserAddress(@PathVariable("userId") Long userId, 
            @PathVariable("addressId") Long addressId);
    
    /**
     * 获取用户默认地址
     */
    @GetMapping("/{userId}/addresses/default")
    Map<String, Object> getDefaultAddress(@PathVariable("userId") Long userId);
    
    /**
     * 获取用户积分信息
     */
    @GetMapping("/{userId}/points")
    Map<String, Object> getUserPoints(@PathVariable("userId") Long userId);
    
    /**
     * 扣减用户积分
     */
    @PostMapping("/{userId}/points/decrease")
    void decreasePoints(@PathVariable("userId") Long userId, 
            @RequestParam("points") Integer points,
            @RequestParam("orderNo") String orderNo);
    
    /**
     * 增加用户积分
     */
    @PostMapping("/{userId}/points/increase")
    void increasePoints(@PathVariable("userId") Long userId, 
            @RequestParam("points") Integer points,
            @RequestParam("orderNo") String orderNo);
    
    /**
     * 获取用户优惠券列表
     */
    @GetMapping("/{userId}/coupons")
    Map<String, Object> getUserCoupons(@PathVariable("userId") Long userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);
    
    /**
     * 使用优惠券
     */
    @PostMapping("/{userId}/coupons/{couponId}/use")
    void useCoupon(@PathVariable("userId") Long userId,
            @PathVariable("couponId") Long couponId,
            @RequestParam("orderNo") String orderNo);
    
    /**
     * 退还优惠券
     */
    @PostMapping("/{userId}/coupons/{couponId}/return")
    void returnCoupon(@PathVariable("userId") Long userId,
            @PathVariable("couponId") Long couponId,
            @RequestParam("orderNo") String orderNo);
} 