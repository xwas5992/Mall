

import com.mall.productservice.exception.TagException;
import com.mall.productservice.model.Tag;
import com.mall.productservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    // 缓存相关常量
    private static final String CACHE_NAME_TAGS = "tags";
    private static final String CACHE_NAME_POPULAR_TAGS = "popularTags";
    private static final String CACHE_NAME_TAG_STATS = "tagStats";

    @Transactional
    public Tag createTag(Tag tag) {
        if (tagRepository.existsByName(tag.getName())) {
            throw new TagException("标签名称已存在");
        }
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(Long id, Tag tag) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new TagException("标签不存在"));

        // 检查名称是否重复（排除自身）
        if (!existingTag.getName().equals(tag.getName()) && 
            tagRepository.existsByName(tag.getName())) {
            throw new TagException("标签名称已存在");
        }

        existingTag.setName(tag.getName());
        existingTag.setDescription(tag.getDescription());
        existingTag.setType(tag.getType());
        existingTag.setStatus(tag.getStatus());
        existingTag.setSortOrder(tag.getSortOrder());
        existingTag.setIconUrl(tag.getIconUrl());
        existingTag.setColor(tag.getColor());
        existingTag.setUpdatedBy(tag.getUpdatedBy());

        return tagRepository.save(existingTag);
    }

    @Transactional
    @CacheEvict(value = {CACHE_NAME_TAGS, CACHE_NAME_POPULAR_TAGS, CACHE_NAME_TAG_STATS}, allEntries = true)
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagException("标签不存在"));
        
        // 检查标签是否被使用
        if (!tag.getProducts().isEmpty()) {
            throw new TagException("标签正在使用中，无法删除");
        }
        
        tagRepository.delete(tag);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "#id")
    public Tag getTag(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagException("标签不存在"));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "'type_' + #type + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Tag> getTagsByType(Tag.TagType type, Pageable pageable) {
        return tagRepository.findByType(type, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "'active_type_' + #type")
    public List<Tag> getActiveTagsByType(Tag.TagType type) {
        return tagRepository.findActiveByType(type);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "'status_' + #status + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Tag> getTagsByStatus(Boolean status, Pageable pageable) {
        return tagRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "'search_' + #keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Tag> searchTags(String keyword, Pageable pageable) {
        return tagRepository.search(keyword, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "'product_' + #productId")
    public Set<Tag> getTagsByProductId(Long productId) {
        return tagRepository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "'product_type_' + #productId + '_' + #type")
    public Set<Tag> getActiveTagsByTypeAndProductId(Long productId, Tag.TagType type) {
        return tagRepository.findActiveByTypeAndProductId(type, productId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_POPULAR_TAGS, key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Map.Entry<Tag, Long>> getPopularTags(Pageable pageable) {
        return tagRepository.findPopularTags(pageable)
                .map(result -> Map.entry((Tag) result[0], (Long) result[1]));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAG_STATS)
    public Map<Tag.TagType, Long> getTagTypeStats() {
        return tagRepository.getTagTypeStats().stream()
                .collect(Collectors.toMap(
                        result -> (Tag.TagType) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_NAME_TAGS, key = "'related_' + #tagId + '_' + #type + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public List<Tag> getRelatedTags(Long tagId, Tag.TagType type, Pageable pageable) {
        return tagRepository.findRelatedTags(tagId, type, pageable);
    }

    @Transactional
    @CacheEvict(value = {CACHE_NAME_TAGS, CACHE_NAME_POPULAR_TAGS, CACHE_NAME_TAG_STATS}, allEntries = true)
    public void updateTagStatus(Long id, Boolean status) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagException("标签不存在"));
        tag.setStatus(status);
        tagRepository.save(tag);
    }

    @Transactional
    @CacheEvict(value = {CACHE_NAME_TAGS, CACHE_NAME_POPULAR_TAGS, CACHE_NAME_TAG_STATS}, allEntries = true)
    public void updateTagSortOrder(Long id, Integer sortOrder) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagException("标签不存在"));
        tag.setSortOrder(sortOrder);
        tagRepository.save(tag);
    }

    @Transactional
    @CacheEvict(value = {CACHE_NAME_TAGS, CACHE_NAME_POPULAR_TAGS, CACHE_NAME_TAG_STATS}, allEntries = true)
    public void batchUpdateTagStatus(Set<Long> tagIds, Boolean status) {
        tagRepository.findByIds(tagIds).forEach(tag -> {
            tag.setStatus(status);
            tagRepository.save(tag);
        });
    }

    @Transactional
    @CacheEvict(value = {CACHE_NAME_TAGS, CACHE_NAME_POPULAR_TAGS, CACHE_NAME_TAG_STATS}, allEntries = true)
    public void batchDeleteTags(Set<Long> tagIds) {
        tagRepository.findByIds(tagIds).forEach(tag -> {
            if (tag.getProducts().isEmpty()) {
                tagRepository.delete(tag);
            }
        });
    }
} 