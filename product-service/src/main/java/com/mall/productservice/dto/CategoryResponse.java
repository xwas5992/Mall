

import com.mall.productservice.model.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Integer level;
    private Integer sortOrder;
    private Boolean isVisible;
    private String iconUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryResponse> children;

    public static CategoryResponse fromCategory(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setParentId(category.getParentId());
        response.setLevel(category.getLevel());
        response.setSortOrder(category.getSortOrder());
        response.setIsVisible(category.getIsVisible());
        response.setIconUrl(category.getIconUrl());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            response.setChildren(category.getChildren().stream()
                    .map(CategoryResponse::fromCategory)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
} 