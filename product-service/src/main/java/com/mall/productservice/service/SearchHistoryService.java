

import com.mall.productservice.model.SearchHistory;
import com.mall.productservice.repository.SearchHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    // 缓存相关常量
    private static final String CACHE_NAME_SEARCH_HISTORY = "searchHistory";
    private static final String CACHE_NAME_POPULAR_KEYWORDS = "popularKeywords";
    private static final String CACHE_NAME_POPULAR_CATEGORIES = "popularCategories";
    private static final String CACHE_NAME_POPULAR_BRANDS = "popularBrands";
    private static final int DEFAULT_CACHE_DAYS = 7;

    @Transactional
    public SearchHistory recordSearch(Long userId, String keyword, String category, String brand,
                                    Double minPrice, Double maxPrice, Integer resultCount,
                                    HttpServletRequest request) {
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword);
        history.setCategory(category);
        history.setBrand(brand);
        history.setMinPrice(minPrice);
        history.setMaxPrice(maxPrice);
        history.setResultCount(resultCount);
        history.setIpAddress(getClientIp(request));
        history.setUserAgent(request.getHeader("User-Agent"));
        history.setIsSuccessful(true);

        // 保存后清除相关缓存
        clearUserSearchHistoryCache(userId);
        return searchHistoryRepository.save(history);
    }

    @Transactional
    public void recordFailedSearch(Long userId, String keyword, String errorMessage,
                                 HttpServletRequest request) {
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword);
        history.setResultCount(0);
        history.setIpAddress(getClientIp(request));
        history.setUserAgent(request.getHeader("User-Agent"));
        history.setIsSuccessful(false);
        history.setErrorMessage(errorMessage);

        // 保存后清除相关缓存
        clearUserSearchHistoryCache(userId);
        searchHistoryRepository.save(history);
    }

    @Cacheable(value = CACHE_NAME_SEARCH_HISTORY, key = "#userId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<SearchHistory> getUserSearchHistory(Long userId, Pageable pageable) {
        return searchHistoryRepository.findByUserId(userId, pageable);
    }

    @Cacheable(value = CACHE_NAME_SEARCH_HISTORY, key = "#userId + '_recent_' + #days")
    public List<SearchHistory> getRecentSearches(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return searchHistoryRepository.findRecentByUserId(userId, startTime);
    }

    @Cacheable(value = CACHE_NAME_POPULAR_KEYWORDS, key = "#userId + '_' + #days")
    public List<Map.Entry<String, Long>> getPopularKeywords(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return searchHistoryRepository.findPopularKeywordsByUserId(userId, startTime)
                .stream()
                .map(result -> Map.entry((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

    @Cacheable(value = CACHE_NAME_POPULAR_CATEGORIES, key = "#userId + '_' + #days")
    public List<Map.Entry<String, Long>> getPopularCategories(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return searchHistoryRepository.findPopularCategoriesByUserId(userId, startTime)
                .stream()
                .map(result -> Map.entry((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

    @Cacheable(value = CACHE_NAME_POPULAR_BRANDS, key = "#userId + '_' + #days")
    public List<Map.Entry<String, Long>> getPopularBrands(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return searchHistoryRepository.findPopularBrandsByUserId(userId, startTime)
                .stream()
                .map(result -> Map.entry((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {CACHE_NAME_SEARCH_HISTORY, CACHE_NAME_POPULAR_KEYWORDS, 
                        CACHE_NAME_POPULAR_CATEGORIES, CACHE_NAME_POPULAR_BRANDS}, 
                allEntries = true)
    public void clearOldHistory(Long userId, int days) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
        searchHistoryRepository.deleteByUserIdAndCreatedAtBefore(userId, cutoffTime);
    }

    @Cacheable(value = CACHE_NAME_SEARCH_HISTORY, key = "#userId + '_count_' + #days")
    public long getSearchCount(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return searchHistoryRepository.countByUserIdAndTimeRange(userId, startTime);
    }

    // 定时任务：每天凌晨3点清理30天前的搜索历史
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    @CacheEvict(value = {CACHE_NAME_SEARCH_HISTORY, CACHE_NAME_POPULAR_KEYWORDS, 
                        CACHE_NAME_POPULAR_CATEGORIES, CACHE_NAME_POPULAR_BRANDS}, 
                allEntries = true)
    public void scheduledCleanup() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
        searchHistoryRepository.deleteByUserIdAndCreatedAtBefore(null, cutoffTime);
    }

    // 定时任务：每小时更新热门搜索缓存
    @Scheduled(cron = "0 0 * * * ?")
    @CacheEvict(value = {CACHE_NAME_POPULAR_KEYWORDS, CACHE_NAME_POPULAR_CATEGORIES, 
                        CACHE_NAME_POPULAR_BRANDS}, 
                allEntries = true)
    public void updatePopularSearchesCache() {
        // 触发缓存更新
        // 实际的数据会在下次查询时重新加载
    }

    // 清除指定用户的所有搜索历史缓存
    @CacheEvict(value = {CACHE_NAME_SEARCH_HISTORY, CACHE_NAME_POPULAR_KEYWORDS, 
                        CACHE_NAME_POPULAR_CATEGORIES, CACHE_NAME_POPULAR_BRANDS}, 
                key = "#userId + '*'")
    public void clearUserSearchHistoryCache(Long userId) {
        // 方法体可以为空，注解会处理缓存清除
    }

    // 清除所有搜索历史缓存
    @CacheEvict(value = {CACHE_NAME_SEARCH_HISTORY, CACHE_NAME_POPULAR_KEYWORDS, 
                        CACHE_NAME_POPULAR_CATEGORIES, CACHE_NAME_POPULAR_BRANDS}, 
                allEntries = true)
    public void clearAllSearchHistoryCache() {
        // 方法体可以为空，注解会处理缓存清除
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 