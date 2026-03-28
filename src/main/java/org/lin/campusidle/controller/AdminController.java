package org.lin.campusidle.controller;

import org.lin.campusidle.common.jwt.JwtAuth;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Category;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.entity.User;
import org.lin.campusidle.service.AdminService;
import org.lin.campusidle.vo.PageV0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//全部要做管理员身份校验
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    //管理用户查询用户功能
    @JwtAuth(admin = true)
    @GetMapping("/users")
    public Result<PageV0<User>> getUserList(@RequestParam(required = false) String keyword,
                                            @RequestParam Long current,
                                            @RequestParam Long size) {
        return adminService.getUserList(keyword, current, size);
    }

    //禁用/启用用户功能（修改用户状态为正常/禁用）
    @JwtAuth(admin = true)
    @PutMapping("/users/{userId}/status")
    public Result<?> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        return adminService.updateUserStatus(userId, status);
    }

    //管理商品（更新商品信息）
    @JwtAuth(admin = true)
    @PutMapping("/products/{productId}")
    public Result<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        return adminService.updateProduct(productId, product);
    }

    //修改分类（更新）
    @JwtAuth(admin = true)
    @PutMapping("/categories/{categoryId}")
    public Result<?> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        return adminService.updateCategory(categoryId, category);
    }

    //添加分类
    @JwtAuth(admin = true)
    @PostMapping("/categories")
    public Result<?> addCategory(@RequestBody Category category) {
        return adminService.addCategory(category);
    }

    //删除分类
    @JwtAuth(admin = true)
    @DeleteMapping("/categories/{categoryId}")
    public Result<?> deleteCategory(@PathVariable Long categoryId) {
        return adminService.deleteCategory(categoryId);
    }

    //统计站内信息（统计用户总数，当天订单量，当天交易额，均可用redis做暂存都是临时数据而已不用做持久化）
    @JwtAuth(admin = true)
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return adminService.getStats();
    }
}
