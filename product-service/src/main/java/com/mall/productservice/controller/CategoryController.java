

import com.mall.productservice.dto.CategoryRequest;
import com.mall.productservice.dto.CategoryResponse;
import com.mall.productservice.model.Category;
import com.mall.productservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "商品分类的CRUD接口")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建分类", description = "创建一个新的商品分类")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.createCategory(request);
        return ResponseEntity.ok(CategoryResponse.fromCategory(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新分类", description = "更新指定ID的分类信息")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(CategoryResponse.fromCategory(category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除分类", description = "删除指定ID的分类")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        List<Category> categories = categoryService.getCategoryTree();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "获取所有分类", description = "获取所有分类的列表")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "获取指定ID的分类详情")
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        return ResponseEntity.ok(CategoryResponse.fromCategory(category));
    }

    @GetMapping("/{parentId}/children")
    @Operation(summary = "获取子分类", description = "获取指定父分类下的所有子分类")
    public ResponseEntity<List<CategoryResponse>> getChildren(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        List<Category> categories = categoryService.getChildren(parentId);
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/root")
    @Operation(summary = "获取根分类", description = "获取所有一级分类")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/sort")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新分类排序", description = "更新指定分类的排序值")
    public ResponseEntity<Void> updateSortOrder(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "新的排序值") @RequestParam Integer sortOrder) {
        categoryService.updateSortOrder(id, sortOrder);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/visibility")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新分类可见性", description = "更新指定分类的可见性状态")
    public ResponseEntity<Void> updateVisibility(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "是否可见") @RequestParam Boolean isVisible) {
        categoryService.updateVisibility(id, isVisible);
        return ResponseEntity.ok().build();
    }
} 