

import com.mall.productservice.document.ProductSuggestion;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSuggestionRepository extends ElasticsearchRepository<ProductSuggestion, Long> {

    @Query("{\"suggest\": {\"product-suggest\": {\"prefix\": \"?0\", \"completion\": {\"field\": \"suggest\", \"size\": ?1, \"skip_duplicates\": true}}}}")
    SearchHits<ProductSuggestion> suggest(String prefix, int size);

    @Query("{\"suggest\": {\"product-suggest\": {\"prefix\": \"?0\", \"completion\": {\"field\": \"suggest\", \"size\": ?1, \"skip_duplicates\": true, \"fuzzy\": {\"fuzziness\": \"AUTO\"}}}}}")
    SearchHits<ProductSuggestion> suggestWithFuzzy(String prefix, int size);
} 