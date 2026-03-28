package org.lin.campusidle.controller;

import org.lin.campusidle.common.jwt.JwtAuth;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.common.threadlocal.UserThreadLocal;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.service.ProductService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

//查看商品列表，查看商品详情，查看商品分类不用做登录校验，其他均要做登录校验
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //==========================================
    //查看商品列表
    @GetMapping("/list")
    public Result<?> getProductList(@RequestParam Long current, @RequestParam Long size) {
        // 调用Service层的方法获取商品列表
        return productService.getProductList(current, size);
    }

    //===========================================
    //查看商品详情
    @GetMapping("/detail")
    public Result<?> getProductDetail(@RequestParam Long productId) {
        // 调用Service层的方法获取商品详情
        return productService.getProductDetail(productId);
    }

    //============================================
    //查看商品分类
    @GetMapping("/categories")
    public Result<?> getCategories() {
        // 调用Service层的方法获取商品分类
        return productService.getCategories();
    }

    //================================================
    //查询商品（按商品分类查询，按商品名称查询，商品名称查询可以字段查询不用查询名称完全一致的，包含查询的字段即可）
    @GetMapping("/search")
    public Result<?> searchProducts(@RequestParam(required = false) Integer categoryId, 
                                   @RequestParam(required = false) String keyword, 
                                   @RequestParam Long current, @RequestParam Long size) {
        // 调用Service层的方法根据条件查询商品
        return productService.searchProducts(categoryId, keyword, current, size);
    }

    //============================================
    //发布商品
    @JwtAuth
    @PostMapping("/publish")
    public Result<?> publishProduct(@RequestBody ProductRequest productRequest) {
        // 实现发布商品的逻辑
        // 获取当前登录用户ID
        Long userId = UserThreadLocal.get().getId();
        // 设置商品的发布者ID
        productRequest.getProduct().setPublishUserId(userId);
        // 调用Service层的方法发布商品
        return productService.publishProduct(productRequest.getProduct(), productRequest.getImages());
    }

    //============================================
    //编辑商品
    @JwtAuth
    @PutMapping("/edit")
    public Result<?> editProduct(@RequestBody ProductRequest productRequest) {
        // 实现编辑商品的逻辑
        // 获取当前登录用户ID
        Long userId = UserThreadLocal.get().getId();
        // 调用Service层的方法编辑商品
        return productService.editProduct(productRequest.getProduct(), productRequest.getImages());
    }

    //===========================================
    //下架商品
    @JwtAuth
    @PutMapping("/off-shelf")
    public Result<?> offShelfProduct(@RequestParam Long productId) {
        // 实现下架商品的逻辑
        // 获取当前登录用户ID
        Long userId = UserThreadLocal.get().getId();
        // 调用Service层的方法下架商品
        return productService.offShelfProduct(productId, userId);
    }

    //=============================================
    //删除商品
    @JwtAuth
    @DeleteMapping("/delete")
    public Result<?> deleteProduct(@RequestParam Long productId) {
        // 实现删除商品的逻辑
        // 获取当前登录用户ID
        Long userId = UserThreadLocal.get().getId();
        // 调用Service层的方法删除商品
        return productService.deleteProduct(productId, userId);
    }



    // 商品请求类，包含商品信息和图片列表
    public static class ProductRequest {
        private Product product;
        private List<String> images;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }
}