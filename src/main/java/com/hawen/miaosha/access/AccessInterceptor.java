package com.hawen.miaosha.access;

import com.hawen.miaosha.controller.CookieUtil;
import com.hawen.miaosha.controller.UserController;
import com.hawen.miaosha.redis.FkRedisUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Component
public class AccessInterceptor implements HandlerInterceptor {
    private final UserController userController;
    private final FkRedisUtil fkRedisUtil;

    public AccessInterceptor(UserController userController, FkRedisUtil fkRedisUtil) {
        this.userController = userController;
        this.fkRedisUtil = fkRedisUtil;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CookieUtil.getSessionId(request, response);
        return true;
    }


}
