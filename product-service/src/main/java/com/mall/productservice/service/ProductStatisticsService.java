

import com.mall.productservice.model.Product;
import com.mall.productservice.model.ProductStatistics;
import com.mall.productservice.repository.ProductRepository;
import com.mall.productservice.repository.ProductStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductStatisticsService {

    private final ProductStatisticsRepository statisticsRepository;
    private final ProductRepository productRepository;

    // 缓存相关常量
    private static final String CACHE_NAME_STATISTICS = "productStatistics";
    private static final String CACHE_NAME_CATEGORY_STATS = "categoryStatistics";
    private static final String CACHE_NAME_BRAND_STATS = "brandStatistics";

    @Transactional
    public void incrementViewCount(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setViewCount(statistics.getViewCount() + 1);
        statistics.setLastViewTime(LocalDateTime.now());
        statisticsRepository.save(statistics);
    }

    @Transactional
    public void incrementFavoriteCount(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setFavoriteCount(statistics.getFavoriteCount() + 1);
        statisticsRepository.save(statistics);
    }

    @Transactional
    public void decrementFavoriteCount(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        if (statistics.getFavoriteCount() > 0) {
            statistics.setFavoriteCount(statistics.getFavoriteCount() - 1);
            statisticsRepository.save(statistics);
        }
    }

    @Transactional
    public void incrementCartCount(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setCartCount(statistics.getCartCount() + 1);
        statisticsRepository.save(statistics);
    }

    @Transactional
    public void decrementCartCount(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        if (statistics.getCartCount() > 0) {
            statistics.setCartCount(statistics.getCartCount() - 1);
            statisticsRepository.save(statistics);
        }
    }

    @Transactional
    public void recordPurchase(Long productId, Integer quantity, BigDecimal price) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setPurchaseCount(statistics.getPurchaseCount() + quantity);
        statistics.setTotalSales(statistics.getTotalSales().add(price.multiply(BigDecimal.valueOf(quantity))));
        statistics.setLastPurchaseTime(LocalDateTime.now());
        statisticsRepository.save(statistics);
    }

    @Transactional
    public void recordReview(Long productId, BigDecimal rating) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setReviewCount(statistics.getReviewCount() + 1);
        
        // 更新平均评分
        BigDecimal totalRating = statistics.getAverageRating()
                .multiply(BigDecimal.valueOf(statistics.getReviewCount() - 1))
                .add(rating);
        statistics.setAverageRating(totalRating.divide(BigDecimal.valueOf(statistics.getReviewCount()), 2, RoundingMode.HALF_UP));
        
        statistics.setLastReviewTime(LocalDateTime.now());
        statisticsRepository.save(statistics);
    }

    @Transactional
    public void incrementSearchCount(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setSearchCount(statistics.getSearchCount() + 1);
        statisticsRepository.save(statistics);
    }

    @Transactional
    public void incrementShareCount(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setShareCount(statistics.getShareCount() + 1);
        statisticsRepository.save(statistics);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_STATISTICS, key = "#productId")
    public ProductStatistics getProductStatistics(Long productId) {
        return statisticsRepository.getByProductId(productId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_STATISTICS, key = "'category_' + #categoryId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<ProductStatistics> getStatisticsByCategory(Long categoryId, Pageable pageable) {
        return statisticsRepository.findByCategoryId(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_STATISTICS, key = "'brand_' + #brand + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<ProductStatistics> getStatisticsByBrand(String brand, Pageable pageable) {
        return statisticsRepository.findByBrand(brand, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_CATEGORY_STATS)
    public List<Map<String, Object>> getCategoryStatistics() {
        return statisticsRepository.getCategoryStatistics();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_BRAND_STATS)
    public List<Map<String, Object>> getBrandStatistics() {
        return statisticsRepository.getBrandStatistics();
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsBySales(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderBySalesDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsByViews(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderByViewsDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsByRating(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderByRatingDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsByFavorites(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderByFavoritesDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsByReviews(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderByReviewsDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsByCartAdds(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderByCartAddsDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsByShares(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderBySharesDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getTopProductsBySearches(List<Long> productIds) {
        return statisticsRepository.findByProductIdsOrderBySearchesDesc(productIds);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getRecentPurchases(LocalDateTime startTime) {
        return statisticsRepository.findByLastPurchaseTimeAfter(startTime);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getRecentReviews(LocalDateTime startTime) {
        return statisticsRepository.findByLastReviewTimeAfter(startTime);
    }

    @Transactional(readOnly = true)
    public List<ProductStatistics> getRecentViews(LocalDateTime startTime) {
        return statisticsRepository.findByLastViewTimeAfter(startTime);
    }

    @Transactional
    @CacheEvict(value = {CACHE_NAME_STATISTICS, CACHE_NAME_CATEGORY_STATS, CACHE_NAME_BRAND_STATS}, allEntries = true)
    public void resetStatistics(Long productId) {
        ProductStatistics statistics = getOrCreateStatistics(productId);
        statistics.setViewCount(0L);
        statistics.setFavoriteCount(0L);
        statistics.setCartCount(0L);
        statistics.setPurchaseCount(0L);
        statistics.setReviewCount(0L);
        statistics.setAverageRating(BigDecimal.ZERO);
        statistics.setSearchCount(0L);
        statistics.setShareCount(0L);
        statistics.setTotalSales(BigDecimal.ZERO);
        statisticsRepository.save(statistics);
    }

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    @Transactional
    @CacheEvict(value = {CACHE_NAME_STATISTICS, CACHE_NAME_CATEGORY_STATS, CACHE_NAME_BRAND_STATS}, allEntries = true)
    public void cleanupOldStatistics() {
        // 清理30天前的统计数据
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<ProductStatistics> oldStatistics = statisticsRepository.findByLastViewTimeAfter(thirtyDaysAgo);
        for (ProductStatistics statistics : oldStatistics) {
            if (statistics.getLastViewTime().isBefore(thirtyDaysAgo) &&
                statistics.getLastPurchaseTime().isBefore(thirtyDaysAgo) &&
                statistics.getLastReviewTime().isBefore(thirtyDaysAgo)) {
                resetStatistics(statistics.getProduct().getId());
            }
        }
    }

    private ProductStatistics getOrCreateStatistics(Long productId) {
        return Optional.ofNullable(statisticsRepository.findByProductId(productId))
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                    ProductStatistics statistics = new ProductStatistics();
                    statistics.setProduct(product);
                    statistics.setLastViewTime(LocalDateTime.now());
                    statistics.setLastPurchaseTime(LocalDateTime.now());
                    statistics.setLastReviewTime(LocalDateTime.now());
                    return statisticsRepository.save(statistics);
                });
    }
} 