package org.lin.campusidle.service;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.vo.ProductV0;
import org.lin.campusidle.vo.UserV0;

public interface UserService {
    //发送验证码功能
    String sendCode(String phone);
    
    //校验验证码功能
    boolean verifyCode(String phone, String code);
    
    //校验用户手机号密码功能
    UserV0 verifyPhonePassword(String phone, String password);
    
    //能校验用户两次输入新密码是否一致
    boolean checkPasswordConsistency(String password1, String password2);
    
    //修改密码功能校验用户输入的旧密码是否正确
    boolean verifyOldPassword(Long userId, String oldPassword);
    
    //按用户名/用户id/用户手机号查询用户发布的商品信息
    Result<PageV0<ProductV0>> getUserProducts(Object identifier, Long current, Long size);
    
    //按用户名/用户id/用户手机号查询用户收藏的商品信息
    Result<PageV0<ProductV0>> getUserFavorites(Object identifier, Long current, Long size);
    
    //注册用户
    UserV0 register(String phone, String code, String password, String nickname, String username);
    
    //验证码登录
    UserV0 loginByCode(String phone, String code);
}
