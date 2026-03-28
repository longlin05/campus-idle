package org.lin.campusidle.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Category;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.entity.ProductImage;
import org.lin.campusidle.mapper.CategoryMapper;
import org.lin.campusidle.mapper.ProductImageMapper;
import org.lin.campusidle.mapper.ProductMapper;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.vo.ProductV0;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductImageMapper productImageMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private List<ProductImage> productImages;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        product = new Product();
        product.setProductId(1L);
        product.setPublishUserId(1L);
        product.setTitle("测试商品");
        product.setDescription("测试商品描述");
        product.setPrice(new BigDecimal(100));
        product.setOriginalPrice(new BigDecimal(120));
        product.setCategoryId(1);
        product.setStatus(1);

        productImages = new ArrayList<>();
        ProductImage image1 = new ProductImage();
        image1.setImageId(1L);
        image1.setProductId(1L);
        image1.setImageUrl("http://example.com/image1.jpg");
        image1.setSortOrder(1);
        productImages.add(image1);

        categories = new ArrayList<>();
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("数码产品");
        category.setSortOrder(1);
        categories.add(category);
    }

    @Test
    void testGetProductList() {
        // 测试查询商品列表
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        page.setRecords(productList);
        page.setTotal(1L);
        page.setPages(1L);

        when(productMapper.selectPage(any(), any())).thenReturn(page);
        when(productImageMapper.findByProductId(anyLong())).thenReturn(productImages);

        Result<PageV0<ProductV0>> result = productService.getProductList(1L, 10L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    void testGetProductDetail() {
        // 测试查询商品详情
        when(productMapper.findByProductId(anyLong())).thenReturn(product);
        when(productImageMapper.findByProductId(anyLong())).thenReturn(productImages);

        Result<ProductV0> result = productService.getProductDetail(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("测试商品", result.getData().getName());
    }

    @Test
    void testGetCategories() {
        // 测试查询商品分类
        when(categoryMapper.findAllActive()).thenReturn(categories);

        Result<List<String>> result = productService.getCategories();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("数码产品", result.getData().get(0));
    }

    @Test
    void testSearchProductsByCategory() {
        // 测试按分类查询商品
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        page.setRecords(productList);
        page.setTotal(1L);
        page.setPages(1L);

        when(productMapper.selectPage(any(), any())).thenReturn(page);
        when(productImageMapper.findByProductId(anyLong())).thenReturn(productImages);

        Result<PageV0<ProductV0>> result = productService.searchProductsByCategory(1, 1L, 10L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    void testSearchProductsByKeyword() {
        // 测试按关键词查询商品
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        page.setRecords(productList);
        page.setTotal(1L);
        page.setPages(1L);

        when(productMapper.selectPage(any(), any())).thenReturn(page);
        when(productImageMapper.findByProductId(anyLong())).thenReturn(productImages);

        Result<PageV0<ProductV0>> result = productService.searchProductsByKeyword("测试", 1L, 10L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    void testSearchProducts() {
        // 测试综合查询商品
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        page.setRecords(productList);
        page.setTotal(1L);
        page.setPages(1L);

        when(productMapper.selectPage(any(), any())).thenReturn(page);
        when(productImageMapper.findByProductId(anyLong())).thenReturn(productImages);

        Result<PageV0<ProductV0>> result = productService.searchProducts(1, "测试", 1L, 10L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    void testPublishProduct() {
        // 测试发布商品
        List<String> images = new ArrayList<>();
        images.add("http://example.com/image1.jpg");

        when(productMapper.insert(any())).thenReturn(1);
        when(productImageMapper.insert(any())).thenReturn(1);

        Result<?> result = productService.publishProduct(product, images);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testEditProduct() {
        // 测试编辑商品
        List<String> images = new ArrayList<>();
        images.add("http://example.com/image1.jpg");

        when(productMapper.updateById(any())).thenReturn(1);
        when(productImageMapper.findByProductId(anyLong())).thenReturn(productImages);
        when(productImageMapper.deleteById(anyLong())).thenReturn(1);
        when(productImageMapper.insert(any())).thenReturn(1);

        Result<?> result = productService.editProduct(product, images);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testOffShelfProduct() {
        // 测试下架商品
        when(productMapper.findByProductId(anyLong())).thenReturn(product);
        when(productMapper.updateById(any())).thenReturn(1);

        Result<?> result = productService.offShelfProduct(1L, 1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testDeleteProduct() {
        // 测试删除商品
        when(productMapper.findByProductId(anyLong())).thenReturn(product);
        when(productImageMapper.findByProductId(anyLong())).thenReturn(productImages);
        when(productImageMapper.deleteById(anyLong())).thenReturn(1);
        when(productMapper.deleteById(anyLong())).thenReturn(1);

        Result<?> result = productService.deleteProduct(1L, 1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }
}
