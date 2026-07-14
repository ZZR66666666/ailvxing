package cn.example.user.mapper;

import cn.example.common.entity.UserPreference;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserPreferenceMapper {

    @Select("SELECT * FROM user_preference WHERE user_id = #{userId}")
    UserPreference findByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO user_preference(user_id, budget_level, interest_tags, dietary_preference, transport_preference) " +
            "VALUES(#{userId}, #{budgetLevel}, #{interestTags}, #{dietaryPreference}, #{transportPreference})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserPreference preference);

    @Update("UPDATE user_preference SET budget_level=#{budgetLevel}, interest_tags=#{interestTags}, " +
            "dietary_preference=#{dietaryPreference}, transport_preference=#{transportPreference} WHERE user_id=#{userId}")
    int updateByUserId(UserPreference preference);
}