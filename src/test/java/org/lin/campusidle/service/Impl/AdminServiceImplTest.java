package org.lin.campusidle.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Category;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.entity.User;
import org.lin.campusidle.mapper.CategoryMapper;
import org.lin.campusidle.mapper.ProductMapper;
import org.lin.campusidle.mapper.UserMapper;
import org.lin.campusidle.service.AdminService;
import org.lin.campusidle.vo.PageV0;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User user;
    private Product product;
    private Category category;
    private List<User> userList;
    private List<Product> productList;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        user = new User();
        user.setUserId(1L);
        user.setUsername("admin");
        user.setNickname("管理员");
        user.setPhone("13800138000");
        user.setRole(0);
        user.setStatus(1);

        product = new Product();
        product.setProductId(1L);
        product.setTitle("测试商品");
        product.setPrice(new BigDecimal(100));
        product.setCategoryId(1);
        product.setStatus(1);

        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("数码产品");
        category.setSortOrder(1);

        userList = new ArrayList<>();
        userList.add(user);

        productList = new ArrayList<>();
        productList.add(product);

        categoryList = new ArrayList<>();
        categoryList.add(category);
    }

    @Test
    void testGetUserList() {
        // 测试查询用户列表
        when(userMapper.selectList(null)).thenReturn(userList);

        Result<PageV0<User>> result = adminService.getUserList(null, 1L, 10L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    void testUpdateUserStatus() {
        // 测试禁用/启用用户
        when(userMapper.selectById(anyLong())).thenReturn(user);
        when(userMapper.updateById(any())).thenReturn(1);

        Result<?> result = adminService.updateUserStatus(1L, 0);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testUpdateProduct() {
        // 测试更新商品信息
        when(productMapper.selectById(anyLong())).thenReturn(product);
        when(productMapper.updateById(any())).thenReturn(1);

        Result<?> result = adminService.updateProduct(1L, product);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testUpdateCategory() {
        // 测试更新分类
        when(categoryMapper.selectById(anyLong())).thenReturn(category);
        when(categoryMapper.updateById(any())).thenReturn(1);

        Result<?> result = adminService.updateCategory(1L, category);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testAddCategory() {
        // 测试添加分类
        when(categoryMapper.insert(any())).thenReturn(1);

        Result<?> result = adminService.addCategory(category);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testDeleteCategory() {
        // 测试删除分类
        when(categoryMapper.selectById(anyLong())).thenReturn(category);
        when(categoryMapper.deleteById(anyLong())).thenReturn(1);

        Result<?> result = adminService.deleteCategory(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testGetStats() {
        // 测试获取统计信息
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(userMapper.selectCount(null)).thenReturn(100L);
        when(productMapper.selectCount(null)).thenReturn(500L);
        when(categoryMapper.selectCount(null)).thenReturn(10L);

        Result<Map<String, Object>> result = adminService.getStats();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(100L, result.getData().get("userCount"));
        assertEquals(500L, result.getData().get("productCount"));
        assertEquals(10L, result.getData().get("categoryCount"));
    }

    @Test
    void testGetStatsFromCache() {
        // 测试从缓存获取统计信息
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", 100L);
        stats.put("productCount", 500L);
        stats.put("categoryCount", 10L);

        when(valueOperations.get(anyString())).thenReturn(stats);

        Result<Map<String, Object>> result = adminService.getStats();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(100L, result.getData().get("userCount"));
        assertEquals(500L, result.getData().get("productCount"));
        assertEquals(10L, result.getData().get("categoryCount"));

        // 验证没有调用数据库查询
        verify(userMapper, never()).selectCount(null);
        verify(productMapper, never()).selectCount(null);
        verify(categoryMapper, never()).selectCount(null);
    }
}
