package cn.example.product.mapper;

import cn.example.common.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(@Param("id") Long id);

    @Select("SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC")
    List<Product> findAllOnSale();

    @Select("SELECT * FROM product WHERE (destination LIKE CONCAT('%', #{destination}, '%') OR name LIKE CONCAT('%', #{destination}, '%') OR includes LIKE CONCAT('%', #{destination}, '%') OR description LIKE CONCAT('%', #{destination}, '%')) AND status = 1")
    List<Product> findByDestination(@Param("destination") String destination);

    @Select("SELECT * FROM product WHERE type = #{type} AND status = 1")
    List<Product> findByType(@Param("type") String type);

    @Insert("INSERT INTO product(agency_id, name, type, destination, days, price, price_type, includes, " +
            "description, cover_image, stock, status, start_date, end_date) " +
            "VALUES(#{agencyId}, #{name}, #{type}, #{destination}, #{days}, #{price}, #{priceType}, #{includes}, " +
            "#{description}, #{coverImage}, #{stock}, #{status}, #{startDate}, #{endDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET name=#{name}, type=#{type}, destination=#{destination}, days=#{days}, " +
            "price=#{price}, price_type=#{priceType}, includes=#{includes}, description=#{description}, " +
            "cover_image=#{coverImage}, stock=#{stock}, status=#{status}, start_date=#{startDate}, " +
            "end_date=#{endDate} WHERE id=#{id}")
    int update(Product product);

    @Update("UPDATE product SET stock = stock - #{quantity}, sold_count = sold_count + #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE product SET stock = stock + #{quantity}, sold_count = GREATEST(sold_count - #{quantity}, 0) WHERE id = #{productId}")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM product ORDER BY create_time DESC")
    List<Product> findAll();
}