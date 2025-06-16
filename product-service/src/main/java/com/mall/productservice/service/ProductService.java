

import com.mall.productservice.dto.ProductRequest;
import com.mall.productservice.exception.ResourceNotFoundException;
import com.mall.productservice.model.Category;
import com.mall.productservice.model.Product;
import com.mall.productservice.repository.CategoryRepository;
import com.mall.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SearchService searchService;

    @Transactional
    public Product createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus());

        Product savedProduct = productRepository.save(product);
        // 同步到搜索索引
        searchService.indexProduct(savedProduct);
        return savedProduct;
    }

    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        Product updatedProduct = productRepository.save(product);
        // 同步到搜索索引
        searchService.updateIndex(updatedProduct);
        return updatedProduct;
    }

    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
        // 从搜索索引中删除
        searchService.removeFromIndex(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id")
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrand(brand, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByStatus(ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }

    @Transactional
    public void updateStock(Long id, Integer quantity) {
        Product product = getProduct(id);
        product.setStock(product.getStock() + quantity);
        Product updatedProduct = productRepository.save(product);
        // 同步到搜索索引
        searchService.updateIndex(updatedProduct);
    }

    @Transactional
    public void updateStatus(Long id, ProductStatus status) {
        Product product = getProduct(id);
        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);
        // 同步到搜索索引
        searchService.updateIndex(updatedProduct);
    }

    @Transactional
    public void reindexAllProducts() {
        // 重新索引所有商品
        searchService.indexProducts(productRepository.findAll());
    }
} 