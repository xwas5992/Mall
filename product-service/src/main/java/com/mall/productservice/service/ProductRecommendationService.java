

import com.mall.productservice.document.ProductDocument;
import com.mall.productservice.model.ProductRecommendation;
import com.mall.productservice.model.SearchHistory;
import com.mall.productservice.repository.ProductRecommendationRepository;
import com.mall.productservice.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final ProductRecommendationRepository recommendationRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final SearchService searchService;
    private final ProductService productService;

    @Transactional
    public void generateRecommendations(Long userId) {
        // 1. 基于搜索历史生成推荐
        generateSearchHistoryBasedRecommendations(userId);
        
        // 2. 基于热门商品生成推荐
        generatePopularBasedRecommendations(userId);
        
        // 3. 基于分类相似度生成推荐
        generateCategoryBasedRecommendations(userId);
        
        // 4. 基于品牌相似度生成推荐
        generateBrandBasedRecommendations(userId);
        
        // 5. 基于价格区间生成推荐
        generatePriceRangeBasedRecommendations(userId);
    }

    private void generateSearchHistoryBasedRecommendations(Long userId) {
        // 获取用户最近7天的搜索历史
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        List<SearchHistory> searchHistory = searchHistoryRepository.findRecentByUserId(userId, startTime);

        // 分析搜索关键词，生成推荐
        for (SearchHistory history : searchHistory) {
            if (history.getIsSuccessful() && history.getResultCount() > 0) {
                // 使用搜索服务查找相关商品
                Page<ProductDocument> products = searchService.searchProducts(
                        history.getKeyword(),
                        history.getCategory(),
                        history.getBrand(),
                        history.getMinPrice(),
                        history.getMaxPrice(),
                        Pageable.ofSize(10)
                );

                // 为每个商品生成推荐记录
                for (ProductDocument product : products) {
                    ProductRecommendation recommendation = new ProductRecommendation();
                    recommendation.setUserId(userId);
                    recommendation.setProductId(product.getId());
                    recommendation.setType(ProductRecommendation.RecommendationType.SEARCH_HISTORY);
                    recommendation.setScore(calculateSearchHistoryScore(history, product));
                    recommendation.setReason("基于您的搜索历史：" + history.getKeyword());
                    recommendationRepository.save(recommendation);
                }
            }
        }
    }

    private void generatePopularBasedRecommendations(Long userId) {
        // 获取最近30天的热门商品
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        List<Object[]> popularProducts = recommendationRepository.findPopularProducts(startTime, Pageable.ofSize(20));

        for (Object[] result : popularProducts) {
            Long productId = (Long) result[0];
            Long clickCount = (Long) result[1];

            ProductRecommendation recommendation = new ProductRecommendation();
            recommendation.setUserId(userId);
            recommendation.setProductId(productId);
            recommendation.setType(ProductRecommendation.RecommendationType.POPULAR);
            recommendation.setScore(calculatePopularityScore(clickCount));
            recommendation.setReason("热门商品推荐");
            recommendationRepository.save(recommendation);
        }
    }

    private void generateCategoryBasedRecommendations(Long userId) {
        // 获取用户最近浏览的商品分类
        List<String> userCategories = getUserRecentCategories(userId);
        
        for (String category : userCategories) {
            // 查找同分类的其他商品
            Page<ProductDocument> products = searchService.searchProducts(
                    null, category, null, null, null, Pageable.ofSize(10)
            );

            for (ProductDocument product : products) {
                ProductRecommendation recommendation = new ProductRecommendation();
                recommendation.setUserId(userId);
                recommendation.setProductId(product.getId());
                recommendation.setType(ProductRecommendation.RecommendationType.CATEGORY);
                recommendation.setScore(0.8); // 分类相似度基础分数
                recommendation.setReason("与您感兴趣的分类相似：" + category);
                recommendationRepository.save(recommendation);
            }
        }
    }

    private void generateBrandBasedRecommendations(Long userId) {
        // 获取用户最近浏览的品牌
        List<String> userBrands = getUserRecentBrands(userId);
        
        for (String brand : userBrands) {
            // 查找同品牌的其他商品
            Page<ProductDocument> products = searchService.searchProducts(
                    null, null, brand, null, null, Pageable.ofSize(10)
            );

            for (ProductDocument product : products) {
                ProductRecommendation recommendation = new ProductRecommendation();
                recommendation.setUserId(userId);
                recommendation.setProductId(product.getId());
                recommendation.setType(ProductRecommendation.RecommendationType.BRAND);
                recommendation.setScore(0.7); // 品牌相似度基础分数
                recommendation.setReason("与您感兴趣的品牌相似：" + brand);
                recommendationRepository.save(recommendation);
            }
        }
    }

    private void generatePriceRangeBasedRecommendations(Long userId) {
        // 获取用户最近浏览商品的价格区间
        Map.Entry<Double, Double> priceRange = getUserPriceRange(userId);
        if (priceRange != null) {
            // 查找价格区间内的其他商品
            Page<ProductDocument> products = searchService.searchProducts(
                    null, null, null, priceRange.getKey(), priceRange.getValue(), Pageable.ofSize(10)
            );

            for (ProductDocument product : products) {
                ProductRecommendation recommendation = new ProductRecommendation();
                recommendation.setUserId(userId);
                recommendation.setProductId(product.getId());
                recommendation.setType(ProductRecommendation.RecommendationType.PRICE_RANGE);
                recommendation.setScore(0.6); // 价格区间相似度基础分数
                recommendation.setReason("符合您的价格区间偏好");
                recommendationRepository.save(recommendation);
            }
        }
    }

    private List<String> getUserRecentCategories(Long userId) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        List<Object[]> categories = searchHistoryRepository.findPopularCategoriesByUserId(userId, startTime);
        return categories.stream()
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }

    private List<String> getUserRecentBrands(Long userId) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        List<Object[]> brands = searchHistoryRepository.findPopularBrandsByUserId(userId, startTime);
        return brands.stream()
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }

    private Map.Entry<Double, Double> getUserPriceRange(Long userId) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        List<SearchHistory> history = searchHistoryRepository.findRecentByUserId(userId, startTime);
        
        if (history.isEmpty()) {
            return null;
        }

        double minPrice = history.stream()
                .mapToDouble(h -> h.getMinPrice() != null ? h.getMinPrice() : 0)
                .min()
                .orElse(0);
        
        double maxPrice = history.stream()
                .mapToDouble(h -> h.getMaxPrice() != null ? h.getMaxPrice() : Double.MAX_VALUE)
                .max()
                .orElse(Double.MAX_VALUE);

        return new AbstractMap.SimpleEntry<>(minPrice, maxPrice);
    }

    private double calculateSearchHistoryScore(SearchHistory history, ProductDocument product) {
        double score = 0.5; // 基础分数

        // 关键词匹配加分
        if (product.getName().contains(history.getKeyword())) {
            score += 0.2;
        }

        // 分类匹配加分
        if (history.getCategory() != null && history.getCategory().equals(product.getCategory())) {
            score += 0.1;
        }

        // 品牌匹配加分
        if (history.getBrand() != null && history.getBrand().equals(product.getBrand())) {
            score += 0.1;
        }

        // 价格区间匹配加分
        if (history.getMinPrice() != null && history.getMaxPrice() != null) {
            if (product.getPrice() >= history.getMinPrice() && 
                product.getPrice() <= history.getMaxPrice()) {
                score += 0.1;
            }
        }

        return Math.min(score, 1.0);
    }

    private double calculatePopularityScore(Long clickCount) {
        // 使用对数函数计算热度分数，避免热门商品分数过高
        return Math.min(Math.log1p(clickCount) / 10, 1.0);
    }

    @Transactional(readOnly = true)
    public Page<ProductRecommendation> getRecommendations(Long userId, Pageable pageable) {
        return recommendationRepository.findByUserIdAndIsShownFalseOrderByScoreDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductRecommendation> getRecommendationsByType(
            Long userId, 
            ProductRecommendation.RecommendationType type, 
            Pageable pageable) {
        return recommendationRepository.findByUserIdAndTypeAndIsShownFalse(userId, type, pageable);
    }

    @Transactional
    public void markAsShown(Long recommendationId) {
        recommendationRepository.findById(recommendationId).ifPresent(recommendation -> {
            recommendation.setIsShown(true);
            recommendationRepository.save(recommendation);
        });
    }

    @Transactional
    public void markAsClicked(Long recommendationId) {
        recommendationRepository.findById(recommendationId).ifPresent(recommendation -> {
            recommendation.setIsClicked(true);
            recommendation.setClickedAt(LocalDateTime.now());
            recommendationRepository.save(recommendation);
        });
    }

    @Transactional
    public Map<String, Object> getRecommendationStats(int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> stats = recommendationRepository.getRecommendationStats(startTime);

        Map<String, Object> result = new HashMap<>();
        for (Object[] stat : stats) {
            ProductRecommendation.RecommendationType type = (ProductRecommendation.RecommendationType) stat[0];
            Long count = (Long) stat[1];
            Long clicks = (Long) stat[2];
            
            Map<String, Object> typeStats = new HashMap<>();
            typeStats.put("total", count);
            typeStats.put("clicks", clicks);
            typeStats.put("clickRate", count > 0 ? (double) clicks / count : 0);
            
            result.put(type.name(), typeStats);
        }

        return result;
    }

    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    @Transactional
    public void cleanOldRecommendations() {
        // 清理30天前的推荐记录
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
        recommendationRepository.deleteByUserIdAndCreatedAtBefore(null, cutoffTime);
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    @Transactional
    public void generateDailyRecommendations() {
        // 获取所有活跃用户，为他们生成新的推荐
        // TODO: 实现获取活跃用户的逻辑
        List<Long> activeUserIds = getActiveUserIds();
        for (Long userId : activeUserIds) {
            generateRecommendations(userId);
        }
    }

    private List<Long> getActiveUserIds() {
        // TODO: 实现获取活跃用户的逻辑
        // 这里应该从用户服务获取最近30天有活动的用户ID
        return Collections.emptyList();
    }
} 