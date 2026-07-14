package cn.example.order.controller;

import cn.example.common.common.Result;
import cn.example.common.entity.Orders;
import cn.example.order.mapper.OrdersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrdersMapper ordersMapper;

    @GetMapping("/orders")
    public Result<List<Orders>> listAllOrders() {
        return Result.success(ordersMapper.findAll());
    }

    @DeleteMapping("/order/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        ordersMapper.deleteById(id);
        return Result.success();
    }
}
