package cn.example.user.mapper;

import cn.example.common.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Select("SELECT * FROM review WHERE product_id = #{productId} AND status = 1 ORDER BY create_time DESC")
    List<Review> findByProductId(@Param("productId") Long productId);

    @Select("SELECT * FROM review WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Review> findByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO review(order_id, user_id, product_id, rating, content, images, status) " +
            "VALUES(#{orderId}, #{userId}, #{productId}, #{rating}, #{content}, #{images}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    @Select("SELECT COUNT(*) FROM review")
    int countAll();

    @Select("SELECT * FROM review WHERE id = #{id}")
    Review findById(@Param("id") Long id);

    @Delete("DELETE FROM review WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE review SET rating=#{rating}, content=#{content}, images=#{images} WHERE id=#{id}")
    int update(Review review);

    @Select("SELECT * FROM review ORDER BY create_time DESC")
    List<Review> findByAll();
}