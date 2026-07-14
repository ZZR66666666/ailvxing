package cn.example.user.mapper;

import cn.example.common.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    @Insert("INSERT INTO order_item(order_id, product_id, product_name, product_type, " +
            "price, quantity, travel_date) " +
            "VALUES(#{orderId}, #{productId}, #{productName}, #{productType}, " +
            "#{price}, #{quantity}, #{travelDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem orderItem);

    @Insert("<script>" +
            "INSERT INTO order_item(order_id, product_id, product_name, product_type, price, quantity, travel_date) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.orderId}, #{item.productId}, #{item.productName}, #{item.productType}, #{item.price}, #{item.quantity}, #{item.travelDate})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<OrderItem> items);

    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") Long orderId);
}