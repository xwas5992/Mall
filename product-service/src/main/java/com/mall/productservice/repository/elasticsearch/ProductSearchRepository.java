

import com.mall.productservice.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\"], \"type\": \"best_fields\"}}], \"filter\": [{\"term\": {\"status\": \"ACTIVE\"}}]}}")
    Page<ProductDocument> search(String keyword, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\"], \"type\": \"best_fields\"}}], \"filter\": [{\"term\": {\"status\": \"ACTIVE\"}}, {\"term\": {\"categoryId\": ?1}}]}}")
    Page<ProductDocument> searchByCategory(String keyword, Long categoryId, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\"], \"type\": \"best_fields\"}}], \"filter\": [{\"term\": {\"status\": \"ACTIVE\"}}, {\"term\": {\"brand\": ?1}}]}}")
    Page<ProductDocument> searchByBrand(String keyword, String brand, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\"], \"type\": \"best_fields\"}}], \"filter\": [{\"term\": {\"status\": \"ACTIVE\"}}, {\"range\": {\"price\": {\"gte\": ?1, \"lte\": ?2}}}]}}")
    Page<ProductDocument> searchByPriceRange(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\"], \"type\": \"best_fields\"}}], \"filter\": [{\"term\": {\"status\": \"ACTIVE\"}}, {\"term\": {\"categoryId\": ?1}}, {\"term\": {\"brand\": ?2}}, {\"range\": {\"price\": {\"gte\": ?3, \"lte\": ?4}}}]}}")
    Page<ProductDocument> searchWithFilters(String keyword, Long categoryId, String brand, 
                                          BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<ProductDocument> findByCategoryId(Long categoryId);

    List<ProductDocument> findByBrand(String brand);

    @Query("{\"bool\": {\"must\": [{\"term\": {\"status\": \"ACTIVE\"}}], \"filter\": [{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}]}}")
    Page<ProductDocument> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
} 