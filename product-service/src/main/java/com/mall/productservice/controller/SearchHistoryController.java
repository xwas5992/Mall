

import com.mall.productservice.model.SearchHistory;
import com.mall.productservice.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search/history")
@RequiredArgsConstructor
@Tag(name = "搜索历史", description = "搜索历史相关接口")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping
    @Operation(summary = "获取用户搜索历史", description = "分页获取用户的搜索历史记录")
    public ResponseEntity<Page<SearchHistory>> getUserSearchHistory(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(searchHistoryService.getUserSearchHistory(userId, pageable));
    }

    @GetMapping("/recent")
    @Operation(summary = "获取最近搜索记录", description = "获取用户最近几天的搜索记录")
    public ResponseEntity<List<SearchHistory>> getRecentSearches(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "天数") @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(searchHistoryService.getRecentSearches(userId, days));
    }

    @GetMapping("/popular/keywords")
    @Operation(summary = "获取热门搜索关键词", description = "获取用户最近几天的热门搜索关键词")
    public ResponseEntity<List<Map.Entry<String, Long>>> getPopularKeywords(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "天数") @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(searchHistoryService.getPopularKeywords(userId, days));
    }

    @GetMapping("/popular/categories")
    @Operation(summary = "获取热门搜索分类", description = "获取用户最近几天的热门搜索分类")
    public ResponseEntity<List<Map.Entry<String, Long>>> getPopularCategories(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "天数") @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(searchHistoryService.getPopularCategories(userId, days));
    }

    @GetMapping("/popular/brands")
    @Operation(summary = "获取热门搜索品牌", description = "获取用户最近几天的热门搜索品牌")
    public ResponseEntity<List<Map.Entry<String, Long>>> getPopularBrands(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "天数") @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(searchHistoryService.getPopularBrands(userId, days));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清除历史记录", description = "清除用户指定天数前的搜索历史记录")
    public ResponseEntity<Void> clearOldHistory(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "天数") @RequestParam(defaultValue = "30") int days) {
        searchHistoryService.clearOldHistory(userId, days);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    @Operation(summary = "获取搜索次数", description = "获取用户最近几天的搜索次数")
    public ResponseEntity<Long> getSearchCount(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "天数") @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(searchHistoryService.getSearchCount(userId, days));
    }
} 