package com.hawen.miaosha.domain;

import lombok.Data;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/7
 */
@Data
public class Item {
    private Long id;
    private String itemName;
    private String title;
    private String itemImg;
    private String itemDetail;
    private Double itemPrice;
    private Integer stockNum;
}
