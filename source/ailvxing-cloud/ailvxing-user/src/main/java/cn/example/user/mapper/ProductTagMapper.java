package cn.example.user.mapper;

import cn.example.common.entity.ProductTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductTagMapper {

    @Select("SELECT * FROM product_tag ORDER BY category, id")
    List<ProductTag> findAll();

    @Select("SELECT * FROM product_tag WHERE category = #{category} ORDER BY id")
    List<ProductTag> findByCategory(@Param("category") String category);

    @Select("SELECT * FROM product_tag WHERE id = #{id}")
    ProductTag findById(@Param("id") Long id);

    @Insert("INSERT INTO product_tag(name, category) VALUES(#{name}, #{category})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductTag tag);

    @Delete("DELETE FROM product_tag WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT pt.* FROM product_tag pt " +
            "JOIN product_tag_relation ptr ON pt.id = ptr.tag_id " +
            "WHERE ptr.product_id = #{productId}")
    List<ProductTag> findByProductId(@Param("productId") Long productId);

    @Insert("INSERT INTO product_tag_relation(product_id, tag_id) VALUES(#{productId}, #{tagId})")
    int addTagRelation(@Param("productId") Long productId, @Param("tagId") Long tagId);

    @Delete("DELETE FROM product_tag_relation WHERE product_id = #{productId}")
    int deleteTagRelationsByProductId(@Param("productId") Long productId);

    @Select("SELECT DISTINCT pt.* FROM product_tag pt " +
            "JOIN product_tag_relation ptr ON pt.id = ptr.tag_id " +
            "JOIN product p ON ptr.product_id = p.id " +
            "WHERE p.destination LIKE CONCAT('%', #{destination}, '%') AND p.status = 1")
    List<ProductTag> findTagsByDestination(@Param("destination") String destination);
}