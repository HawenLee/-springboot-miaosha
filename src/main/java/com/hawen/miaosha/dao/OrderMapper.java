package com.hawen.miaosha.dao;

import com.hawen.miaosha.domain.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Mapper
public interface OrderMapper {

    @Insert("insert into order_inf (user_id, item_id, item_name, order_num, order_price, order_channel, order_status, " +
            "create_date) values (#{userId},#{itemId},#{itemName},#{orderNum},#{orderPrice},#{orderChannel},#{status},#{createDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long save(Order order);

    @Select("select order_id as id, user_id as userId, item_id as itemId, " +
            "item_name as itemName, order_num as orderNum, order_price as " +
            "orderPrice, order_channel as orderChannel, order_status as " +
            "status, create_date as createDate, pay_date as payDate from " +
            "order_inf where order_id = #{param1} and user_id = #{param2}")
    Order findByIdOwerId(long  orderId, long userId);



}
