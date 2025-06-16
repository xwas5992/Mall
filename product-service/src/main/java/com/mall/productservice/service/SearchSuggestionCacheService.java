

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SearchSuggestionCacheService {

    private final SearchService searchService;
    private static final String CACHE_KEY_PREFIX = "suggestion:";
    private static final String FUZZY_CACHE_KEY_PREFIX = "suggestion:fuzzy:";

    @Cacheable(value = "suggestions", key = "#prefix + ':' + #size", unless = "#result == null || #result.isEmpty()")
    public List<String> getCachedSuggestions(String prefix, int size) {
        return searchService.getSuggestions(prefix, size);
    }

    @Cacheable(value = "suggestions", key = "'fuzzy:' + #prefix + ':' + #size", unless = "#result == null || #result.isEmpty()")
    public List<String> getCachedFuzzySuggestions(String prefix, int size) {
        return searchService.getSuggestionsWithFuzzy(prefix, size);
    }

    @CacheEvict(value = "suggestions", allEntries = true)
    public void clearSuggestionsCache() {
        // 清除所有建议缓存
    }

    @CacheEvict(value = "suggestions", key = "#prefix + ':*'")
    public void clearSuggestionsCacheForPrefix(String prefix) {
        // 清除指定前缀的建议缓存
    }

    @CacheEvict(value = "suggestions", key = "'fuzzy:' + #prefix + ':*'")
    public void clearFuzzySuggestionsCacheForPrefix(String prefix) {
        // 清除指定前缀的模糊建议缓存
    }

    public String generateCacheKey(String prefix, int size) {
        return CACHE_KEY_PREFIX + prefix + ":" + size;
    }

    public String generateFuzzyCacheKey(String prefix, int size) {
        return FUZZY_CACHE_KEY_PREFIX + prefix + ":" + size;
    }
} 