package cn.example.plan.mapper;

import cn.example.common.entity.PlanFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlanFavoriteMapper {

    @Select("SELECT * FROM plan_favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PlanFavorite> findByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM plan_favorite WHERE user_id = #{userId} AND plan_id = #{planId}")
    int countByUserAndPlan(@Param("userId") Long userId, @Param("planId") Long planId);

    @Insert("INSERT INTO plan_favorite(user_id, plan_id, plan_title) VALUES(#{userId}, #{planId}, #{planTitle})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PlanFavorite favorite);

    @Delete("DELETE FROM plan_favorite WHERE user_id = #{userId} AND plan_id = #{planId}")
    int deleteByUserAndPlan(@Param("userId") Long userId, @Param("planId") Long planId);

    @Delete("DELETE FROM plan_favorite WHERE plan_id = #{planId}")
    int deleteByPlanId(@Param("planId") Long planId);
}