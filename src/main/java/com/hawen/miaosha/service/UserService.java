package com.hawen.miaosha.service;

import com.hawen.miaosha.dao.UserMapper;
import com.hawen.miaosha.domain.User;
import com.hawen.miaosha.exception.MiaoshaException;
import com.hawen.miaosha.redis.FkRedisUtil;
import com.hawen.miaosha.redis.UserKey;
import com.hawen.miaosha.result.CodeMsg;
import com.hawen.miaosha.util.VercodeUtil;
import com.hawen.miaosha.vo.LoginVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Service
public class UserService {
    private final UserMapper userMapper;
    private final FkRedisUtil fkRedisUtil;

    public UserService(UserMapper userMapper, FkRedisUtil fkRedisUtil) {
        this.userMapper = userMapper;
        this.fkRedisUtil = fkRedisUtil;
    }

    public BufferedImage createVerifyCode(String token) {
        if (Objects.isNull(token)) {
            return null;
        }
        Random rdm = new Random();
        String verifyCode = VercodeUtil.generateVerifyCode(rdm);
        int rnd = VercodeUtil.calc(verifyCode);
        fkRedisUtil.set(UserKey.verifyCode, token, rnd);
        return VercodeUtil.createVerifyImage(verifyCode, rdm);
    }

    public boolean checkVerifyCode(String token, Integer vercode) {
        if (Objects.isNull(token)) {
            return false;
        }
        Integer codeOld = fkRedisUtil.get(UserKey.verifyCode, token, Integer.class);
        if (Objects.isNull(codeOld) || codeOld - vercode != 0) {
            return false;
        }
        fkRedisUtil.delete(UserKey.verifyCode, token);
        return true;
    }

    @Transactional
    public User login(LoginVo loginVo) {
        if (Objects.isNull(loginVo)) {
            throw new MiaoshaException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        User user = getById(Long.parseLong(mobile));
        if (Objects.isNull(user)) {
            throw new MiaoshaException(CodeMsg.PASSWORD_ERROR);
        }
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLastLoginDate(new Date());
        userMapper.update(user);
        return user;

    }

    private User getById(long id) {
        User user = fkRedisUtil.get(UserKey.getById, "" + id, User.class);
        if (Objects.nonNull(user)) {
            return user;
        }
        user = userMapper.findById(id);
        if (Objects.nonNull(user)) {
            fkRedisUtil.set(UserKey.getById, "" + id, user);
        }
        return user;
    }

}
