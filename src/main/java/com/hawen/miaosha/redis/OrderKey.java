package com.hawen.miaosha.redis;

public class OrderKey extends AbstractPrefix {
    public OrderKey(String prefix) {
        super(prefix);
    }

    // 代表永不过期
    public static OrderKey miaoshaOrderByUserIdAndItemId = new OrderKey("mOrderUserItem");
}
