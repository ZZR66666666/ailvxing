package cn.example.plan.service.impl;

import cn.example.common.common.Result;
import cn.example.common.entity.PlanProductRecommend;
import cn.example.common.entity.Product;
import cn.example.plan.client.ProductClient;
import cn.example.plan.mapper.PlanProductRecommendMapper;
import cn.example.plan.service.PlanProductRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanProductRecommendServiceImpl implements PlanProductRecommendService {

    private final PlanProductRecommendMapper planProductRecommendMapper;
    private final ProductClient productClient;   // 改用 Feign 远程调用

    @Override
    public Result<List<PlanProductRecommend>> listByPlanId(Long planId) {
        return Result.success(planProductRecommendMapper.findByPlanId(planId));
    }

    @Override
    public Result<Void> addRecommend(PlanProductRecommend recommend) {
        planProductRecommendMapper.insert(recommend);
        return Result.success();
    }

    @Override
    public Result<Void> updateStatus(Long id, Integer status) {
        planProductRecommendMapper.updateStatus(id, status);
        return Result.success();
    }

    @Override
    public Result<Void> generateRecommendations(Long planId, String destination, String interestTags) {
        List<PlanProductRecommend> existing = planProductRecommendMapper.findByPlanId(planId);
        if (!existing.isEmpty()) {
            log.info("行程{}已有推荐产品，跳过生成", planId);
            return Result.success();
        }

        // 通过 Feign 调用产品服务获取全部产品
        Result<List<Product>> result = productClient.listAll();
        if (result == null || result.getData() == null) return Result.success();
        List<Product> products = result.getData();

        String[] tags = interestTags != null ? interestTags.split(",") : new String[0];
        for (Product p : products) {
            if (p.getStatus() != 1 || (p.getStock() != null && p.getStock() <= 0)) continue;

            // 只推荐目的地匹配的产品
            if (p.getDestination() == null || !p.getDestination().contains(destination)) {
                continue;
            }

            StringBuilder reason = new StringBuilder();
            reason.append("目的地匹配");
            for (String tag : tags) {
                String t = tag.trim().toLowerCase();
                if (t.isEmpty()) continue;
                String nameAndDesc = (p.getName() != null ? p.getName() : "") + " " + (p.getDescription() != null ? p.getDescription() : "");
                if (nameAndDesc.toLowerCase().contains(t)) {
                    reason.append(" + 兴趣匹配（").append(t).append("）");
                    break;
                }
            }

            PlanProductRecommend recommend = new PlanProductRecommend();
            recommend.setPlanId(planId);
            recommend.setProductId(p.getId());
            recommend.setMatchReason(reason.toString());
            recommend.setStatus(0);
            planProductRecommendMapper.insert(recommend);
        }

        log.info("为行程{}生成了{}条产品推荐", planId, products.size());
        return Result.success();
    }
}
