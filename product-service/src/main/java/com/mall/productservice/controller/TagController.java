

import com.mall.productservice.model.Tag;
import com.mall.productservice.service.TagService;
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
import java.util.Set;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "商品标签的增删改查接口")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @Operation(summary = "创建标签", description = "创建一个新的商品标签")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.createTag(tag));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新标签", description = "更新指定ID的标签信息")
    public ResponseEntity<Tag> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.updateTag(id, tag));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "删除指定ID的标签")
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取标签", description = "获取指定ID的标签详情")
    public ResponseEntity<Tag> getTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "按类型获取标签", description = "获取指定类型的所有标签")
    public ResponseEntity<Page<Tag>> getTagsByType(
            @Parameter(description = "标签类型") @PathVariable Tag.TagType type,
            Pageable pageable) {
        return ResponseEntity.ok(tagService.getTagsByType(type, pageable));
    }

    @GetMapping("/type/{type}/active")
    @Operation(summary = "获取活跃标签", description = "获取指定类型的活跃标签列表")
    public ResponseEntity<List<Tag>> getActiveTagsByType(
            @Parameter(description = "标签类型") @PathVariable Tag.TagType type) {
        return ResponseEntity.ok(tagService.getActiveTagsByType(type));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态获取标签", description = "获取指定状态的所有标签")
    public ResponseEntity<Page<Tag>> getTagsByStatus(
            @Parameter(description = "标签状态") @PathVariable Boolean status,
            Pageable pageable) {
        return ResponseEntity.ok(tagService.getTagsByStatus(status, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索标签", description = "根据关键词搜索标签")
    public ResponseEntity<Page<Tag>> searchTags(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(tagService.searchTags(keyword, pageable));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "获取商品标签", description = "获取指定商品的所有标签")
    public ResponseEntity<Set<Tag>> getTagsByProductId(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        return ResponseEntity.ok(tagService.getTagsByProductId(productId));
    }

    @GetMapping("/product/{productId}/type/{type}")
    @Operation(summary = "获取商品特定类型标签", description = "获取指定商品的特定类型活跃标签")
    public ResponseEntity<Set<Tag>> getActiveTagsByTypeAndProductId(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "标签类型") @PathVariable Tag.TagType type) {
        return ResponseEntity.ok(tagService.getActiveTagsByTypeAndProductId(productId, type));
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门标签", description = "获取使用频率最高的标签")
    public ResponseEntity<Page<Map.Entry<Tag, Long>>> getPopularTags(Pageable pageable) {
        return ResponseEntity.ok(tagService.getPopularTags(pageable));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取标签统计", description = "获取各类型标签的数量统计")
    public ResponseEntity<Map<Tag.TagType, Long>> getTagTypeStats() {
        return ResponseEntity.ok(tagService.getTagTypeStats());
    }

    @GetMapping("/{tagId}/related")
    @Operation(summary = "获取相关标签", description = "获取与指定标签相关的其他标签")
    public ResponseEntity<List<Tag>> getRelatedTags(
            @Parameter(description = "标签ID") @PathVariable Long tagId,
            @Parameter(description = "标签类型") @RequestParam Tag.TagType type,
            Pageable pageable) {
        return ResponseEntity.ok(tagService.getRelatedTags(tagId, type, pageable));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新标签状态", description = "更新指定标签的启用状态")
    public ResponseEntity<Void> updateTagStatus(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Parameter(description = "标签状态") @RequestParam Boolean status) {
        tagService.updateTagStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/sort")
    @Operation(summary = "更新标签排序", description = "更新指定标签的排序值")
    public ResponseEntity<Void> updateTagSortOrder(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Parameter(description = "排序值") @RequestParam Integer sortOrder) {
        tagService.updateTagSortOrder(id, sortOrder);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/batch/status")
    @Operation(summary = "批量更新标签状态", description = "批量更新多个标签的启用状态")
    public ResponseEntity<Void> batchUpdateTagStatus(
            @Parameter(description = "标签ID列表") @RequestBody Set<Long> tagIds,
            @Parameter(description = "标签状态") @RequestParam Boolean status) {
        tagService.batchUpdateTagStatus(tagIds, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除标签", description = "批量删除多个未使用的标签")
    public ResponseEntity<Void> batchDeleteTags(
            @Parameter(description = "标签ID列表") @RequestBody Set<Long> tagIds) {
        tagService.batchDeleteTags(tagIds);
        return ResponseEntity.ok().build();
    }
} 