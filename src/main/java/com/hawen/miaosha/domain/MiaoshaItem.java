package com.hawen.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/7
 */
@Data
public class MiaoshaItem extends Item{
    private Long id;
    private Long itemId;
    private double miaoshaPrice;
    private Integer stockNum;
    private Date startDate;
    private Date endDate;
}
