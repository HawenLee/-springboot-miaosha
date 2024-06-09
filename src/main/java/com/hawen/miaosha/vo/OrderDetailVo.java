package com.hawen.miaosha.vo;


import com.hawen.miaosha.domain.MiaoshaItem;
import com.hawen.miaosha.domain.Order;
import com.hawen.miaosha.domain.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderDetailVo {
    private MiaoshaItem miaoshaItem;
    private Order order;
    private User user;

    public MiaoshaItem getMiaoshaItem() {
        return miaoshaItem;
    }

    public void setMiaoshaItem(MiaoshaItem miaoshaItem) {
        this.miaoshaItem = miaoshaItem;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "OrderDetailVo{" +
                "miaoshaItem=" + miaoshaItem +
                ", order=" + order +
                ", user=" + user +
                '}';
    }
}