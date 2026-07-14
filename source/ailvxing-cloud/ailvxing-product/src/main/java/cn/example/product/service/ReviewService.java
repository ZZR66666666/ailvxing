package cn.example.product.service;

import cn.example.common.common.Result;
import cn.example.common.entity.Review;

import java.util.List;

public interface ReviewService {

    Result<Void> addReview(Long userId, Review review);

    Result<List<Review>> listByProductId(Long productId);

    Result<List<Review>> listByUserId(Long userId);

    Result<Void> deleteReview(Long userId, Long reviewId);

    Result<Void> updateReview(Long userId, Review review);
}