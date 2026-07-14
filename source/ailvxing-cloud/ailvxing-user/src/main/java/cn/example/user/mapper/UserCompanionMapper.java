package cn.example.user.mapper;

import cn.example.common.entity.UserCompanion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserCompanionMapper {

    @Select("SELECT * FROM user_companion WHERE user_id = #{userId}")
    List<UserCompanion> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_companion WHERE id = #{id}")
    UserCompanion findById(@Param("id") Long id);

    @Insert("INSERT INTO user_companion(user_id, name, relationship, phone, id_card) " +
            "VALUES(#{userId}, #{name}, #{relationship}, #{phone}, #{idCard})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCompanion companion);

    @Update("UPDATE user_companion SET name=#{name}, relationship=#{relationship}, " +
            "phone=#{phone}, id_card=#{idCard} WHERE id=#{id}")
    int update(UserCompanion companion);

    @Delete("DELETE FROM user_companion WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}