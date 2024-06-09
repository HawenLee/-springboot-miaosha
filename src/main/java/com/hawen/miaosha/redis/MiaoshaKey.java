package com.hawen.miaosha.redis;

public class MiaoshaKey extends AbstractPrefix {
    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaKey miaoshaVerifyCode = new MiaoshaKey(300, "vc");
    // 动态的秒杀路径每60秒就会过期
    public static MiaoshaKey miaoshaPath = new MiaoshaKey(60, "mp");
    // 0代表永不过期
    public static MiaoshaKey isItemOver = new MiaoshaKey(0, "over");
}
