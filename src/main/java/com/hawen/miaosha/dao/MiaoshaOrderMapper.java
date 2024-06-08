package com.hawen.miaosha.dao;

import com.hawen.miaosha.domain.MiaoshaOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
public interface MiaoshaOrderMapper {
    @Select("select miaosha_order_id as id, user_id as userId, order_id as " +
            "orderId, item_id as itemId from miaosha_order " +
            "where user_id=#{userId} and item_id=#{itemId}")
    MiaoshaOrder findByUserIdItemId(@Param("userId") long userId,
                                    @Param("itemId") long itemId);

    @Insert("insert into miaosha_order(user_id, item_id, order_id) values " +
            "(#{userId}, #{itemId}, #{orderId})")
    int save(MiaoshaOrder miaoshaOrder);
}
