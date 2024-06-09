package com.hawen.miaosha.controller;

import com.hawen.miaosha.domain.User;
import com.hawen.miaosha.exception.MiaoshaException;
import com.hawen.miaosha.redis.FkRedisUtil;
import com.hawen.miaosha.redis.UserKey;
import com.hawen.miaosha.result.CodeMsg;
import com.hawen.miaosha.result.Result;
import com.hawen.miaosha.service.UserService;
import com.hawen.miaosha.util.MD5Util;
import com.hawen.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final FkRedisUtil fkRedisUtil;

    public UserController(UserService userService, FkRedisUtil fkRedisUtil) {
        this.userService = userService;
        this.fkRedisUtil = fkRedisUtil;
    }

    //该方法使用redis缓存是实现分布式session
    //该方法将session信息保存在redis缓存中，将sessionId 以cookie的形式写入浏览器
    private void addSession(HttpServletResponse response, String token, User user) {
        fkRedisUtil.set(UserKey.token, token, user);
        CookieUtil.addSessionId(response, token);
    }

    @GetMapping("/login")
    public String toLogin() {
        return "login";
    }

    @GetMapping("/verifyCode")
    @ResponseBody
    public void getLoginVerifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = CookieUtil.getSessionId(request, response);
        //创建验证码图片
        BufferedImage image = userService.createVerifyCode(token);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "JPEG", out);
        out.flush();
        out.close();
    }

    @PostMapping("/proLogin")
    @ResponseBody
    public Result<Boolean> proLogin(HttpServletRequest request, HttpServletResponse response, LoginVo loginVo) {
        String token = CookieUtil.getCookieValue(request, UserKey.COOKIE_NAME_TOKEN);
        if (Objects.nonNull(token)) {
            if (!userService.checkVerifyCode(token, loginVo.getVercode())) {
                return Result.error(CodeMsg.REQUEST_ILLEGAL);
            }
            User user = getByToken(response, token);
            if (Objects.nonNull(user) && user.getId().toString().equals(loginVo.getMobile()) &&
                    MD5Util.passToDbPass(loginVo.getPassword(), user.getSalt()).equals(user.getPassword())) {
                return Result.success(true);
            }
        }
        try {
            User user = userService.login(loginVo);
            addSession(response, token, user);
            return Result.success(true);
        } catch (MiaoshaException e) {
            return Result.error(e.getCodeMsg());
        }
    }

    //根据分布式sessionID 读取相对应 的User
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = fkRedisUtil.get(UserKey.token, token, User.class);
        if (user != null) {
            //延长时间
            addSession(response, token, user);
        }
        return user;

    }

}
