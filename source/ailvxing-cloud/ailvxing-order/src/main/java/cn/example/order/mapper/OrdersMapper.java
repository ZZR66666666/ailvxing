package cn.example.order.mapper;

import cn.example.common.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders findById(@Param("id") Long id);

    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Orders findByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Orders> findByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO orders(order_no, user_id, plan_id, total_amount, pay_amount, " +
            "pay_time, pay_method, status, contact_name, contact_phone, remark) " +
            "VALUES(#{orderNo}, #{userId}, #{planId}, #{totalAmount}, #{payAmount}, " +
            "#{payTime}, #{payMethod}, #{status}, #{contactName}, #{contactPhone}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Orders orders);

    @Update("UPDATE orders SET status=#{status}, pay_time=#{payTime}, pay_method=#{payMethod}, " +
            "pay_amount=#{payAmount} WHERE id=#{id}")
    int updateStatus(Orders orders);

    @Select("SELECT COUNT(*) FROM orders")
    int countAll();

    @Select("SELECT * FROM orders ORDER BY create_time DESC")
    List<Orders> findAll();

    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}