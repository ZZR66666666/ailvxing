package cn.example.order.service;

import cn.example.common.common.Result;
import cn.example.common.dto.OrderCreateDTO;
import cn.example.common.entity.OrderItem;
import cn.example.common.entity.Orders;

import java.util.List;

public interface OrderService {

    Result<String> createOrder(Long userId, OrderCreateDTO dto);

    Result<Orders> getOrder(Long id);

    Result<List<Orders>> listMyOrders(Long userId);

    Result<Void> payOrder(Long orderId, String payMethod);

    Result<Void> cancelOrder(Long userId, Long orderId);

    Result<Void> confirmOrder(Long orderId);

    Result<Void> departOrder(Long orderId);

    Result<Void> completeOrder(Long orderId);

    Result<Void> refundOrder(Long userId, Long orderId);

    Result<Void> deleteOrder(Long userId, Long orderId);

    Result<List<OrderItem>> getOrderItems(Long orderId);
}