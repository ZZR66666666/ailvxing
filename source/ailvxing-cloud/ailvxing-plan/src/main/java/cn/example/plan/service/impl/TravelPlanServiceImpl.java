package cn.example.plan.service.impl;

import cn.example.common.common.Result;
import cn.example.common.dto.PlanGenerateDTO;
import cn.example.common.entity.AiCallLog;
import cn.example.common.entity.PlanFavorite;
import cn.example.common.entity.PlanProductRecommend;
import cn.example.common.entity.TravelPlan;
import cn.example.plan.mapper.AiCallLogMapper;
import cn.example.plan.config.AiConfig;
import cn.example.plan.mapper.PlanFavoriteMapper;
import cn.example.plan.mapper.TravelPlanMapper;
import cn.example.plan.service.DeepSeekService;
import cn.example.plan.service.PlanProductRecommendService;
import cn.example.plan.service.QdrantService;
import cn.example.plan.service.TravelPlanService;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelPlanServiceImpl implements TravelPlanService {

    private final TravelPlanMapper travelPlanMapper;
    private final AiConfig aiConfig;
    private final PlanFavoriteMapper planFavoriteMapper;
    private final DeepSeekService deepSeekService;
    private final PlanProductRecommendService planProductRecommendService;
    private final AiCallLogMapper aiCallLogMapper;
    private final QdrantService qdrantService;

    @Override
    public Result<TravelPlan> generatePlan(Long userId, PlanGenerateDTO dto) {
        TravelPlan plan = new TravelPlan();
        plan.setUserId(userId);
        plan.setTitle(dto.getDestination() + dto.getDays() + "日游");
        plan.setDestination(dto.getDestination());
        plan.setDays(dto.getDays());
        plan.setBudget(dto.getBudget());
        plan.setBudgetLevel(dto.getBudgetLevel() != null ? dto.getBudgetLevel() : "comfort");
        plan.setInterestTags(dto.getInterestTags());
        plan.setCompanionIds(dto.getCompanionIds());
        plan.setStatus(1);
        plan.setShareCode(IdUtil.simpleUUID());

        // 从 Qdrant 向量库搜索相关目的地信息，增强 AI 提示词
        String qdrantContext = "";
        try {
            var results = qdrantService.search(dto.getDestination() + " " + (dto.getInterestTags() != null ? dto.getInterestTags() : ""), 5);
            if (results != null && !results.isEmpty()) {
                StringBuilder ctx = new StringBuilder("\n【知识库参考信息——请基于以下真实景点数据规划行程】\n");
                for (int i = 0; i < results.size(); i++) {
                    var r = results.get(i);
                    ctx.append(i + 1).append(". ").append(r.getName()).append("\n");
                }
                qdrantContext = ctx.toString();
                log.info("Qdrant匹配{}个目的地", results.size());
            }
        } catch (Exception e) {
            log.warn("Qdrant不可用，跳过增强: {}", e.getMessage());
        }

        String prompt = buildPrompt(dto, qdrantContext);
        long startTime = System.currentTimeMillis();
        String aiResult = null;
        boolean success = false;
        String errorMsg = null;

        try {
            aiResult = deepSeekService.chat(prompt);
            success = (aiResult != null);
            if (!success) {
                errorMsg = "AI返回空结果";
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            log.error("AI规划生成异常: {}", errorMsg);
        }

        long duration = System.currentTimeMillis() - startTime;

        AiCallLog callLog = new AiCallLog();
        callLog.setUserId(userId);
        callLog.setModel(aiConfig.getModel());
        callLog.setPrompt(prompt.length() > 500 ? prompt.substring(0, 500) : prompt);
        callLog.setDurationMs((int) duration);
        callLog.setIsSuccess(success ? 1 : 0);
        callLog.setErrorMsg(errorMsg);
        if (aiResult != null) {
            callLog.setResponse(aiResult.length() > 1000 ? aiResult.substring(0, 1000) : aiResult);
        }
        aiCallLogMapper.insert(callLog);

        if (aiResult != null) {
            plan.setPlanDetail(aiResult);
            plan.setAiModel(aiConfig.getModel());
        } else {
            plan.setPlanDetail("{\"message\":\"AI规划生成失败，请稍后重试\",\"days\":[]}");
            plan.setAiModel("deepseek-chat-failed");
        }

        travelPlanMapper.insert(plan);

        planProductRecommendService.generateRecommendations(plan.getId(), plan.getDestination(), plan.getInterestTags());

        return Result.success(plan);
    }

    private String buildPrompt(PlanGenerateDTO dto, String qdrantContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("请为我规划一次旅行，要求如下：\n");
        if (!qdrantContext.isEmpty()) {
            sb.append(qdrantContext);
            sb.append("\n");
        }
        sb.append("目的地：").append(dto.getDestination()).append("\n");
        sb.append("天数：").append(dto.getDays()).append("天\n");
        if (dto.getBudget() != null) {
            sb.append("预算：").append(dto.getBudget()).append("元\n");
        }
        if (dto.getBudgetLevel() != null) {
            String levelName = switch (dto.getBudgetLevel()) {
                case "economy" -> "经济型（注重性价比，选择实惠的住宿和交通）";
                case "comfort" -> "舒适型（平衡品质与价格，选择中档住宿和交通）";
                case "luxury" -> "豪华型（追求极致体验，选择高端住宿和专属服务）";
                default -> dto.getBudgetLevel();
            };
            sb.append("预算等级：").append(levelName).append("\n");
        }
        if (dto.getInterestTags() != null) {
            sb.append("兴趣偏好：").append(dto.getInterestTags()).append("\n");
        }
        if (dto.getCompanionIds() != null && !dto.getCompanionIds().isEmpty()) {
            sb.append("同行人员ID：").append(dto.getCompanionIds()).append("（请考虑多人出行的需求）\n");
        }
        sb.append("\n【预算计算规则——必须严格遵守，否则输出无效】\n");
        sb.append("1. 每一项活动都必须在cost字段填写具体金额（人民币元），免费项目填0，不要留空\n");
        sb.append("2. 每一天的dayBudget = 该天所有activities的cost之和，一毛钱都不能差\n");
        sb.append("3. totalBudget = 所有天的dayBudget之和，一毛钱都不能差\n");
        sb.append("4. budgetDetail必须列出：交通费总和 + 住宿费总和 + 餐饮费总和 + 门票活动费总和 + 其他费总和 = totalBudget，每一项后面写具体金额\n");
        if (dto.getBudget() != null) {
            sb.append("5. totalBudget必须小于等于").append(dto.getBudget()).append("元预算上限\n");
        }
        sb.append("\n请按以下JSON格式输出旅行规划（不要输出其他内容，只输出JSON）：\n");
        sb.append("{\n");
        sb.append("  \"title\": \"行程标题\",\n");
        sb.append("  \"summary\": \"行程概述\",\n");
        sb.append("  \"tips\": \"旅行注意事项\",\n");
        sb.append("  \"totalBudget\": D1dayBudget+D2dayBudget+...+DNdayBudget,\n");
        sb.append("  \"budgetDetail\": \"交通费总和1+交通费总和2+...=交通总计XX元 + 住宿费总和=住宿总计XX元 + 餐饮费总和=餐饮总计XX元 + 门票活动费总和=门票总计XX元 + 其他费总和=其他总计XX元 = totalBudget元\",\n");
        sb.append("  \"days\": [\n");
        sb.append("    {\n");
        sb.append("      \"day\": 1,\n");
        sb.append("      \"title\": \"第1天 标题\",\n");
        sb.append("      \"dayBudget\": 当日所有activities的cost之和,\n");
        sb.append("      \"activities\": [\n");
        sb.append("        {\"time\": \"08:00\", \"activity\": \"活动内容\", \"location\": \"地点\", \"duration\": \"1小时\", \"cost\": 0, \"type\": \"scenic/restaurant/hotel/transport\"}\n");
        sb.append("      ]\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Result<TravelPlan> getPlan(Long id) {
        TravelPlan plan = travelPlanMapper.findById(id);
        if (plan == null) {
            return Result.error(404, "行程不存在");
        }
        return Result.success(plan);
    }

    @Override
    public Result<List<TravelPlan>> listMyPlans(Long userId) {
        return Result.success(travelPlanMapper.findByUserId(userId));
    }

    @Override
    public Result<Void> favoritePlan(Long userId, Long planId) {
        int count = planFavoriteMapper.countByUserAndPlan(userId, planId);
        if (count > 0) {
            return Result.error(400, "已收藏该行程");
        }
        TravelPlan plan = travelPlanMapper.findById(planId);
        PlanFavorite favorite = new PlanFavorite();
        favorite.setUserId(userId);
        favorite.setPlanId(planId);
        favorite.setPlanTitle(plan != null ? plan.getTitle() : "");
        planFavoriteMapper.insert(favorite);
        return Result.success();
    }

    @Override
    public Result<Void> unfavoritePlan(Long userId, Long planId) {
        planFavoriteMapper.deleteByUserAndPlan(userId, planId);
        return Result.success();
    }

    @Override
    public Result<List<PlanFavorite>> listMyFavorites(Long userId) {
        return Result.success(planFavoriteMapper.findByUserId(userId));
    }

    @Override
    public Result<TravelPlan> getSharedPlan(String shareCode) {
        TravelPlan plan = travelPlanMapper.findByShareCode(shareCode);
        if (plan == null) {
            return Result.error(404, "分享的行程不存在或已失效");
        }
        return Result.success(plan);
    }

    @Override
    public Result<String> sharePlan(Long userId, Long planId) {
        TravelPlan plan = travelPlanMapper.findById(planId);
        if (plan == null) {
            return Result.error(404, "行程不存在");
        }
        if (plan.getShareCode() == null) {
            String shareCode = IdUtil.simpleUUID();
            plan.setShareCode(shareCode);
            travelPlanMapper.update(plan);
        }
        return Result.success(plan.getShareCode());
    }

    @Override
    public Result<Void> deletePlan(Long userId, Long planId) {
        TravelPlan plan = travelPlanMapper.findById(planId);
        if (plan == null) {
            return Result.error(404, "行程不存在");
        }
        if (!plan.getUserId().equals(userId)) {
            return Result.error(403, "无权删除此行程");
        }
        planFavoriteMapper.deleteByPlanId(planId);
        travelPlanMapper.deleteById(planId);
        return Result.success();
    }

    @Override
    public Result<Void> updatePlan(Long userId, TravelPlan plan) {
        TravelPlan existing = travelPlanMapper.findById(plan.getId());
        if (existing == null) {
            return Result.error(404, "行程不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权修改此行程");
        }
        existing.setTitle(plan.getTitle());
        existing.setPlanDetail(plan.getPlanDetail());
        existing.setBudgetLevel(plan.getBudgetLevel());
        travelPlanMapper.update(existing);
        return Result.success();
    }
}