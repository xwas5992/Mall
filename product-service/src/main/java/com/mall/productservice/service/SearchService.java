import com.mall.productservice.document.ProductDocument;
import com.mall.productservice.document.ProductSuggestion;
import com.mall.productservice.model.Product;
import com.mall.productservice.repository.elasticsearch.ProductSearchRepository;
import com.mall.productservice.repository.elasticsearch.ProductSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductSuggestionRepository productSuggestionRepository;
    private final ProductService productService;
    private final SearchSuggestionCacheService suggestionCacheService;
    private final SearchHistoryService searchHistoryService;
    private final HttpServletRequest request;

    @Transactional(readOnly = true)
    public Page<ProductDocument> search(String keyword, Pageable pageable) {
        return productSearchRepository.search(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductDocument> searchByCategory(String keyword, Long categoryId, Pageable pageable) {
        return productSearchRepository.searchByCategory(keyword, categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductDocument> searchByBrand(String keyword, String brand, Pageable pageable) {
        return productSearchRepository.searchByBrand(keyword, brand, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductDocument> searchByPriceRange(String keyword, BigDecimal minPrice, 
                                                  BigDecimal maxPrice, Pageable pageable) {
        return productSearchRepository.searchByPriceRange(keyword, minPrice, maxPrice, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductDocument> searchWithFilters(String keyword, Long categoryId, String brand,
                                                 BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productSearchRepository.searchWithFilters(keyword, categoryId, brand, minPrice, maxPrice, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> getSuggestions(String prefix, int size) {
        SearchHits<ProductSuggestion> suggestions = productSuggestionRepository.suggest(prefix, size);
        return suggestions.getSearchHits().stream()
                .map(hit -> hit.getContent().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getSuggestionsWithFuzzy(String prefix, int size) {
        SearchHits<ProductSuggestion> suggestions = productSuggestionRepository.suggestWithFuzzy(prefix, size);
        return suggestions.getSearchHits().stream()
                .map(hit -> hit.getContent().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public void indexProduct(Product product) {
        ProductDocument document = convertToDocument(product);
        productSearchRepository.save(document);
        productSuggestionRepository.save(ProductSuggestion.fromProduct(document));
        clearRelatedSuggestionsCache(document);
    }

    @Transactional
    public void indexProducts(List<Product> products) {
        List<ProductDocument> documents = products.stream()
                .map(this::convertToDocument)
                .toList();
        productSearchRepository.saveAll(documents);
        List<ProductSuggestion> suggestions = documents.stream()
                .map(ProductSuggestion::fromProduct)
                .toList();
        productSuggestionRepository.saveAll(suggestions);
        suggestionCacheService.clearSuggestionsCache();
    }

    @Transactional
    public void removeFromIndex(Long productId) {
        ProductDocument document = productSearchRepository.findById(productId).orElse(null);
        if (document != null) {
            clearRelatedSuggestionsCache(document);
        }
        productSearchRepository.deleteById(productId);
        productSuggestionRepository.deleteById(productId);
    }

    @Transactional
    public void updateIndex(Product product) {
        indexProduct(product);
    }

    private void clearRelatedSuggestionsCache(ProductDocument document) {
        String[] words = document.getName().split("\\s+");
        for (String word : words) {
            if (word.length() >= 2) {
                suggestionCacheService.clearSuggestionsCacheForPrefix(word);
                suggestionCacheService.clearFuzzySuggestionsCacheForPrefix(word);
            }
        }
        if (document.getBrand() != null && document.getBrand().length() >= 2) {
            suggestionCacheService.clearSuggestionsCacheForPrefix(document.getBrand());
            suggestionCacheService.clearFuzzySuggestionsCacheForPrefix(document.getBrand());
        }
        if (document.getCategoryName() != null && document.getCategoryName().length() >= 2) {
            suggestionCacheService.clearSuggestionsCacheForPrefix(document.getCategoryName());
            suggestionCacheService.clearFuzzySuggestionsCacheForPrefix(document.getCategoryName());
        }
    }

    private ProductDocument convertToDocument(Product product) {
        ProductDocument document = new ProductDocument();
        document.setId(product.getId());
        document.setName(product.getName());
        document.setDescription(product.getDescription());
        document.setCategoryId(product.getCategory().getId());
        document.setCategoryName(product.getCategory().getName());
        document.setBrand(product.getBrand());
        document.setPrice(product.getPrice());
        document.setStock(product.getStock());
        document.setStatus(product.getStatus().name());
        document.setCreatedAt(product.getCreatedAt());
        document.setUpdatedAt(product.getUpdatedAt());
        return document;
    }

    public Page<ProductDocument> searchProducts(String keyword, String category, String brand,
                                              Double minPrice, Double maxPrice, Pageable pageable) {
        try {
            Page<ProductDocument> results = productSearchRepository.search(keyword, category, brand,
                    minPrice, maxPrice, pageable);

            // 记录搜索历史
            searchHistoryService.recordSearch(
                    getCurrentUserId(), // 需要实现获取当前用户ID的方法
                    keyword,
                    category,
                    brand,
                    minPrice,
                    maxPrice,
                    (int) results.getTotalElements(),
                    request
            );

            return results;
        } catch (Exception e) {
            // 记录失败的搜索
            searchHistoryService.recordFailedSearch(
                    getCurrentUserId(),
                    keyword,
                    e.getMessage(),
                    request
            );
            throw e;
        }
    }

    private Long getCurrentUserId() {
        // TODO: 从安全上下文中获取当前用户ID
        // 这里需要根据实际的安全实现来获取用户ID
        // 临时返回null，实际使用时需要实现
        return null;
    }
} 