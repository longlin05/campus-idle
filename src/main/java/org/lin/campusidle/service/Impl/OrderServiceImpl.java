package org.lin.campusidle.service.Impl;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.OrderInfo;
import org.lin.campusidle.entity.Product;
import org.lin.campusidle.mapper.OrderMapper;
import org.lin.campusidle.mapper.ProductMapper;
import org.lin.campusidle.service.OrderService;
import org.lin.campusidle.vo.PageV0;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.lin.campusidle.common.util.RedisUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisUtils redisUtils;

    //创建订单功能（调用校验商品功能）
    @Override
    public Result<?> createOrder(OrderInfo orderInfo) {
        // 校验商品状态
        if (!validateProductStatus(orderInfo.getProductId())) {
            return Result.error(400, "商品状态异常，无法创建订单");
        }
        
        // 生成订单号
        String orderNo = generateOrderNo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setStatus(0); // 0-待支付
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        
        // 保存订单
        orderMapper.insert(orderInfo);
        
        return Result.success("订单创建成功");
    }

    //校验商品状态功能
    @Override
    public boolean validateProductStatus(Long productId) {
        Product product = productMapper.findByProductId(productId);
        if (product == null) {
            return false;
        }
        // 商品状态为1表示上架
        return product.getStatus() == 1;
    }

    //按订单id查询订单信息功能
    @Override
    public Result<OrderInfo> getOrderById(Long orderId) {
        // 尝试从缓存获取
        String cacheKey = "order:info:" + orderId;
        Object cachedObject = redisUtils.get(cacheKey);
        OrderInfo cachedOrder = null;
        if (cachedObject != null && cachedObject instanceof OrderInfo) {
            cachedOrder = (OrderInfo) cachedObject;
        }
        
        if (cachedOrder != null) {
            return Result.success(cachedOrder);
        }
        
        // 缓存未命中，从数据库查询
        OrderInfo orderInfo = orderMapper.findByOrderId(orderId);
        if (orderInfo == null) {
            // 缓存空对象，设置5分钟过期时间
            redisUtils.set(cacheKey, null, 5, TimeUnit.MINUTES);
            return Result.error(404, "订单不存在");
        }
        
        // 缓存订单信息，设置7天过期时间
        redisUtils.set(cacheKey, orderInfo, 7, TimeUnit.DAYS);
        
        return Result.success(orderInfo);
    }

    //支付订单功能（仅修改订单为已支付状态）
    @Override
    public Result<?> payOrder(Long orderId) {
        OrderInfo orderInfo = orderMapper.findByOrderId(orderId);
        if (orderInfo == null) {
            return Result.error(404, "订单不存在");
        }
        if (orderInfo.getStatus() != 0) {
            return Result.error(400, "订单状态异常，无法支付");
        }
        
        orderInfo.setStatus(1); // 1-已支付
        orderInfo.setPayTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderMapper.updateById(orderInfo);
        
        // 清理缓存
        String cacheKey = "order:info:" + orderId;
        redisUtils.delete(cacheKey);
        
        return Result.success("订单支付成功");
    }

    //确认收货功能（仅修改订单状态为已收货）
    @Override
    public Result<?> confirmReceipt(Long orderId) {
        OrderInfo orderInfo = orderMapper.findByOrderId(orderId);
        if (orderInfo == null) {
            return Result.error(404, "订单不存在");
        }
        if (orderInfo.getStatus() != 2) {
            return Result.error(400, "订单状态异常，无法确认收货");
        }
        
        orderInfo.setStatus(3); // 3-已完成
        orderInfo.setUpdateTime(new Date());
        orderMapper.updateById(orderInfo);
        
        // 清理缓存
        String cacheKey = "order:info:" + orderId;
        redisUtils.delete(cacheKey);
        
        return Result.success("确认收货成功");
    }

    //取消订单功能（逻辑删除/直接删除）
    @Override
    public Result<?> cancelOrder(Long orderId) {
        OrderInfo orderInfo = orderMapper.findByOrderId(orderId);
        if (orderInfo == null) {
            return Result.error(404, "订单不存在");
        }
        if (orderInfo.getStatus() >= 3) {
            return Result.error(400, "订单状态异常，无法取消");
        }
        
        orderInfo.setStatus(4); // 4-已取消
        orderInfo.setCancelTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderMapper.updateById(orderInfo);
        
        // 清理缓存
        String cacheKey = "order:info:" + orderId;
        redisUtils.delete(cacheKey);
        
        return Result.success("订单取消成功");
    }

    //按用户id查询订单列表
    @Override
    public Result<PageV0<OrderInfo>> getOrderListByUserId(Long userId, Long current, Long size) {
        List<OrderInfo> orderList = orderMapper.findByUserId(userId);
        
        // 实现分页
        int start = (int) ((current - 1) * size);
        int end = (int) (start + size);
        if (start >= orderList.size()) {
            return Result.success(new PageV0<>());
        }
        if (end > orderList.size()) {
            end = orderList.size();
        }
        List<OrderInfo> pageList = orderList.subList(start, end);
        
        PageV0<OrderInfo> page = new PageV0<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotal((long) orderList.size());
        page.setPages((orderList.size() + size - 1) / size);
        page.setRecords(pageList);
        
        return Result.success(page);
    }

    //申请退款功能（仅修改订单状态为退款中）
    @Override
    public Result<?> applyRefund(Long orderId) {
        OrderInfo orderInfo = orderMapper.findByOrderId(orderId);
        if (orderInfo == null) {
            return Result.error(404, "订单不存在");
        }
        if (orderInfo.getStatus() != 1 && orderInfo.getStatus() != 2) {
            return Result.error(400, "订单状态异常，无法申请退款");
        }
        
        orderInfo.setStatus(5); // 5-退款中
        orderInfo.setUpdateTime(new Date());
        orderMapper.updateById(orderInfo);
        
        // 清理缓存
        String cacheKey = "order:info:" + orderId;
        redisUtils.delete(cacheKey);
        
        return Result.success("退款申请成功");
    }

    //处理退款功能（仅修改订单状态为已退款）
    @Override
    public Result<?> processRefund(Long orderId) {
        OrderInfo orderInfo = orderMapper.findByOrderId(orderId);
        if (orderInfo == null) {
            return Result.error(404, "订单不存在");
        }
        if (orderInfo.getStatus() != 5) {
            return Result.error(400, "订单状态异常，无法处理退款");
        }
        
        orderInfo.setStatus(6); // 6-已退款
        orderInfo.setUpdateTime(new Date());
        orderMapper.updateById(orderInfo);
        
        // 清理缓存
        String cacheKey = "order:info:" + orderId;
        redisUtils.delete(cacheKey);
        
        return Result.success("退款处理成功");
    }

    // 生成订单号
    private String generateOrderNo() {
        // 简单实现，实际项目中可以使用更复杂的算法
        return System.currentTimeMillis() + "" + (int) (Math.random() * 1000);
    }

}
