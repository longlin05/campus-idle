package org.lin.campusidle.service.Impl;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Category;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.entity.User;
import org.lin.campusidle.mapper.CategoryMapper;
import org.lin.campusidle.mapper.ProductMapper;
import org.lin.campusidle.mapper.UserMapper;
import org.lin.campusidle.service.AdminService;
import org.lin.campusidle.vo.PageV0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis键前缀
    private static final String STATS_KEY = "admin:stats:";
    private static final long STATS_EXPIRE_TIME = 24 * 60 * 60; // 24小时过期

    @Override
    public Result<PageV0<User>> getUserList(String keyword, Long current, Long size) {
        // 查询所有用户
        List<User> userList = userMapper.selectList(null);

        // 如果有搜索关键词，进行过滤
        if (keyword != null && !keyword.isEmpty()) {
            userList = userList.stream()
                    .filter(user -> user.getUsername().contains(keyword)
                            || user.getNickname().contains(keyword)
                            || user.getPhone().contains(keyword))
                    .toList();
        }

        // 实现分页
        int start = (int) ((current - 1) * size);
        int end = (int) (start + size);
        if (start >= userList.size()) {
            return Result.success(new PageV0<>());
        }
        if (end > userList.size()) {
            end = userList.size();
        }
        List<User> pageList = userList.subList(start, end);

        PageV0<User> page = new PageV0<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotal((long) userList.size());
        page.setPages((userList.size() + size - 1) / size);
        page.setRecords(pageList);

        return Result.success(page);
    }

    @Override
    public Result<?> updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);

        String statusText = status == 1 ? "启用" : "禁用";
        return Result.success("用户" + statusText + "成功");
    }

    @Override
    public Result<?> updateProduct(Long productId, Product product) {
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null) {
            return Result.error(404, "商品不存在");
        }

        product.setProductId(productId);
        productMapper.updateById(product);

        return Result.success("商品更新成功");
    }

    @Override
    public Result<?> updateCategory(Long categoryId, Category category) {
        Category existingCategory = categoryMapper.selectById(categoryId);
        if (existingCategory == null) {
            return Result.error(404, "分类不存在");
        }

        category.setCategoryId(categoryId);
        categoryMapper.updateById(category);

        return Result.success("分类更新成功");
    }

    @Override
    public Result<?> addCategory(Category category) {
        categoryMapper.insert(category);
        return Result.success("分类添加成功");
    }

    @Override
    public Result<?> deleteCategory(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        categoryMapper.deleteById(categoryId);
        return Result.success("分类删除成功");
    }

    @Override
    public Result<Map<String, Object>> getStats() {
        String key = STATS_KEY + "today";

        // 先从Redis获取缓存
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) redisTemplate.opsForValue().get(key);

        if (stats == null) {
            // 从数据库统计
            stats = new HashMap<>();

            // 用户总数
            Long userCount = userMapper.selectCount(null);
            stats.put("userCount", userCount);

            // 商品总数
            Long productCount = productMapper.selectCount(null);
            stats.put("productCount", productCount);

            // 分类总数
            Long categoryCount = categoryMapper.selectCount(null);
            stats.put("categoryCount", categoryCount);

            // 存入Redis，设置过期时间
            redisTemplate.opsForValue().set(key, stats, STATS_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        return Result.success(stats);
    }
}
