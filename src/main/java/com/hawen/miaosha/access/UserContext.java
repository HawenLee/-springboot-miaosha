package com.hawen.miaosha.access;

import com.hawen.miaosha.domain.User;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/9
 */
public class UserContext {
    private static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    //建议版本，真实需要将其删除
    public static User getUser(){
        return userHolder.get();
    }
}
