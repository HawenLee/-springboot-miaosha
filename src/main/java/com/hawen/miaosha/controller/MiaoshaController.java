package com.hawen.miaosha.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hawen.miaosha.access.AccessLimit;
import com.hawen.miaosha.domain.MiaoshaItem;
import com.hawen.miaosha.domain.MiaoshaOrder;
import com.hawen.miaosha.domain.User;
import com.hawen.miaosha.rabbitmq.MiaoshaMessage;
import com.hawen.miaosha.rabbitmq.MiaoshaSender;
import com.hawen.miaosha.redis.FkRedisUtil;
import com.hawen.miaosha.redis.ItemKey;
import com.hawen.miaosha.result.CodeMsg;
import com.hawen.miaosha.service.MiaoshaService;
import com.hawen.miaosha.result.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/9
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    private final MiaoshaService miaoshaService;
    private final FkRedisUtil fkRedisUtil;
    private final MiaoshaSender mqSender;
    //存放itemId与是否秒杀结束的对应关系
    private final Map<Long, Boolean> localOverMap = Collections.synchronizedMap(new HashMap<>());

    public MiaoshaController(MiaoshaService miaoshaService, FkRedisUtil fkRedisUtil, MiaoshaSender mqSender) {
        this.miaoshaService = miaoshaService;
        this.fkRedisUtil = fkRedisUtil;
        this.mqSender = mqSender;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<MiaoshaItem> itemList = miaoshaService.listMiaoshaItem();
        if (Objects.isNull(itemList)) {
            return;
        }
        for (MiaoshaItem item : itemList) {
            fkRedisUtil.set(ItemKey.miaoshaItemStock, "" + item.getItemId(), item.getStockCount());
            localOverMap.put(item.getId(), false);
        }
    }

    @GetMapping("/verifyCode")
    @ResponseBody
    @AccessLimit
    public void getMiaoshaVerifyVerifyCode(HttpServletResponse response, User user, @RequestParam("itemId") long itemId) throws IOException {
        BufferedImage image = miaoshaService.createVerifyCode(user, itemId);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "JPEG", out);
        out.flush();
        out.close();
    }

    @GetMapping("/path")
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 5)
    public Result<String> getMiaoshaPath(User user, @RequestParam("itemId") long itemId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
        if (!miaoshaService.checkVerifyCode(user, itemId, verifyCode)) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createMiaoshaPath(user, itemId);
        return Result.success(path);
    }

    @PostMapping("/{path}/proMiaosha")
    @ResponseBody
    @AccessLimit
    public Result<Integer> proMiaosha(Model model, User user,
                                      @RequestParam("itemId") long itemId,
                                      @PathVariable("path") String path) throws JsonProcessingException {
        model.addAttribute("user", user);
        boolean check = miaoshaService.checkPath(user, itemId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        Boolean over = localOverMap.get(itemId);
        if (Objects.nonNull(over) && over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        Long stock = fkRedisUtil.decr(ItemKey.miaoshaItemStock, "" + itemId);
        if (stock < 0) {
            localOverMap.put(itemId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        MiaoshaOrder miaoshaOrder = miaoshaService.getMiaoshaOrderByUserIdAndItemId(user.getId(), itemId);
        if (Objects.nonNull(miaoshaOrder)) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setUser(user);
        miaoshaMessage.setItemId(itemId);
        mqSender.sendMiaoshaMessage(miaoshaMessage);
        return Result.success(0);
    }

    @GetMapping("/result")
    @ResponseBody
    @AccessLimit
    public Result<Long> miaoshaResult(Model model, User user, @RequestParam("itemId") long itemId) {
        model.addAttribute("user", user);
        long result = miaoshaService.getMiaoshaResult(user.getId(), itemId);
        return Result.success(result);
    }

}
