package cn.example.user.mapper;

import cn.example.common.entity.TravelPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TravelPlanMapper {

    @Select("SELECT * FROM travel_plan WHERE id = #{id}")
    TravelPlan findById(@Param("id") Long id);

    @Select("SELECT * FROM travel_plan WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<TravelPlan> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM travel_plan WHERE share_code = #{shareCode}")
    TravelPlan findByShareCode(@Param("shareCode") String shareCode);

    @Insert("INSERT INTO travel_plan(user_id, title, destination, days, budget, budget_level, " +
            "interest_tags, companion_ids, plan_detail, ai_model, status, share_code) " +
            "VALUES(#{userId}, #{title}, #{destination}, #{days}, #{budget}, #{budgetLevel}, " +
            "#{interestTags}, #{companionIds}, #{planDetail}, #{aiModel}, #{status}, #{shareCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TravelPlan plan);

    @Update("UPDATE travel_plan SET title=#{title}, plan_detail=#{planDetail}, " +
            "budget_level=#{budgetLevel}, status=#{status}, share_code=#{shareCode} WHERE id=#{id}")
    int update(TravelPlan plan);

    @Delete("DELETE FROM travel_plan WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM travel_plan")
    int countAll();
}