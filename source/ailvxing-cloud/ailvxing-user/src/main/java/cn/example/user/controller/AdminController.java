package cn.example.user.controller;

import cn.example.common.common.Result;
import cn.example.common.entity.AiCallLog;
import cn.example.common.entity.OrderItem;
import cn.example.common.entity.Orders;
import cn.example.common.entity.PlanProductRecommend;
import cn.example.common.entity.Product;
import cn.example.common.entity.Review;
import cn.example.common.entity.User;
import cn.example.user.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "后台运营监控")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AiCallLogMapper aiCallLogMapper;
    private final ProductMapper productMapper;
    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final ReviewMapper reviewMapper;
    private final TravelPlanMapper travelPlanMapper;
    private final PlanProductRecommendMapper planProductRecommendMapper;
    private final UserMapper userMapper;

    @Operation(summary = "AI调用统计")
    @GetMapping("/stats/ai")
    public Result<Map<String, Object>> aiStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("successCount", aiCallLogMapper.countSuccess());
        stats.put("failedCount", aiCallLogMapper.countFailed());
        return Result.success(stats);
    }

    @Operation(summary = "最近AI调用日志")
    @GetMapping("/stats/ai/logs")
    public Result<List<AiCallLog>> recentAiLogs(@RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(aiCallLogMapper.findRecent(limit));
    }

    @Operation(summary = "热门目的地统计")
    @GetMapping("/stats/destinations")
    public Result<List<Map<String, Object>>> hotDestinations() {
        List<Product> products = productMapper.findAllOnSale();
        Map<String, Long> destCount = products.stream()
                .collect(Collectors.groupingBy(Product::getDestination, Collectors.counting()));
        List<Map<String, Object>> result = destCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("destination", e.getKey());
                    m.put("productCount", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @Operation(summary = "产品销售排行")
    @GetMapping("/stats/product-ranking")
    public Result<List<Map<String, Object>>> productRanking() {
        List<Product> products = productMapper.findAllOnSale();
        List<Map<String, Object>> result = products.stream()
                .sorted(Comparator.comparingInt(Product::getSoldCount).reversed())
                .limit(10)
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("productId", p.getId());
                    m.put("name", p.getName());
                    m.put("destination", p.getDestination());
                    m.put("soldCount", p.getSoldCount());
                    m.put("stock", p.getStock());
                    return m;
                })
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @Operation(summary = "产品推荐转化率")
    @GetMapping("/stats/recommend-conversion")
    public Result<Map<String, Object>> recommendConversion() {
        Map<String, Object> stats = new HashMap<>();
        int totalRecommends = 0;
        int viewed = 0;
        int ordered = 0;
        List<Product> products = productMapper.findAllOnSale();
        for (Product p : products) {
            List<PlanProductRecommend> recs = planProductRecommendMapper.findByProductId(p.getId());
            totalRecommends += recs.size();
            viewed += (int) recs.stream().filter(r -> r.getStatus() >= 1).count();
            ordered += (int) recs.stream().filter(r -> r.getStatus() == 2).count();
        }
        stats.put("totalRecommends", totalRecommends);
        stats.put("viewedCount", viewed);
        stats.put("orderedCount", ordered);
        stats.put("viewRate", totalRecommends > 0 ? String.format("%.1f%%", viewed * 100.0 / totalRecommends) : "0%");
        stats.put("conversionRate", viewed > 0 ? String.format("%.1f%%", ordered * 100.0 / viewed) : "0%");
        return Result.success(stats);
    }

    @Operation(summary = "概览数据")
    @GetMapping("/stats/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("productCount", productMapper.findAllOnSale().size());
        stats.put("aiSuccessCount", aiCallLogMapper.countSuccess());
        stats.put("aiFailedCount", aiCallLogMapper.countFailed());
        stats.put("planCount", travelPlanMapper.countAll());
        stats.put("orderCount", ordersMapper.countAll());
        stats.put("reviewCount", reviewMapper.countAll());
        return Result.success(stats);
    }

    @Operation(summary = "产品下架")
    @PutMapping("/product/{id}/offline")
    public Result<Void> offlineProduct(@PathVariable Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return Result.error(404, "产品不存在");
        }
        product.setStatus(0);
        productMapper.update(product);
        return Result.success();
    }

    @Operation(summary = "产品上架")
    @PutMapping("/product/{id}/online")
    public Result<Void> onlineProduct(@PathVariable Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return Result.error(404, "产品不存在");
        }
        product.setStatus(1);
        productMapper.update(product);
        return Result.success();
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("/product/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return Result.error(404, "产品不存在");
        }
        productMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "所有订单列表（后台管理）")
    @GetMapping("/orders")
    public Result<List<Orders>> listAllOrders() {
        List<Orders> orders = ordersMapper.findAll();
        for (Orders order : orders) {
            order.setItems(orderItemMapper.findByOrderId(order.getId()));
        }
        return Result.success(orders);
    }

    @Operation(summary = "所有用户列表（后台管理）")
    @GetMapping("/users")
    public Result<List<User>> listAllUsers() {
        List<User> users = userMapper.findAll();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/user/{id}/status")
    public Result<Void> toggleUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setStatus(status);
        userMapper.update(user);
        return Result.success();
    }

    @Operation(summary = "所有评价列表（后台管理）")
    @GetMapping("/reviews")
    public Result<List<Review>> listAllReviews() {
        return Result.success(reviewMapper.findByAll());
    }

    @Operation(summary = "删除评价（后台管理）")
    @DeleteMapping("/review/{id}")
    public Result<Void> adminDeleteReview(@PathVariable Long id) {
        Review review = reviewMapper.findById(id);
        if (review == null) {
            return Result.error(404, "评价不存在");
        }
        reviewMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "编辑产品（后台管理）")
    @PutMapping("/product")
    public Result<Void> adminUpdateProduct(@RequestBody Product product) {
        Product existing = productMapper.findById(product.getId());
        if (existing == null) {
            return Result.error(404, "产品不存在");
        }
        productMapper.update(product);
        return Result.success();
    }

    @Operation(summary = "删除订单（后台管理）")
    @DeleteMapping("/order/{id}")
    public Result<Void> adminDeleteOrder(@PathVariable Long id) {
        Orders order = ordersMapper.findById(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        orderItemMapper.deleteByOrderId(id);
        ordersMapper.deleteById(id);
        return Result.success();
    }
}