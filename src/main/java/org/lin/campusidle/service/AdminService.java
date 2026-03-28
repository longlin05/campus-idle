package org.lin.campusidle.service;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Category;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.entity.User;

import java.util.Map;

public interface AdminService {

    // 查询用户列表（支持关键词搜索）
    Result<PageV0<User>> getUserList(String keyword, Long current, Long size);

    // 禁用/启用用户
    Result<?> updateUserStatus(Long userId, Integer status);

    // 更新商品信息
    Result<?> updateProduct(Long productId, Product product);

    // 更新分类
    Result<?> updateCategory(Long categoryId, Category category);

    // 添加分类
    Result<?> addCategory(Category category);

    // 删除分类
    Result<?> deleteCategory(Long categoryId);

    // 获取统计信息
    Result<Map<String, Object>> getStats();
}
