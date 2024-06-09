package com.hawen.miaosha.domain;

import lombok.Builder;
import lombok.Data;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Data
@Builder
public class MiaoshaOrder {
    private Long id;
    private Long userId;
    private Long orderId;
    private Long itemId;
}
