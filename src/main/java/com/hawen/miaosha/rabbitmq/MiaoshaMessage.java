package com.hawen.miaosha.rabbitmq;


import com.hawen.miaosha.domain.User;

public class MiaoshaMessage {
    private User user;
    private long itemId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
}
