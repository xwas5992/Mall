

import com.mall.productservice.model.ProductStatistics;
import com.mall.productservice.service.ProductStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics/products")
@RequiredArgsConstructor
@Tag(name = "商品统计", description = "商品统计数据相关接口")
public class ProductStatisticsController {

    private final ProductStatisticsService statisticsService;

    @GetMapping("/{productId}")
    @Operation(summary = "获取商品统计", description = "获取指定商品的统计数据")
    public ResponseEntity<ProductStatistics> getProductStatistics(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        return ResponseEntity.ok(statisticsService.getProductStatistics(productId));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "获取分类统计", description = "获取指定分类下的商品统计数据")
    public ResponseEntity<Page<ProductStatistics>> getStatisticsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(statisticsService.getStatisticsByCategory(categoryId, pageable));
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "获取品牌统计", description = "获取指定品牌的商品统计数据")
    public ResponseEntity<Page<ProductStatistics>> getStatisticsByBrand(
            @Parameter(description = "品牌名称") @PathVariable String brand,
            Pageable pageable) {
        return ResponseEntity.ok(statisticsService.getStatisticsByBrand(brand, pageable));
    }

    @GetMapping("/categories")
    @Operation(summary = "获取分类统计汇总", description = "获取所有分类的统计数据汇总")
    public ResponseEntity<List<Map<String, Object>>> getCategoryStatistics() {
        return ResponseEntity.ok(statisticsService.getCategoryStatistics());
    }

    @GetMapping("/brands")
    @Operation(summary = "获取品牌统计汇总", description = "获取所有品牌的统计数据汇总")
    public ResponseEntity<List<Map<String, Object>>> getBrandStatistics() {
        return ResponseEntity.ok(statisticsService.getBrandStatistics());
    }

    @GetMapping("/top/sales")
    @Operation(summary = "获取销量排行", description = "获取指定商品列表的销量排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsBySales(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsBySales(productIds));
    }

    @GetMapping("/top/views")
    @Operation(summary = "获取浏览量排行", description = "获取指定商品列表的浏览量排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsByViews(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsByViews(productIds));
    }

    @GetMapping("/top/rating")
    @Operation(summary = "获取评分排行", description = "获取指定商品列表的评分排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsByRating(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsByRating(productIds));
    }

    @GetMapping("/top/favorites")
    @Operation(summary = "获取收藏排行", description = "获取指定商品列表的收藏排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsByFavorites(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsByFavorites(productIds));
    }

    @GetMapping("/top/reviews")
    @Operation(summary = "获取评价排行", description = "获取指定商品列表的评价排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsByReviews(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsByReviews(productIds));
    }

    @GetMapping("/top/cart")
    @Operation(summary = "获取加购排行", description = "获取指定商品列表的加购排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsByCartAdds(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsByCartAdds(productIds));
    }

    @GetMapping("/top/shares")
    @Operation(summary = "获取分享排行", description = "获取指定商品列表的分享排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsByShares(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsByShares(productIds));
    }

    @GetMapping("/top/searches")
    @Operation(summary = "获取搜索排行", description = "获取指定商品列表的搜索排行")
    public ResponseEntity<List<ProductStatistics>> getTopProductsBySearches(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> productIds) {
        return ResponseEntity.ok(statisticsService.getTopProductsBySearches(productIds));
    }

    @GetMapping("/recent/purchases")
    @Operation(summary = "获取最近购买", description = "获取指定时间后的购买记录")
    public ResponseEntity<List<ProductStatistics>> getRecentPurchases(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        return ResponseEntity.ok(statisticsService.getRecentPurchases(startTime));
    }

    @GetMapping("/recent/reviews")
    @Operation(summary = "获取最近评价", description = "获取指定时间后的评价记录")
    public ResponseEntity<List<ProductStatistics>> getRecentReviews(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        return ResponseEntity.ok(statisticsService.getRecentReviews(startTime));
    }

    @GetMapping("/recent/views")
    @Operation(summary = "获取最近浏览", description = "获取指定时间后的浏览记录")
    public ResponseEntity<List<ProductStatistics>> getRecentViews(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        return ResponseEntity.ok(statisticsService.getRecentViews(startTime));
    }

    @PostMapping("/{productId}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "重置统计数据", description = "重置指定商品的统计数据")
    public ResponseEntity<Void> resetStatistics(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.resetStatistics(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increment/view/{productId}")
    @Operation(summary = "增加浏览量", description = "增加商品的浏览量")
    public ResponseEntity<Void> incrementViewCount(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.incrementViewCount(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increment/favorite/{productId}")
    @Operation(summary = "增加收藏量", description = "增加商品的收藏量")
    public ResponseEntity<Void> incrementFavoriteCount(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.incrementFavoriteCount(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decrement/favorite/{productId}")
    @Operation(summary = "减少收藏量", description = "减少商品的收藏量")
    public ResponseEntity<Void> decrementFavoriteCount(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.decrementFavoriteCount(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increment/cart/{productId}")
    @Operation(summary = "增加加购量", description = "增加商品的加购量")
    public ResponseEntity<Void> incrementCartCount(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.incrementCartCount(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decrement/cart/{productId}")
    @Operation(summary = "减少加购量", description = "减少商品的加购量")
    public ResponseEntity<Void> decrementCartCount(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.decrementCartCount(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increment/search/{productId}")
    @Operation(summary = "增加搜索量", description = "增加商品的搜索量")
    public ResponseEntity<Void> incrementSearchCount(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.incrementSearchCount(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increment/share/{productId}")
    @Operation(summary = "增加分享量", description = "增加商品的分享量")
    public ResponseEntity<Void> incrementShareCount(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        statisticsService.incrementShareCount(productId);
        return ResponseEntity.ok().build();
    }
} 