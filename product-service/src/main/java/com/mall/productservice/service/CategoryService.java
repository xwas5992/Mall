

import com.mall.productservice.dto.CategoryRequest;
import com.mall.productservice.exception.CategoryException;
import com.mall.productservice.exception.ResourceNotFoundException;
import com.mall.productservice.model.Category;
import com.mall.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private static final int MAX_LEVEL = 3;

    @Transactional
    @CacheEvict(value = {"categoryTree", "categoryList"}, allEntries = true)
    public Category createCategory(CategoryRequest request) {
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
            
            if (parent.getLevel() >= MAX_LEVEL) {
                throw new CategoryException("分类层级不能超过" + MAX_LEVEL + "级");
            }
        }

        if (categoryRepository.existsByNameAndParentId(request.getName(), request.getParentId())) {
            throw new CategoryException("同级分类下已存在相同名称的分类");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setLevel(request.getParentId() == null ? 1 : 
                categoryRepository.findById(request.getParentId()).get().getLevel() + 1);
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setIsVisible(request.getIsVisible() != null ? request.getIsVisible() : true);
        category.setIconUrl(request.getIconUrl());

        return categoryRepository.save(category);
    }

    @Transactional
    @CacheEvict(value = {"categoryTree", "categoryList"}, allEntries = true)
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (request.getParentId() != null && !request.getParentId().equals(category.getParentId())) {
            Category newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
            
            if (newParent.getLevel() >= MAX_LEVEL) {
                throw new CategoryException("分类层级不能超过" + MAX_LEVEL + "级");
            }

            if (categoryRepository.existsByNameAndParentId(request.getName(), request.getParentId())) {
                throw new CategoryException("同级分类下已存在相同名称的分类");
            }

            category.setParentId(request.getParentId());
            category.setLevel(newParent.getLevel() + 1);
        } else if (request.getName() != null && !request.getName().equals(category.getName()) &&
                categoryRepository.existsByNameAndParentId(request.getName(), category.getParentId())) {
            throw new CategoryException("同级分类下已存在相同名称的分类");
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        if (request.getIsVisible() != null) {
            category.setIsVisible(request.getIsVisible());
        }
        if (request.getIconUrl() != null) {
            category.setIconUrl(request.getIconUrl());
        }

        return categoryRepository.save(category);
    }

    @Transactional
    @CacheEvict(value = {"categoryTree", "categoryList"}, allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (categoryRepository.countChildren(id) > 0) {
            throw new CategoryException("该分类下还有子分类，无法删除");
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryTree")
    public List<Category> getCategoryTree() {
        List<Category> allCategories = categoryRepository.findByIsVisibleTrueOrderBySortOrderAsc();
        Map<Long, List<Category>> parentIdToChildren = allCategories.stream()
                .filter(category -> category.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));

        allCategories.forEach(category -> 
            category.setChildren(parentIdToChildren.getOrDefault(category.getId(), new ArrayList<>())));

        return allCategories.stream()
                .filter(category -> category.getParentId() == null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryList")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Category> getChildren(Long parentId) {
        return categoryRepository.findByParentIdAndIsVisibleTrueOrderBySortOrderAsc(parentId);
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    @Transactional
    @CacheEvict(value = {"categoryTree", "categoryList"}, allEntries = true)
    public void updateSortOrder(Long id, Integer newSortOrder) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setSortOrder(newSortOrder);
        categoryRepository.save(category);
    }

    @Transactional
    @CacheEvict(value = {"categoryTree", "categoryList"}, allEntries = true)
    public void updateVisibility(Long id, Boolean isVisible) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setIsVisible(isVisible);
        categoryRepository.save(category);
    }
} 