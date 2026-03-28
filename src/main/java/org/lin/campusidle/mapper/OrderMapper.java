package org.lin.campusidle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.lin.campusidle.entity.OrderInfo;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
    //根据订单ID查询订单
    @Select("select * from order_info where order_id=#{orderId}")
    OrderInfo findByOrderId(Long orderId);
    
    //根据用户ID查询订单列表
    @Select("select * from order_info where buyer_id=#{userId} or seller_id=#{userId}")
    List<OrderInfo> findByUserId(Long userId);
    
    //根据订单号查询订单
    @Select("select * from order_info where order_no=#{orderNo}")
    OrderInfo findByOrderNo(String orderNo);
}