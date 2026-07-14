package cn.example.plan.mapper;

import cn.example.common.entity.PlanShare;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlanShareMapper {

    @Select("SELECT * FROM plan_share WHERE share_code = #{shareCode}")
    PlanShare findByShareCode(@Param("shareCode") String shareCode);

    @Select("SELECT * FROM plan_share WHERE plan_id = #{planId} AND share_user_id = #{userId} AND status = 1")
    PlanShare findActiveByPlanAndUser(@Param("planId") Long planId, @Param("userId") Long userId);

    @Select("SELECT * FROM plan_share WHERE share_user_id = #{userId} ORDER BY create_time DESC")
    List<PlanShare> findByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO plan_share(plan_id, share_user_id, share_code, view_count, expire_time, status) " +
            "VALUES(#{planId}, #{shareUserId}, #{shareCode}, #{viewCount}, #{expireTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PlanShare share);

    @Update("UPDATE plan_share SET view_count = view_count + 1 WHERE share_code = #{shareCode}")
    int incrementViewCount(@Param("shareCode") String shareCode);

    @Update("UPDATE plan_share SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}