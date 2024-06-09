package com.hawen.miaosha.controller;

import com.hawen.miaosha.access.AccessLimit;
import com.hawen.miaosha.domain.MiaoshaItem;
import com.hawen.miaosha.domain.User;
import com.hawen.miaosha.redis.FkRedisUtil;
import com.hawen.miaosha.redis.ItemKey;
import com.hawen.miaosha.service.MiaoshaService;
import com.hawen.miaosha.result.Result;
import com.hawen.miaosha.vo.ItemDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/9
 */
@Controller
@RequestMapping("/item")
public class ItemController {
    private final MiaoshaService miaoshaService;
    private final FkRedisUtil fkRedisUtil;
    private final ThymeleafViewResolver thymeleafViewResolver;

    public ItemController(MiaoshaService miaoshaService, FkRedisUtil fkRedisUtil, ThymeleafViewResolver thymeleafViewResolver) {
        this.miaoshaService = miaoshaService;
        this.fkRedisUtil = fkRedisUtil;
        this.thymeleafViewResolver = thymeleafViewResolver;
    }

    @GetMapping("/list")
    @ResponseBody
    @AccessLimit
    public String list(HttpServletRequest request, HttpServletResponse response, User user) {
        String html = fkRedisUtil.get(ItemKey.itemList, "", String.class);
        if (StringUtils.isNotEmpty(html)) {
            return html;
        }
        List<MiaoshaItem> itemList = miaoshaService.listMiaoshaItem();
        Map<String, Object> variables = new HashMap<String, Object>() {{
            put("user", user);
            put("itemList", itemList);

        }};
        WebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(),
                variables);
        html = thymeleafViewResolver.getTemplateEngine().process("item_list", ctx);
        if (StringUtils.isNotEmpty(html)) {
            fkRedisUtil.set(ItemKey.itemList, "", html);
        }
        return html;
    }

    @GetMapping(value = "/detail/{itemId}")
    @ResponseBody
    @AccessLimit
    public Result<ItemDetailVo> detail(User user, @PathVariable("itemId") long itemId) {
        MiaoshaItem item = miaoshaService.getMiaoshaItemById(itemId);
        long startAt = item.getStartDate().getTime();
        long endAt = item.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int remainSecond;
        if (now < startAt) {
            remainSecond = (int) ((startAt - now) - 1000);
        } else if (now > endAt) {
            remainSecond = -1;
        } else {
            remainSecond = 0;
        }
        int leftSeconds = (int) ((endAt - now) / 1000);
        ItemDetailVo itemDetailVo = new ItemDetailVo();
        itemDetailVo.setMiaoshaItem(item);
        itemDetailVo.setUser(user);
        itemDetailVo.setRemainSeconds(remainSecond);
        itemDetailVo.setLeftSeconds(leftSeconds);
        return Result.success(itemDetailVo);
    }


}
