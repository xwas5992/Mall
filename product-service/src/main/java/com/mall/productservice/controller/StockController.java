

import com.mall.productservice.model.StockOperation;
import com.mall.productservice.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "库存管理", description = "商品库存相关的所有接口")
public class StockController {

    private final StockService stockService;

    @PostMapping("/{productId}/lock")
    @Operation(summary = "锁定库存", description = "为订单锁定商品库存")
    public ResponseEntity<Void> lockStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody LockStockRequest request) {
        stockService.lockStock(productId, request.getQuantity(), request.getOrderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/unlock")
    @Operation(summary = "释放库存", description = "释放订单锁定的商品库存")
    public ResponseEntity<Void> unlockStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody UnlockStockRequest request) {
        stockService.unlockStock(productId, request.getOrderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/deduct")
    @Operation(summary = "扣减库存", description = "扣减订单锁定的商品库存")
    public ResponseEntity<Void> deductStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody DeductStockRequest request) {
        stockService.deductStock(productId, request.getOrderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/add")
    @Operation(summary = "增加库存", description = "增加商品库存")
    public ResponseEntity<Void> addStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody AddStockRequest request) {
        stockService.addStock(productId, request.getQuantity(), request.getRemark());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/adjust")
    @Operation(summary = "调整库存", description = "调整商品库存到指定数量")
    public ResponseEntity<Void> adjustStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody AdjustStockRequest request) {
        stockService.adjustStock(productId, request.getNewStock(), request.getRemark());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/operations")
    @Operation(summary = "获取库存操作记录", description = "获取商品的库存操作记录")
    public ResponseEntity<List<StockOperation>> getStockOperations(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStockOperations(productId));
    }

    @Data
    public static class LockStockRequest {
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量必须大于0")
        private Integer quantity;

        @NotBlank(message = "订单ID不能为空")
        private String orderId;
    }

    @Data
    public static class UnlockStockRequest {
        @NotBlank(message = "订单ID不能为空")
        private String orderId;
    }

    @Data
    public static class DeductStockRequest {
        @NotBlank(message = "订单ID不能为空")
        private String orderId;
    }

    @Data
    public static class AddStockRequest {
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量必须大于0")
        private Integer quantity;

        private String remark;
    }

    @Data
    public static class AdjustStockRequest {
        @NotNull(message = "新库存数量不能为空")
        @Min(value = 0, message = "库存数量不能小于0")
        private Integer newStock;

        private String remark;
    }
} 