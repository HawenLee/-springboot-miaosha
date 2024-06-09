package com.hawen.miaosha.controller;

import com.hawen.miaosha.redis.UserKey;
import com.hawen.miaosha.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
public class CookieUtil {
    //工具方法，将session Id 以Cookie的形式写入浏览器
    public static void addSessionId(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(UserKey.COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            //找到并返回返回目标cookie的值
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String getSessionId(HttpServletRequest request, HttpServletResponse response) {
        //通过cookie获取分布式sessionId
        String token = CookieUtil.getCookieValue(request, UserKey.COOKIE_NAME_TOKEN);
        if (StringUtils.isBlank(token)) {
            token = UUIDUtil.uuid();
            addSessionId(response, token);
        }
        return token;
    }


}
