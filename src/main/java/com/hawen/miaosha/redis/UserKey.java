package com.hawen.miaosha.redis;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
public class UserKey extends AbstractPrefix {
    public static final String COOKIE_NAME_TOKEN = "token";
    public static final int TOKEN_EXPIRE = 1800;

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserKey token = new UserKey(TOKEN_EXPIRE, COOKIE_NAME_TOKEN);
    // 0代表永不过期
    public static UserKey getById = new UserKey(0, "id");
    // 用于保存验证码的key
    public static UserKey verifyCode = new UserKey(300, "vc");
}
