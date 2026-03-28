package org.lin.campusidle.controller;

import org.lin.campusidle.common.jwt.JwtAuth;
import org.lin.campusidle.common.jwt.JwtUtils;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.service.UserService;
import org.lin.campusidle.vo.LoginResult;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.vo.ProductV0;
import org.lin.campusidle.vo.UserV0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    //========================================================
    //用户注册功能
    @PostMapping("/register")
    public Result<LoginResult> register(@RequestParam String phone, @RequestParam String code,
                                        @RequestParam String password, @RequestParam String nickname,
                                        @RequestParam String username) {
        UserV0 user = userService.register(phone, code, password, nickname, username);
        if (user == null) {
            return Result.businessError("注册失败");
        }

        // 生成JWT令牌
        String token = generateToken(user);

        // 构建登录结果
        LoginResult result = new LoginResult();
        result.setUser(user);
        result.setToken(token);

        return Result.success(result);
    }

    //==========================================================
    //用户手机号密码登录功能
    @PostMapping("/login/password")
    public Result<LoginResult> loginByPassword(@RequestParam String phone, @RequestParam String password) {
        UserV0 user = userService.verifyPhonePassword(phone, password);
        if (user == null) {
            return Result.businessError("手机号或密码错误");
        }

        // 生成JWT令牌
        String token = generateToken(user);

        // 构建登录结果
        LoginResult result = new LoginResult();
        result.setUser(user);
        result.setToken(token);

        return Result.success(result);
    }

    //================================================
    //用户手机号验证码登录
    @PostMapping("/login/sms")
    public Result<LoginResult> loginByCode(@RequestParam String phone, @RequestParam String code) {
        UserV0 user = userService.loginByCode(phone, code);
        if (user == null) {
            return Result.businessError("手机号或验证码错误");
        }

        // 生成JWT令牌
        String token = generateToken(user);

        // 构建登录结果
        LoginResult result = new LoginResult();
        result.setUser(user);
        result.setToken(token);

        return Result.success(result);
    }

    //===================================================
    //发送验证码
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestParam String phone) {
        String code = userService.sendCode(phone);
        return Result.success("验证码发送成功");
    }

    //=============================================
    //查看我的发布功能
    @JwtAuth
    @GetMapping("/products")
    public Result<PageV0<ProductV0>> getUserProducts(@RequestParam Object identifier,
                                                     @RequestParam Long current, @RequestParam Long size) {
        return userService.getUserProducts(identifier, current, size);
    }

    //==============================================
    //查看我的收藏功能
    @JwtAuth
    @GetMapping("/favorites")
    public Result<PageV0<ProductV0>> getUserFavorites(@RequestParam Object identifier,
                                                      @RequestParam Long current, @RequestParam Long size) {
        return userService.getUserFavorites(identifier, current, size);
    }

    // 生成JWT令牌
    private String generateToken(UserV0 user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("phone", user.getPhone());
        claims.put("nickname", user.getNickname());
        claims.put("role", user.getRole());
        claims.put("status", user.getStatus());
        return jwtUtils.generateToken(String.valueOf(user.getId()), claims);
    }
}