package org.lin.campusidle.config;

import org.lin.campusidle.common.util.Md5Util;
import org.lin.campusidle.entity.User;
import org.lin.campusidle.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在管理员账户
        User admin = userMapper.findByPhone("13800138000");
        if (admin == null) {
            // 创建默认管理员账户
            admin = new User();
            admin.setUsername("admin");
            admin.setNickname("管理员");
            admin.setPhone("13800138000");
            admin.setEmail("admin@campus-idle.com"); // 设置唯一的邮箱
            admin.setPassword(Md5Util.encrypt("admin123"));
            admin.setRole(0); // 0表示管理员
            admin.setStatus(1); // 1表示正常
            userMapper.insert(admin);
            System.out.println("默认管理员账户创建成功：手机号 13800138000，密码 admin123");
        }
    }
}
