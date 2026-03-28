package org.lin.campusidle.controller;

import org.lin.campusidle.common.jwt.JwtAuth;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.common.threadlocal.UserThreadLocal;
import org.lin.campusidle.entity.OrderInfo;
import org.lin.campusidle.service.OrderService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

//此层内所有方法都要经拦截器校验登录状态
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //创建订单
    //需校验商品状态为上架
    @JwtAuth
    @PostMapping("/create")
    public Result<?> createOrder(@RequestBody OrderInfo orderInfo) {
        // 设置买家ID为当前登录用户
        orderInfo.setBuyerId(UserThreadLocal.get().getId());
        return orderService.createOrder(orderInfo);
    }

    //支付订单
    @JwtAuth
    @PutMapping("/pay")
    public Result<?> payOrder(@RequestParam Long orderId) {
        return orderService.payOrder(orderId);
    }

    //确认收货
    @JwtAuth
    @PutMapping("/confirm")
    public Result<?> confirmReceipt(@RequestParam Long orderId) {
        return orderService.confirmReceipt(orderId);
    }

    //取消订单
    @JwtAuth
    @PutMapping("/cancel")
    public Result<?> cancelOrder(@RequestParam Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    //查看我的订单（订单列表）
    @JwtAuth
    @GetMapping("/list")
    public Result<?> getOrderList(@RequestParam Long current, @RequestParam Long size) {
        Long userId = UserThreadLocal.get().getId();
        return orderService.getOrderListByUserId(userId, current, size);
    }

    //查看订单详情
    @JwtAuth
    @GetMapping("/detail")
    public Result<?> getOrderDetail(@RequestParam Long orderId) {
        return orderService.getOrderById(orderId);
    }

    //申请退款
    @JwtAuth
    @PostMapping("/refund/apply")
    public Result<?> applyRefund(@RequestParam Long orderId) {
        return orderService.applyRefund(orderId);
    }

    //处理退款
    @JwtAuth
    @PostMapping("/refund/process")
    public Result<?> processRefund(@RequestParam Long orderId) {
        return orderService.processRefund(orderId);
    }

}
