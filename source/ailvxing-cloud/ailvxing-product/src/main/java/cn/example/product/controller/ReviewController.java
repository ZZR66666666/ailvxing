package cn.example.product.controller;

import cn.example.common.common.Result;
import cn.example.common.entity.Review;
import cn.example.product.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评价管理")
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "提交评价")
    @PostMapping
    public Result<Void> addReview(HttpServletRequest request, @RequestBody Review review) {
        return reviewService.addReview(getUserId(request), review);
    }

    @Operation(summary = "查看产品评价")
    @GetMapping("/product/{productId}")
    public Result<List<Review>> listByProduct(@PathVariable Long productId) {
        return reviewService.listByProductId(productId);
    }

    @Operation(summary = "我的评价列表")
    @GetMapping("/my")
    public Result<List<Review>> listMyReviews(HttpServletRequest request) {
        return reviewService.listByUserId(getUserId(request));
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/{reviewId}")
    public Result<Void> deleteReview(HttpServletRequest request, @PathVariable Long reviewId) {
        return reviewService.deleteReview(getUserId(request), reviewId);
    }

    @Operation(summary = "编辑评价")
    @PutMapping
    public Result<Void> updateReview(HttpServletRequest request, @RequestBody Review review) {
        return reviewService.updateReview(getUserId(request), review);
    }
}