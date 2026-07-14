package cn.example.plan.controller;

import cn.example.common.common.Result;
import cn.example.common.dto.PlanGenerateDTO;
import cn.example.common.entity.PlanFavorite;
import cn.example.common.entity.PlanProductRecommend;
import cn.example.common.entity.TravelPlan;
import cn.example.plan.service.PlanProductRecommendService;
import cn.example.plan.service.TravelPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "智能旅行规划")
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class TravelPlanController {

    private final TravelPlanService travelPlanService;
    private final PlanProductRecommendService planProductRecommendService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "生成旅行规划")
    @PostMapping("/generate")
    public Result<TravelPlan> generatePlan(
            HttpServletRequest request,
            @Valid @RequestBody PlanGenerateDTO dto) {
        return travelPlanService.generatePlan(getUserId(request), dto);
    }

    @Operation(summary = "获取规划详情")
    @GetMapping("/{id}")
    public Result<TravelPlan> getPlan(@PathVariable Long id) {
        return travelPlanService.getPlan(id);
    }

    @Operation(summary = "我的行程列表")
    @GetMapping("/my")
    public Result<List<TravelPlan>> listMyPlans(HttpServletRequest request) {
        return travelPlanService.listMyPlans(getUserId(request));
    }

    @Operation(summary = "收藏行程")
    @PostMapping("/favorite/{planId}")
    public Result<Void> favoritePlan(
            HttpServletRequest request,
            @PathVariable Long planId) {
        return travelPlanService.favoritePlan(getUserId(request), planId);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorite/{planId}")
    public Result<Void> unfavoritePlan(
            HttpServletRequest request,
            @PathVariable Long planId) {
        return travelPlanService.unfavoritePlan(getUserId(request), planId);
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/favorites")
    public Result<List<PlanFavorite>> listMyFavorites(HttpServletRequest request) {
        return travelPlanService.listMyFavorites(getUserId(request));
    }

    @Operation(summary = "分享行程")
    @PostMapping("/share/{planId}")
    public Result<String> sharePlan(
            HttpServletRequest request,
            @PathVariable Long planId) {
        return travelPlanService.sharePlan(getUserId(request), planId);
    }

    @Operation(summary = "查看分享的行程")
    @GetMapping("/shared/{shareCode}")
    public Result<TravelPlan> getSharedPlan(@PathVariable String shareCode) {
        return travelPlanService.getSharedPlan(shareCode);
    }

    @Operation(summary = "获取行程推荐产品")
    @GetMapping("/{planId}/recommendations")
    public Result<List<PlanProductRecommend>> getRecommendations(@PathVariable Long planId) {
        return planProductRecommendService.listByPlanId(planId);
    }

    @Operation(summary = "更新推荐产品状态")
    @PutMapping("/recommend/{id}/status")
    public Result<Void> updateRecommendStatus(@PathVariable Long id, @RequestParam Integer status) {
        return planProductRecommendService.updateStatus(id, status);
    }

    @Operation(summary = "重新生成行程（切换预算等级）")
    @PostMapping("/{planId}/regenerate")
    public Result<TravelPlan> regeneratePlan(
            HttpServletRequest request,
            @PathVariable Long planId,
            @RequestParam String budgetLevel) {
        TravelPlan plan = travelPlanService.getPlan(planId).getData();
        if (plan == null) {
            return Result.error(404, "行程不存在");
        }
        PlanGenerateDTO dto = new PlanGenerateDTO();
        dto.setDestination(plan.getDestination());
        dto.setDays(plan.getDays());
        dto.setBudget(plan.getBudget());
        dto.setBudgetLevel(budgetLevel);
        dto.setInterestTags(plan.getInterestTags());
        dto.setCompanionIds(plan.getCompanionIds());
        return travelPlanService.generatePlan(getUserId(request), dto);
    }

    @Operation(summary = "删除行程规划")
    @DeleteMapping("/{planId}")
    public Result<Void> deletePlan(HttpServletRequest request, @PathVariable Long planId) {
        return travelPlanService.deletePlan(getUserId(request), planId);
    }

    @Operation(summary = "编辑行程规划")
    @PutMapping
    public Result<Void> updatePlan(HttpServletRequest request, @RequestBody TravelPlan plan) {
        return travelPlanService.updatePlan(getUserId(request), plan);
    }
}