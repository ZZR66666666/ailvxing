package cn.example.user.mapper;

import cn.example.common.entity.PlanProductRecommend;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlanProductRecommendMapper {

    @Select("SELECT * FROM plan_product_recommend WHERE plan_id = #{planId}")
    List<PlanProductRecommend> findByPlanId(@Param("planId") Long planId);

    @Select("SELECT * FROM plan_product_recommend WHERE plan_id = #{planId} AND status = #{status}")
    List<PlanProductRecommend> findByPlanIdAndStatus(@Param("planId") Long planId, @Param("status") Integer status);

    @Select("SELECT * FROM plan_product_recommend WHERE product_id = #{productId}")
    List<PlanProductRecommend> findByProductId(@Param("productId") Long productId);

    @Insert("INSERT INTO plan_product_recommend(plan_id, product_id, plan_day_id, match_reason, status) " +
            "VALUES(#{planId}, #{productId}, #{planDayId}, #{matchReason}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PlanProductRecommend recommend);

    @Update("UPDATE plan_product_recommend SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}