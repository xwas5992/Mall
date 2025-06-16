

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.productservice.dto.ProductRequest;
import com.mall.productservice.dto.ProductResponse;
import com.mall.productservice.exception.ResourceNotFoundException;
import com.mall.productservice.mapper.ProductMapper;
import com.mall.productservice.model.Product;
import com.mall.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private List<Product> productList;
    private List<ProductResponse> productResponseList;

    @BeforeEach
    void setUp() {
        // 设置测试数据
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setDescription("测试描述");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setCategory("测试分类");
        product.setStatus(true);

        productRequest = new ProductRequest();
        productRequest.setName("测试商品");
        productRequest.setDescription("测试描述");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setStock(100);
        productRequest.setCategory("测试分类");
        productRequest.setStatus(true);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("测试商品");
        productResponse.setDescription("测试描述");
        productResponse.setPrice(new BigDecimal("99.99"));
        productResponse.setStock(100);
        productResponse.setCategory("测试分类");
        productResponse.setStatus(true);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("测试商品2");
        product2.setDescription("测试描述2");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setStock(50);
        product2.setCategory("测试分类");
        product2.setStatus(true);

        productList = Arrays.asList(product, product2);

        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setId(2L);
        productResponse2.setName("测试商品2");
        productResponse2.setDescription("测试描述2");
        productResponse2.setPrice(new BigDecimal("199.99"));
        productResponse2.setStock(50);
        productResponse2.setCategory("测试分类");
        productResponse2.setStatus(true);

        productResponseList = Arrays.asList(productResponse, productResponse2);
    }

    @Test
    void createProduct_Success() throws Exception {
        when(productMapper.toEntity(any(ProductRequest.class))).thenReturn(product);
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("测试商品")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.stock", is(100)));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void updateProduct_Success() throws Exception {
        when(productService.getProduct(1L)).thenReturn(product);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("测试商品")));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_NotFound() throws Exception {
        when(productService.getProduct(1L)).thenThrow(new ResourceNotFoundException("Product", "id", 1L));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound());

        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void getProduct_Success() throws Exception {
        when(productService.getProduct(1L)).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("测试商品")));

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void getProduct_NotFound() throws Exception {
        when(productService.getProduct(1L)).thenThrow(new ResourceNotFoundException("Product", "id", 1L));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void getAllProducts_Success() throws Exception {
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(0, 10), productList.size());
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(productPage);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(productResponse)
                .thenReturn(productResponseList.get(1));

        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("测试商品")))
                .andExpect(jsonPath("$.content[1].name", is("测试商品2")));

        verify(productService, times(1)).getAllProducts(any(PageRequest.class));
    }

    @Test
    void getActiveProducts_Success() throws Exception {
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(0, 10), productList.size());
        when(productService.getActiveProducts(any(PageRequest.class))).thenReturn(productPage);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(productResponse)
                .thenReturn(productResponseList.get(1));

        mockMvc.perform(get("/api/products/active")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("测试商品")))
                .andExpect(jsonPath("$.content[1].name", is("测试商品2")));

        verify(productService, times(1)).getActiveProducts(any(PageRequest.class));
    }

    @Test
    void getProductsByCategory_Success() throws Exception {
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(0, 10), productList.size());
        when(productService.getProductsByCategory(eq("测试分类"), any(PageRequest.class))).thenReturn(productPage);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(productResponse)
                .thenReturn(productResponseList.get(1));

        mockMvc.perform(get("/api/products/category/测试分类")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("测试商品")))
                .andExpect(jsonPath("$.content[1].name", is("测试商品2")));

        verify(productService, times(1)).getProductsByCategory(eq("测试分类"), any(PageRequest.class));
    }

    @Test
    void searchProducts_Success() throws Exception {
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(0, 10), productList.size());
        when(productService.searchProducts(eq("测试"), any(PageRequest.class))).thenReturn(productPage);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(productResponse)
                .thenReturn(productResponseList.get(1));

        mockMvc.perform(get("/api/products/search")
                .param("keyword", "测试")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("测试商品")))
                .andExpect(jsonPath("$.content[1].name", is("测试商品2")));

        verify(productService, times(1)).searchProducts(eq("测试"), any(PageRequest.class));
    }

    @Test
    void getProductsByIds_Success() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(productService.getProductsByIds(ids)).thenReturn(productList);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(productResponse)
                .thenReturn(productResponseList.get(1));

        mockMvc.perform(post("/api/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("测试商品")))
                .andExpect(jsonPath("$[1].name", is("测试商品2")));

        verify(productService, times(1)).getProductsByIds(ids);
    }

    @Test
    void getLowStockProducts_Success() throws Exception {
        when(productService.getLowStockProducts(10)).thenReturn(productList);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(productResponse)
                .thenReturn(productResponseList.get(1));

        mockMvc.perform(get("/api/products/low-stock")
                .param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("测试商品")))
                .andExpect(jsonPath("$[1].name", is("测试商品2")));

        verify(productService, times(1)).getLowStockProducts(10);
    }
} 