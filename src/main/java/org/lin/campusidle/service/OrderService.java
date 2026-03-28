package org.lin.campusidle.service;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.OrderInfo;
import org.lin.campusidle.vo.PageV0;

import java.util.List;

public interface OrderService {

    //创建订单功能（调用校验商品功能）
    Result<?> createOrder(OrderInfo orderInfo);

    //校验商品状态功能
    boolean validateProductStatus(Long productId);

    //按订单id查询订单信息功能
    Result<OrderInfo> getOrderById(Long orderId);

    //支付订单功能（仅修改订单为已支付状态）
    Result<?> payOrder(Long orderId);

    //确认收货功能（仅修改订单状态为已收货）
    Result<?> confirmReceipt(Long orderId);

    //取消订单功能（逻辑删除/直接删除）
    Result<?> cancelOrder(Long orderId);

    //按用户id查询订单列表
    Result<PageV0<OrderInfo>> getOrderListByUserId(Long userId, Long current, Long size);

    //申请退款功能（仅修改订单状态为退款中）
    Result<?> applyRefund(Long orderId);

    //处理退款功能（仅修改订单状态为已退款）
    Result<?> processRefund(Long orderId);

}
