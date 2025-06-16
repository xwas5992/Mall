

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.suggest.Completion;

@Data
@Document(indexName = "#{@elasticsearchConfig.getProductSuggestionIndexName()}")
public class ProductSuggestion {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @CompletionField(analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private Completion suggest;

    public static ProductSuggestion fromProduct(ProductDocument product) {
        ProductSuggestion suggestion = new ProductSuggestion();
        suggestion.setId(product.getId());
        suggestion.setName(product.getName());
        suggestion.setBrand(product.getBrand());
        suggestion.setCategoryName(product.getCategoryName());
        
        // 构建建议字段，包含商品名称、品牌和分类
        Completion completion = new Completion(new String[]{
            product.getName(),
            product.getBrand(),
            product.getCategoryName()
        });
        suggestion.setSuggest(completion);
        
        return suggestion;
    }
} 