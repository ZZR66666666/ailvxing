package cn.example.order.service.impl;

import cn.example.common.common.Result;
import cn.example.common.dto.OrderCreateDTO;
import cn.example.common.entity.OrderItem;
import cn.example.common.entity.Orders;
import cn.example.common.entity.Product;
import cn.example.order.mapper.OrderItemMapper;
import cn.example.order.mapper.OrdersMapper;
import cn.example.order.client.ProductClient;
import cn.example.order.service.OrderService;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductClient productClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public Result<String> createOrder(Long userId, OrderCreateDTO dto) {
        Result<Product> productResult = productClient.getById(dto.getProductId());
        Product product = productResult != null ? productResult.getData() : null;
        if (product == null) {
            return Result.error(404, "产品不存在");
        }
        if (product.getStock() == null || product.getStock() <= 0) {
            return Result.error(400, "产品已售罄");
        }

        Orders order = new Orders();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setPlanId(dto.getProductId());
        order.setTotalAmount(product.getPrice());
        order.setStatus(0);
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        order.setRemark(dto.getRemark());
        ordersMapper.insert(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductType(product.getType());
        item.setQuantity(1);
        item.setPrice(product.getPrice());
        orderItemMapper.insert(item);

        try { productClient.decreaseStock(product.getId(), 1); } catch (Exception ignored) {}
        clearProductCache(product.getId());

        return Result.success("下单成功", String.valueOf(order.getId()));
    }

    @Override
    public Result<Orders> getOrder(Long id) {
        Orders order = ordersMapper.findById(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    @Override
    public Result<List<Orders>> listMyOrders(Long userId) {
        List<Orders> orders = ordersMapper.findByUserId(userId);
        for (Orders order : orders) {
            order.setItems(orderItemMapper.findByOrderId(order.getId()));
        }
        return Result.success(orders);
    }

    @Override
    public Result<Void> payOrder(Long orderId, String payMethod) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error(400, "订单状态不允许支付");
        }
        order.setStatus(1);
        order.setPayMethod(payMethod);
        order.setPayTime(LocalDateTime.now());
        order.setPayAmount(order.getTotalAmount());
        ordersMapper.updateStatus(order);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> cancelOrder(Long userId, Long orderId) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            return Result.error(400, "当前状态不允许取消");
        }
        if (order.getStatus() == 1) {
            order.setStatus(5);
        } else {
            order.setStatus(6);
        }
        ordersMapper.updateStatus(order);
        List<OrderItem> cancelItems = orderItemMapper.findByOrderId(orderId);
        for (OrderItem it : cancelItems) {
            try { productClient.increaseStock(it.getProductId(), it.getQuantity()); } catch (Exception ignored) {}
        }
        return Result.success();
    }

    @Override
    public Result<Void> confirmOrder(Long orderId) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error(400, "只能确认已支付的订单");
        }
        order.setStatus(2);
        ordersMapper.updateStatus(order);
        return Result.success();
    }

    @Override
    public Result<Void> departOrder(Long orderId) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (order.getStatus() != 2) {
            return Result.error(400, "只能对已确认的订单进行出团操作");
        }
        order.setStatus(3);
        ordersMapper.updateStatus(order);
        return Result.success();
    }

    @Override
    public Result<Void> completeOrder(Long orderId) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (order.getStatus() != 3) {
            return Result.error(400, "只能对已出团的订单进行完成操作");
        }
        order.setStatus(4);
        ordersMapper.updateStatus(order);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> refundOrder(Long userId, Long orderId) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }
        if (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3) {
            return Result.error(400, "当前状态不允许退款");
        }
        order.setStatus(5);
        ordersMapper.updateStatus(order);
        // 恢复库存
        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        for (OrderItem item : items) {
            try { productClient.increaseStock(item.getProductId(), item.getQuantity()); } catch (Exception ignored) {}
        }
        return Result.success();
    }

    @Override
    public Result<List<OrderItem>> getOrderItems(Long orderId) {
        return Result.success(orderItemMapper.findByOrderId(orderId));
    }

    @Override
    @Transactional
    public Result<Void> deleteOrder(Long userId, Long orderId) {
        Orders order = ordersMapper.findById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }
        if (order.getStatus() != 5 && order.getStatus() != 6) {
            return Result.error(400, "只能删除已退款或已取消的订单");
        }
        orderItemMapper.deleteByOrderId(orderId);
        ordersMapper.deleteById(orderId);
        return Result.success();
    }

    private void clearProductCache(Long productId) {
        redisTemplate.delete("product:detail:" + productId);
        var keys = redisTemplate.keys("product:list*");
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
        keys = redisTemplate.keys("product:search:*");
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
    }
}