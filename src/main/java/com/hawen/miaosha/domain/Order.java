package com.hawen.miaosha.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Data
@Builder
public class Order {
    private Long id;
    private Long userId;
    private Long itemId;
    private String itemName;
    private Integer orderNum;
    private Double orderPrice;
    private Integer orderChannel;
    private Integer status;
    private Date createDate;
    private Date payDate;
}
