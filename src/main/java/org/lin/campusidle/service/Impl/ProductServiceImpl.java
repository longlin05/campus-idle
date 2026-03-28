package org.lin.campusidle.service.Impl;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Category;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.entity.ProductImage;
import org.lin.campusidle.mapper.CategoryMapper;
import org.lin.campusidle.mapper.ProductImageMapper;
import org.lin.campusidle.mapper.ProductMapper;
import org.lin.campusidle.service.ProductService;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.vo.ProductV0;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductImageMapper productImageMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    //查询所有商品功能
    @Override
    public Result<PageV0<ProductV0>> getProductList(Long current, Long size) {
        // 实现查询所有商品的逻辑
        // 使用MyBatis-Plus的分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> resultPage = productMapper.selectPage(page, null);
        
        // 转换为PageV0
        PageV0<ProductV0> pageV0 = new PageV0<>();
        pageV0.setCurrent(resultPage.getCurrent());
        pageV0.setSize(resultPage.getSize());
        pageV0.setTotal(resultPage.getTotal());
        pageV0.setPages(resultPage.getPages());
        
        // 转换为ProductV0列表
        List<ProductV0> records = new ArrayList<>();
        for (Product product : resultPage.getRecords()) {
            // 查询商品图片
            List<ProductImage> images = productImageMapper.findByProductId(product.getProductId());
            List<String> imageUrls = new ArrayList<>();
            for (ProductImage image : images) {
                imageUrls.add(image.getImageUrl());
            }
            records.add(convertToProductV0(product, imageUrls));
        }
        pageV0.setRecords(records);
        
        return Result.success(pageV0);
    }

    //按商品id查询商品信息（包含商品表，商品图片表）功能
    @Override
    public Result<ProductV0> getProductDetail(Long productId) {
        // 实现按商品id查询商品详情的逻辑
        Product product = productMapper.findByProductId(productId);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        
        // 查询商品图片
        List<ProductImage> images = productImageMapper.findByProductId(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage image : images) {
            imageUrls.add(image.getImageUrl());
        }
        
        // 转换为ProductV0
        ProductV0 productV0 = convertToProductV0(product, imageUrls);
        return Result.success(productV0);
    }

    //查询商品类别功能
    @Override
    public Result<List<String>> getCategories() {
        // 实现查询商品类别的逻辑
        List<Category> categoryList = categoryMapper.findAllActive();
        List<String> categories = new ArrayList<>();
        for (Category category : categoryList) {
            categories.add(category.getCategoryName());
        }
        return Result.success(categories);
    }

    //按商品类别查询商品功能
    @Override
    public Result<PageV0<ProductV0>> searchProductsByCategory(Integer categoryId, Long current, Long size) {
        // 实现按商品类别查询商品的逻辑
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Product> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("category_id", categoryId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> resultPage = productMapper.selectPage(page, wrapper);
        
        return convertToPageV0(resultPage);
    }

    //按商品名称查询商品功能
    @Override
    public Result<PageV0<ProductV0>> searchProductsByKeyword(String keyword, Long current, Long size) {
        // 实现按商品名称查询商品的逻辑
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Product> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.like("title", keyword);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> resultPage = productMapper.selectPage(page, wrapper);
        
        return convertToPageV0(resultPage);
    }

    //综合查询商品功能
    @Override
    public Result<PageV0<ProductV0>> searchProducts(Integer categoryId, String keyword, Long current, Long size) {
        // 实现综合查询商品的逻辑
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Product> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("title", keyword);
        }
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> resultPage = productMapper.selectPage(page, wrapper);
        
        return convertToPageV0(resultPage);
    }
    
    // 辅助方法：将MyBatis-Plus的Page转换为PageV0
    private Result<PageV0<ProductV0>> convertToPageV0(com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> resultPage) {
        PageV0<ProductV0> pageV0 = new PageV0<>();
        pageV0.setCurrent(resultPage.getCurrent());
        pageV0.setSize(resultPage.getSize());
        pageV0.setTotal(resultPage.getTotal());
        pageV0.setPages(resultPage.getPages());
        
        // 转换为ProductV0列表
        List<ProductV0> records = new ArrayList<>();
        for (Product product : resultPage.getRecords()) {
            // 查询商品图片
            List<ProductImage> images = productImageMapper.findByProductId(product.getProductId());
            List<String> imageUrls = new ArrayList<>();
            for (ProductImage image : images) {
                imageUrls.add(image.getImageUrl());
            }
            records.add(convertToProductV0(product, imageUrls));
        }
        pageV0.setRecords(records);
        
        return Result.success(pageV0);
    }

    //发布商品功能
    @Override
    public Result<?> publishProduct(Product product, List<String> images) {
        // 实现发布商品的逻辑
        // 1. 保存商品信息
        productMapper.insert(product);
        
        // 2. 保存商品图片信息
        if (images != null && !images.isEmpty()) {
            int sortOrder = 1;
            for (String imageUrl : images) {
                ProductImage productImage = new ProductImage();
                productImage.setProductId(product.getProductId());
                productImage.setImageUrl(imageUrl);
                productImage.setSortOrder(sortOrder++);
                productImageMapper.insert(productImage);
            }
        }
        
        return Result.success("商品发布成功");
    }

    //修改商品信息功能
    @Override
    public Result<?> editProduct(Product product, List<String> images) {
        // 实现修改商品信息的逻辑
        // 1. 更新商品信息
        productMapper.updateById(product);
        
        // 2. 删除原有图片信息
        List<ProductImage> oldImages = productImageMapper.findByProductId(product.getProductId());
        for (ProductImage image : oldImages) {
            productImageMapper.deleteById(image.getImageId());
        }
        
        // 3. 保存新图片信息
        if (images != null && !images.isEmpty()) {
            int sortOrder = 1;
            for (String imageUrl : images) {
                ProductImage productImage = new ProductImage();
                productImage.setProductId(product.getProductId());
                productImage.setImageUrl(imageUrl);
                productImage.setSortOrder(sortOrder++);
                productImageMapper.insert(productImage);
            }
        }
        
        return Result.success("商品编辑成功");
    }

    //下架商品功能（仅修改商品状态）
    @Override
    public Result<?> offShelfProduct(Long productId, Long userId) {
        // 实现下架商品的逻辑
        // 1. 验证商品是否属于当前用户
        Product product = productMapper.findByProductId(productId);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        if (!product.getPublishUserId().equals(userId)) {
            return Result.error(403, "无权操作此商品");
        }
        
        // 2. 修改商品状态为下架
        product.setStatus(0); // 0表示下架
        productMapper.updateById(product);
        
        return Result.success("商品下架成功");
    }

    //删除商品功能
    @Override
    public Result<?> deleteProduct(Long productId, Long userId) {
        // 实现删除商品的逻辑
        // 1. 验证商品是否属于当前用户
        Product product = productMapper.findByProductId(productId);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        if (!product.getPublishUserId().equals(userId)) {
            return Result.error(403, "无权操作此商品");
        }
        
        // 2. 删除商品图片信息
        List<ProductImage> images = productImageMapper.findByProductId(productId);
        for (ProductImage image : images) {
            productImageMapper.deleteById(image.getImageId());
        }
        
        // 3. 删除商品信息
        productMapper.deleteById(productId);
        
        return Result.success("商品删除成功");
    }



    // 将Product对象转换为ProductV0对象
    private ProductV0 convertToProductV0(Product product, List<String> images) {
        ProductV0 productV0 = new ProductV0();
        BeanUtils.copyProperties(product, productV0);
        productV0.setName(product.getTitle());
        productV0.setImages(images);
        return productV0;
    }
}
