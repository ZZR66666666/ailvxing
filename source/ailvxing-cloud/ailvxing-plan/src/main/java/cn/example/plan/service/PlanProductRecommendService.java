package cn.example.plan.service;

import cn.example.common.common.Result;
import cn.example.common.entity.PlanProductRecommend;

import java.util.List;

public interface PlanProductRecommendService {

    Result<List<PlanProductRecommend>> listByPlanId(Long planId);

    Result<Void> addRecommend(PlanProductRecommend recommend);

    Result<Void> updateStatus(Long id, Integer status);

    Result<Void> generateRecommendations(Long planId, String destination, String interestTags);
}