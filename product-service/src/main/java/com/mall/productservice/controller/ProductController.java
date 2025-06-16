
import com.mall.productservice.dto.ProductRequest;
import com.mall.productservice.dto.ProductResponse;
import com.mall.productservice.exception.ResourceNotFoundException;
import com.mall.productservice.mapper.ProductMapper;
import com.mall.productservice.model.Product;
import com.mall.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Tag(name = "商品管理", description = "商品相关的所有接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @PostMapping
    @Operation(summary = "创建商品", description = "创建一个新的商品")
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "商品信息") @Valid @RequestBody ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productService.createProduct(product);
        return new ResponseEntity<>(productMapper.toResponse(savedProduct), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品", description = "根据ID更新商品信息")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Parameter(description = "商品信息") @Valid @RequestBody ProductRequest request) {
        Product existingProduct = productService.getProduct(id);
        productMapper.updateEntityFromRequest(existingProduct, request);
        Product updatedProduct = productService.updateProduct(id, existingProduct);
        return ResponseEntity.ok(productMapper.toResponse(updatedProduct));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "根据ID删除商品")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品详细信息")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @GetMapping
    @Operation(summary = "获取商品列表", description = "分页获取商品列表")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Product> products = productService.getAllProducts(pageRequest);
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "获取上架商品", description = "分页获取所有上架商品")
    public ResponseEntity<Page<ProductResponse>> getActiveProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.getActiveProducts(PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "获取分类商品", description = "根据分类获取商品列表")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @Parameter(description = "商品分类") @PathVariable String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.getProductsByCategory(category, PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.searchProducts(keyword, PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(productMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    @Operation(summary = "批量获取商品", description = "根据ID列表批量获取商品")
    public ResponseEntity<List<ProductResponse>> getProductsByIds(
            @Parameter(description = "商品ID列表") @RequestBody List<Long> ids) {
        List<Product> products = productService.getProductsByIds(ids);
        List<ProductResponse> response = products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "获取低库存商品", description = "获取库存低于阈值的商品")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @Parameter(description = "库存阈值") @RequestParam(defaultValue = "10") Integer threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        List<ProductResponse> response = products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
} 