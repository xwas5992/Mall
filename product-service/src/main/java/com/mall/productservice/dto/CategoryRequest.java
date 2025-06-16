

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;

    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    private String description;

    private Long parentId;

    private Integer sortOrder;

    private Boolean isVisible;

    @Size(max = 200, message = "图标URL长度不能超过200个字符")
    private String iconUrl;
} 