package cn.example.order.controller;

import cn.example.common.common.Result;
import cn.example.common.dto.OrderCreateDTO;
import cn.example.common.entity.OrderItem;
import cn.example.common.entity.Orders;
import cn.example.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<String> createOrder(
            HttpServletRequest request,
            @Valid @RequestBody OrderCreateDTO dto) {
        return orderService.createOrder(getUserId(request), dto);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Orders> getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/my")
    public Result<List<Orders>> listMyOrders(HttpServletRequest request) {
        return orderService.listMyOrders(getUserId(request));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay/{orderId}")
    public Result<Void> payOrder(
            @PathVariable Long orderId,
            @RequestParam String payMethod) {
        return orderService.payOrder(orderId, payMethod);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(HttpServletRequest request, @PathVariable Long orderId) {
        return orderService.cancelOrder(getUserId(request), orderId);
    }

    @Operation(summary = "确认订单（旅行社）")
    @PostMapping("/confirm/{orderId}")
    public Result<Void> confirmOrder(@PathVariable Long orderId) {
        return orderService.confirmOrder(orderId);
    }

    @Operation(summary = "出团（旅行社）")
    @PostMapping("/depart/{orderId}")
    public Result<Void> departOrder(@PathVariable Long orderId) {
        return orderService.departOrder(orderId);
    }

    @Operation(summary = "完成订单")
    @PostMapping("/complete/{orderId}")
    public Result<Void> completeOrder(@PathVariable Long orderId) {
        return orderService.completeOrder(orderId);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/{orderId}")
    public Result<Void> refundOrder(HttpServletRequest request, @PathVariable Long orderId) {
        return orderService.refundOrder(getUserId(request), orderId);
    }

    @Operation(summary = "获取订单明细")
    @GetMapping("/{orderId}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{orderId}")
    public Result<Void> deleteOrder(HttpServletRequest request, @PathVariable Long orderId) {
        return orderService.deleteOrder(getUserId(request), orderId);
    }
}