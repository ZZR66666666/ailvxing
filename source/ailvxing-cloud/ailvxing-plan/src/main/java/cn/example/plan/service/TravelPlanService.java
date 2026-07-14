package cn.example.plan.service;

import cn.example.common.common.Result;
import cn.example.common.dto.PlanGenerateDTO;
import cn.example.common.entity.PlanFavorite;
import cn.example.common.entity.TravelPlan;

import java.util.List;

public interface TravelPlanService {

    Result<TravelPlan> generatePlan(Long userId, PlanGenerateDTO dto);

    Result<TravelPlan> getPlan(Long id);

    Result<List<TravelPlan>> listMyPlans(Long userId);

    Result<Void> favoritePlan(Long userId, Long planId);

    Result<Void> unfavoritePlan(Long userId, Long planId);

    Result<List<PlanFavorite>> listMyFavorites(Long userId);

    Result<TravelPlan> getSharedPlan(String shareCode);

    Result<String> sharePlan(Long userId, Long planId);

    Result<Void> deletePlan(Long userId, Long planId);

    Result<Void> updatePlan(Long userId, TravelPlan plan);
}