package org.lin.campusidle.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.OrderInfo;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.mapper.OrderMapper;
import org.lin.campusidle.mapper.ProductMapper;
import org.lin.campusidle.service.OrderService;
import org.lin.campusidle.vo.PageV0;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderInfo orderInfo;
    private Product product;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        orderInfo = new OrderInfo();
        orderInfo.setOrderId(1L);
        orderInfo.setOrderNo("20260328001");
        orderInfo.setProductId(1L);
        orderInfo.setBuyerId(1L);
        orderInfo.setSellerId(2L);
        orderInfo.setOrderAmount(new BigDecimal(100));
        orderInfo.setStatus(0);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());

        product = new Product();
        product.setProductId(1L);
        product.setStatus(1); // 1-上架
    }

    @Test
    void testCreateOrder() {
        // 测试创建订单
        when(productMapper.findByProductId(anyLong())).thenReturn(product);
        when(orderMapper.insert(any(OrderInfo.class))).thenReturn(1);

        Result<?> result = orderService.createOrder(orderInfo);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("订单创建成功", result.getMessage());
    }

    @Test
    void testValidateProductStatus() {
        // 测试校验商品状态
        when(productMapper.findByProductId(anyLong())).thenReturn(product);

        boolean result = orderService.validateProductStatus(1L);

        assertEquals(true, result);
    }

    @Test
    void testGetOrderById() {
        // 测试查询订单详情
        when(orderMapper.findByOrderId(anyLong())).thenReturn(orderInfo);

        Result<OrderInfo> result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getOrderId());
    }

    @Test
    void testPayOrder() {
        // 测试支付订单
        when(orderMapper.findByOrderId(anyLong())).thenReturn(orderInfo);
        when(orderMapper.updateById(any(OrderInfo.class))).thenReturn(1);

        Result<?> result = orderService.payOrder(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("订单支付成功", result.getMessage());
    }

    @Test
    void testConfirmReceipt() {
        // 测试确认收货
        orderInfo.setStatus(2); // 2-已发货
        when(orderMapper.findByOrderId(anyLong())).thenReturn(orderInfo);
        when(orderMapper.updateById(any(OrderInfo.class))).thenReturn(1);

        Result<?> result = orderService.confirmReceipt(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("确认收货成功", result.getMessage());
    }

    @Test
    void testCancelOrder() {
        // 测试取消订单
        when(orderMapper.findByOrderId(anyLong())).thenReturn(orderInfo);
        when(orderMapper.updateById(any(OrderInfo.class))).thenReturn(1);

        Result<?> result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("订单取消成功", result.getMessage());
    }

    @Test
    void testGetOrderListByUserId() {
        // 测试查询订单列表
        List<OrderInfo> orderList = new ArrayList<>();
        orderList.add(orderInfo);
        when(orderMapper.findByUserId(anyLong())).thenReturn(orderList);

        Result<PageV0<OrderInfo>> result = orderService.getOrderListByUserId(1L, 1L, 10L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    void testApplyRefund() {
        // 测试申请退款
        orderInfo.setStatus(1); // 1-已支付
        when(orderMapper.findByOrderId(anyLong())).thenReturn(orderInfo);
        when(orderMapper.updateById(any(OrderInfo.class))).thenReturn(1);

        Result<?> result = orderService.applyRefund(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("退款申请成功", result.getMessage());
    }

    @Test
    void testProcessRefund() {
        // 测试处理退款
        orderInfo.setStatus(5); // 5-退款中
        when(orderMapper.findByOrderId(anyLong())).thenReturn(orderInfo);
        when(orderMapper.updateById(any(OrderInfo.class))).thenReturn(1);

        Result<?> result = orderService.processRefund(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("退款处理成功", result.getMessage());
    }
}
