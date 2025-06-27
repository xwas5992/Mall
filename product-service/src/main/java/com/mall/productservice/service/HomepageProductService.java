package com.mall.productservice.service;

import com.mall.productservice.dto.HomepageProductRequest;
import com.mall.productservice.model.Product;
import com.mall.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomepageProductService {

    private final ProductRepository productRepository;

    /**
     * 获取首页展示的商品列表
     */
    public List<Product> getHomepageProducts() {
        try {
            log.info("获取首页展示商品列表");
            List<Product> products = productRepository.findByFeaturedOnHomepageTrueOrderByHomepageSortOrderAsc();
            log.info("获取到 {} 个首页商品", products.size());
            return products;
        } catch (Exception e) {
            log.error("获取首页商品失败", e);
            throw new RuntimeException("获取首页商品失败: " + e.getMessage());
        }
    }

    /**
     * 设置商品是否在首页展示
     */
    @Transactional
    public Product setProductHomepageStatus(Long productId, Boolean featuredOnHomepage) {
        try {
            log.info("设置商品 {} 首页展示状态为: {}", productId, featuredOnHomepage);
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("商品不存在");
            }

            Product product = productOpt.get();
            product.setFeaturedOnHomepage(featuredOnHomepage);
            
            // 如果设置为首页展示且没有排序，设置默认排序
            if (featuredOnHomepage && product.getHomepageSortOrder() == null) {
                Integer maxOrder = productRepository.findMaxHomepageSortOrder();
                product.setHomepageSortOrder(maxOrder != null ? maxOrder + 1 : 1);
            }

            Product savedProduct = productRepository.save(product);
            log.info("商品 {} 首页展示状态设置成功", productId);
            return savedProduct;
        } catch (Exception e) {
            log.error("设置商品首页展示状态失败", e);
            throw new RuntimeException("设置商品首页展示状态失败: " + e.getMessage());
        }
    }

    /**
     * 更新首页商品排序
     */
    @Transactional
    public Product updateHomepageSortOrder(Long productId, Integer sortOrder) {
        try {
            log.info("更新商品 {} 排序为: {}", productId, sortOrder);
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("商品不存在");
            }

            Product product = productOpt.get();
            if (!product.getFeaturedOnHomepage()) {
                throw new RuntimeException("该商品未设置为首页展示");
            }

            product.setHomepageSortOrder(sortOrder);
            Product savedProduct = productRepository.save(product);
            log.info("商品 {} 排序更新成功", productId);
            return savedProduct;
        } catch (Exception e) {
            log.error("更新首页商品排序失败", e);
            throw new RuntimeException("更新首页商品排序失败: " + e.getMessage());
        }
    }

    /**
     * 更新首页商品显示信息
     */
    @Transactional
    public Product updateHomepageDisplayInfo(HomepageProductRequest request) {
        try {
            log.info("更新商品 {} 首页显示信息", request.getProductId());
            Optional<Product> productOpt = productRepository.findById(request.getProductId());
            if (productOpt.isEmpty()) {
                throw new RuntimeException("商品不存在");
            }

            Product product = productOpt.get();
            
            if (request.getFeaturedOnHomepage() != null) {
                product.setFeaturedOnHomepage(request.getFeaturedOnHomepage());
            }
            
            if (request.getHomepageSortOrder() != null) {
                product.setHomepageSortOrder(request.getHomepageSortOrder());
            }
            
            if (request.getHomepageDisplayTitle() != null) {
                product.setHomepageDisplayTitle(request.getHomepageDisplayTitle());
            }
            
            if (request.getHomepageDisplayDescription() != null) {
                product.setHomepageDisplayDescription(request.getHomepageDisplayDescription());
            }

            Product savedProduct = productRepository.save(product);
            log.info("商品 {} 首页显示信息更新成功", request.getProductId());
            return savedProduct;
        } catch (Exception e) {
            log.error("更新首页商品显示信息失败", e);
            throw new RuntimeException("更新首页商品显示信息失败: " + e.getMessage());
        }
    }

    /**
     * 批量设置首页商品
     */
    @Transactional
    public List<Product> batchSetHomepageProducts(List<HomepageProductRequest> requests) {
        try {
            log.info("批量设置 {} 个商品的首页展示信息", requests.size());
            List<Product> updatedProducts = new java.util.ArrayList<>();
            
            for (HomepageProductRequest request : requests) {
                try {
                    Product product = updateHomepageDisplayInfo(request);
                    updatedProducts.add(product);
                } catch (Exception e) {
                    log.error("更新商品 {} 失败: {}", request.getProductId(), e.getMessage());
                }
            }
            
            log.info("批量设置完成，成功更新 {} 个商品", updatedProducts.size());
            return updatedProducts;
        } catch (Exception e) {
            log.error("批量设置首页商品失败", e);
            throw new RuntimeException("批量设置首页商品失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有可设置为首页的商品
     */
    public List<Product> getAvailableProductsForHomepage() {
        try {
            log.info("获取可设置为首页的商品列表");
            List<Product> products = productRepository.findByStatusTrueOrderByNameAsc();
            log.info("获取到 {} 个可用商品", products.size());
            return products;
        } catch (Exception e) {
            log.error("获取可用商品失败", e);
            throw new RuntimeException("获取可用商品失败: " + e.getMessage());
        }
    }

    /**
     * 获取ProductRepository实例
     */
    public ProductRepository getProductRepository() {
        return productRepository;
    }
} 