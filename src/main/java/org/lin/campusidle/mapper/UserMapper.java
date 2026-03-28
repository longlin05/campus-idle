package org.lin.campusidle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.lin.campusidle.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    //根据用户名查询用户
    @Select("select * from sys_user where username=#{username}")
    User findByUsername(String userName);
    
    //根据手机号查询用户
    @Select("select * from sys_user where phone=#{phone}")
    User findByPhone(String phone);
}
