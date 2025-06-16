

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    @Size(min = 2, max = 100, message = "商品名称长度必须在2-100个字符之间")
    private String name;

    @Size(max = 1000, message = "商品描述不能超过1000个字符")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格必须大于等于0")
    private BigDecimal price;

    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存必须大于等于0")
    private Integer stock;

    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;

    @NotBlank(message = "商品分类不能为空")
    @Size(max = 50, message = "商品分类长度不能超过50个字符")
    private String category;

    private Boolean status = true;
} 