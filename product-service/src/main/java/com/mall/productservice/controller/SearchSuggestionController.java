

import com.mall.productservice.service.SearchSuggestionCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search/suggestions")
@RequiredArgsConstructor
@Tag(name = "搜索建议", description = "商品搜索建议相关接口")
public class SearchSuggestionController {

    private final SearchSuggestionCacheService suggestionCacheService;

    @GetMapping
    @Operation(summary = "获取搜索建议", description = "根据输入前缀获取商品搜索建议")
    public ResponseEntity<List<String>> getSuggestions(
            @Parameter(description = "搜索前缀") @RequestParam String prefix,
            @Parameter(description = "建议数量") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(suggestionCacheService.getCachedSuggestions(prefix, size));
    }

    @GetMapping("/fuzzy")
    @Operation(summary = "获取模糊搜索建议", description = "根据输入前缀获取商品搜索建议（支持模糊匹配）")
    public ResponseEntity<List<String>> getSuggestionsWithFuzzy(
            @Parameter(description = "搜索前缀") @RequestParam String prefix,
            @Parameter(description = "建议数量") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(suggestionCacheService.getCachedFuzzySuggestions(prefix, size));
    }

    @DeleteMapping("/cache")
    @Operation(summary = "清除建议缓存", description = "清除所有搜索建议缓存")
    public ResponseEntity<Void> clearSuggestionsCache() {
        suggestionCacheService.clearSuggestionsCache();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cache/{prefix}")
    @Operation(summary = "清除指定前缀的建议缓存", description = "清除指定前缀的搜索建议缓存")
    public ResponseEntity<Void> clearSuggestionsCacheForPrefix(
            @Parameter(description = "搜索前缀") @PathVariable String prefix) {
        suggestionCacheService.clearSuggestionsCacheForPrefix(prefix);
        suggestionCacheService.clearFuzzySuggestionsCacheForPrefix(prefix);
        return ResponseEntity.ok().build();
    }
} 