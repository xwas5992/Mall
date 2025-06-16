

import com.mall.productservice.exception.ResourceNotFoundException;
import com.mall.productservice.model.Product;
import com.mall.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setDescription("测试描述");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setCategory("测试分类");
        product.setStatus(true);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("测试商品2");
        product2.setDescription("测试描述2");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setStock(50);
        product2.setCategory("测试分类");
        product2.setStatus(true);

        productList = Arrays.asList(product, product2);
    }

    @Test
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.createProduct(product);

        assertNotNull(savedProduct);
        assertEquals(product.getName(), savedProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct(1L, product);

        assertNotNull(updatedProduct);
        assertEquals(product.getName(), updatedProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_NotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(1L, product);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_NotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(1L);
        });

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void getProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProduct(1L);

        assertNotNull(foundProduct);
        assertEquals(product.getName(), foundProduct.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProduct(1L);
        });

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getAllProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void getActiveProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findByStatus(true, pageable)).thenReturn(productPage);

        Page<Product> result = productService.getActiveProducts(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findByStatus(true, pageable);
    }

    @Test
    void getProductsByCategory_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findByCategoryAndStatus("测试分类", true, pageable))
                .thenReturn(productPage);

        Page<Product> result = productService.getProductsByCategory("测试分类", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findByCategoryAndStatus("测试分类", true, pageable);
    }

    @Test
    void searchProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.searchProducts("测试", pageable)).thenReturn(productPage);

        Page<Product> result = productService.searchProducts("测试", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).searchProducts("测试", pageable);
    }

    @Test
    void getProductsByIds_Success() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(productRepository.findByIdIn(ids)).thenReturn(productList);

        List<Product> result = productService.getProductsByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findByIdIn(ids);
    }

    @Test
    void getProductsByIds_NotFound() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(productRepository.findByIdIn(ids)).thenReturn(Arrays.asList(product));

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductsByIds(ids);
        });

        verify(productRepository, times(1)).findByIdIn(ids);
    }

    @Test
    void getLowStockProducts_Success() {
        when(productRepository.findLowStockProducts(10)).thenReturn(productList);

        List<Product> result = productService.getLowStockProducts(10);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findLowStockProducts(10);
    }
} 