package org.lin.campusidle.service.Impl;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.common.threadlocal.UserThreadLocal;
import org.lin.campusidle.common.util.CodeUtil;
import org.lin.campusidle.common.util.Md5Util;
import org.lin.campusidle.entity.User;
import org.lin.campusidle.mapper.UserMapper;
import org.lin.campusidle.service.UserService;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.vo.ProductV0;
import org.lin.campusidle.vo.UserV0;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CodeUtil codeUtil;
    
    //发送验证码功能
    @Override
    public String sendCode(String phone) {
        return codeUtil.sendCode(phone);
    }
    
    //校验验证码功能
    @Override
    public boolean verifyCode(String phone, String code) {
        return codeUtil.verifyCode(phone, code);
    }
    
    //校验用户手机号密码功能
    @Override
    public UserV0 verifyPhonePassword(String phone, String password) {
        // 根据手机号查询用户
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            return null;
        }
        if (Md5Util.verify(password, user.getPassword())) {
            return convertToUserV0(user);
        }
        return null;
    }
    
    //能校验用户两次输入新密码是否一致
    @Override
    public boolean checkPasswordConsistency(String password1, String password2) {
        return password1 != null && password1.equals(password2);
    }
    
    //修改密码功能校验用户输入的旧密码是否正确
    @Override
    public boolean verifyOldPassword(Long userId, String oldPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        return Md5Util.verify(oldPassword, user.getPassword());
    }
    
    //按用户名/用户id/用户手机号查询用户发布的商品信息
    @Override
    public Result<PageV0<ProductV0>> getUserProducts(Object identifier, Long current, Long size) {
        Long userId = null;
        
        // 优先从ThreadLocal获取用户信息
        UserV0 user = UserThreadLocal.get();
        if (user != null) {
            userId = user.getId();
        } else if (identifier instanceof Long) {
            // 如果ThreadLocal中没有，使用传入的identifier
            userId = (Long) identifier;
        }
        
        // 实现根据用户id查询商品的逻辑
        // 这里模拟返回数据
        PageV0<ProductV0> page = new PageV0<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotal(0L);
        page.setPages(0L);
        page.setRecords(new ArrayList<>());
        return Result.success(page);
    }
    
    //按用户名/用户id/用户手机号查询用户收藏的商品信息
    @Override
    public Result<PageV0<ProductV0>> getUserFavorites(Object identifier, Long current, Long size) {
        Long userId = null;
        
        // 优先从ThreadLocal获取用户信息
        UserV0 user = UserThreadLocal.get();
        if (user != null) {
            userId = user.getId();
        } else if (identifier instanceof Long) {
            // 如果ThreadLocal中没有，使用传入的identifier
            userId = (Long) identifier;
        }
        
        // 实现根据用户id查询收藏的逻辑
        // 这里模拟返回数据
        PageV0<ProductV0> page = new PageV0<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotal(0L);
        page.setPages(0L);
        page.setRecords(new ArrayList<>());
        return Result.success(page);
    }
    
    //注册用户
    @Override
    public UserV0 register(String phone, String code, String password, String nickname, String username) {
        // 校验验证码
        if (!this.verifyCode(phone, code)) {
            return null;
        }
        
        // 创建用户
        User user = new User();
        user.setPhone(phone);
        user.setPassword(Md5Util.encrypt(password));
        user.setNickname(nickname);
        user.setUsername(username);
        
        // 保存用户
        userMapper.insert(user);
        
        // 返回用户信息（不包含敏感信息）
        return convertToUserV0(user);
    }
    
    //验证码登录
    @Override
    public UserV0 loginByCode(String phone, String code) {
        // 校验验证码
        if (!this.verifyCode(phone, code)) {
            return null;
        }
        
        // 根据手机号查询用户
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            return null;
        }
        
        // 返回用户信息（不包含敏感信息）
        return convertToUserV0(user);
    }
    
    // 将User对象转换为UserV0对象，避免返回敏感信息
    private UserV0 convertToUserV0(User user) {
        UserV0 userV0 = new UserV0();
        userV0.setId(user.getUserId());
        userV0.setUsername(user.getUsername());
        userV0.setNickname(user.getNickname());
        userV0.setPhone(user.getPhone());
        userV0.setEmail(user.getEmail());
        userV0.setAvatar(user.getAvatar());
        userV0.setStatus(user.getStatus());
        userV0.setRole(user.getRole());
        userV0.setCreateTime(user.getCreateTime());
        userV0.setLastLoginTime(user.getLastLoginTime());
        return userV0;
    }
}
