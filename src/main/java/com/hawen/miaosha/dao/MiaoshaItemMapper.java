package com.hawen.miaosha.dao;

import com.hawen.miaosha.domain.MiaoshaItem;
import org.apache.ibatis.annotations.*;

import java.util.*;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Mapper
public interface MiaoshaItemMapper {
    // 查询所有秒杀商品
    @Select("select it.*,mi.stock_count, mi.start_date, mi.end_date, " +
            "mi.miaosha_price from miaosha_item mi left join item_inf " +
            "it on mi.item_id = it.item_id")
    @Results(id = "itemMapper", value = {
            @Result(property = "itemId", column = "item_id"),
            @Result(property = "itemName", column = "item_name"),
            @Result(property = "title", column = "title"),
            @Result(property = "itemImg", column = "item_img"),
            @Result(property = "itemDetail", column = "item_detail"),
            @Result(property = "itemPrice", column = "item_price"),
            @Result(property = "stockNum", column = "stock_num"),
            @Result(property = "miaoshaPrice", column = "miaosha_price"),
            @Result(property = "stockCount", column = "stock_count"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date")
    })
    List<MiaoshaItem> findAll();

    // 根据商品ID查询秒杀商品
    @Select("select it.*,mi.stock_count, mi.start_date, mi.end_date, " +
            "mi.miaosha_price from miaosha_item mi left join item_inf it " +
            "on mi.item_id = it.item_id where it.item_id = #{itemId}")
    @ResultMap("itemMapper")
    MiaoshaItem findById(@Param("itemId") long itemId);

    // 更新miaosha_item表中的记录
    @Update("update miaosha_item set stock_count = stock_count - 1" +
            " where item_id = #{itemId}")
    int reduceStock(MiaoshaItem miaoshaItem);

}
