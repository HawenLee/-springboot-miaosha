package com.hawen.miaosha.vo;


import com.hawen.miaosha.domain.MiaoshaItem;
import com.hawen.miaosha.domain.User;

public class ItemDetailVo {
    private int remainSeconds = 0;
    private int leftSeconds = 0;
    private MiaoshaItem miaoshaItem;
    private User user;

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public int getLeftSeconds() {
        return leftSeconds;
    }

    public void setLeftSeconds(int leftSeconds) {
        this.leftSeconds = leftSeconds;
    }

    public MiaoshaItem getMiaoshaItem() {
        return miaoshaItem;
    }

    public void setMiaoshaItem(MiaoshaItem miaoshaItem) {
        this.miaoshaItem = miaoshaItem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ItemDetailVo{" +
                "remainSeconds=" + remainSeconds +
                ", leftSeconds=" + leftSeconds +
                ", miaoshaItem=" + miaoshaItem +
                ", user=" + user +
                '}';
    }
}