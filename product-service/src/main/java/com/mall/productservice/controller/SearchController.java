

import com.mall.productservice.document.ProductDocument;
import com.mall.productservice.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "商品搜索", description = "商品搜索相关接口")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    public ResponseEntity<Page<ProductDocument>> search(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(searchService.search(keyword, pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "按分类搜索", description = "在指定分类下搜索商品")
    public ResponseEntity<Page<ProductDocument>> searchByCategory(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByCategory(keyword, categoryId, pageable));
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "按品牌搜索", description = "搜索指定品牌的商品")
    public ResponseEntity<Page<ProductDocument>> searchByBrand(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "品牌名称") @PathVariable String brand,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByBrand(keyword, brand, pageable));
    }

    @GetMapping("/price")
    @Operation(summary = "按价格区间搜索", description = "在指定价格区间内搜索商品")
    public ResponseEntity<Page<ProductDocument>> searchByPriceRange(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "最低价格") @RequestParam BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam BigDecimal maxPrice,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchByPriceRange(keyword, minPrice, maxPrice, pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "多条件搜索", description = "使用多个条件组合搜索商品")
    public ResponseEntity<Page<ProductDocument>> searchWithFilters(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "品牌名称") @RequestParam(required = false) String brand,
            @Parameter(description = "最低价格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchWithFilters(
                keyword, categoryId, brand, minPrice, maxPrice, pageable));
    }
} 