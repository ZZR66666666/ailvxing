package cn.example.product.service.impl;

import cn.example.common.common.Result;
import cn.example.common.entity.Review;
import cn.example.product.mapper.ReviewMapper;
import cn.example.product.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    @Override
    public Result<Void> addReview(Long userId, Review review) {
        review.setUserId(userId);
        review.setStatus(1);
        reviewMapper.insert(review);
        return Result.success();
    }

    @Override
    public Result<List<Review>> listByProductId(Long productId) {
        return Result.success(reviewMapper.findByProductId(productId));
    }

    @Override
    public Result<List<Review>> listByUserId(Long userId) {
        return Result.success(reviewMapper.findByUserId(userId));
    }

    @Override
    public Result<Void> deleteReview(Long userId, Long reviewId) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            return Result.error(404, "评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            return Result.error(403, "无权删除此评价");
        }
        reviewMapper.deleteById(reviewId);
        return Result.success();
    }

    @Override
    public Result<Void> updateReview(Long userId, Review review) {
        Review existing = reviewMapper.findById(review.getId());
        if (existing == null) {
            return Result.error(404, "评价不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权修改此评价");
        }
        reviewMapper.update(review);
        return Result.success();
    }
}