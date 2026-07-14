package cn.example.user.mapper;

import cn.example.common.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> findAll();

    @Insert("INSERT INTO user(username, password, nickname, phone, email, avatar, role, status) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{email}, #{avatar}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET nickname=#{nickname}, phone=#{phone}, email=#{email}, " +
            "avatar=#{avatar}, status=#{status} WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE user SET nickname=#{nickname}, phone=#{phone}, email=#{email} WHERE id=#{id}")
    int updateProfile(User user);

    @Update("UPDATE user SET password=#{password} WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}