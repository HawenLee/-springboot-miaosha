package com.hawen.miaosha.controller;

import com.hawen.miaosha.access.AccessLimit;
import com.hawen.miaosha.domain.MiaoshaItem;
import com.hawen.miaosha.domain.Order;
import com.hawen.miaosha.domain.User;
import com.hawen.miaosha.result.CodeMsg;
import com.hawen.miaosha.service.MiaoshaService;
import com.hawen.miaosha.result.Result;
import com.hawen.miaosha.vo.OrderDetailVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/9
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    private final MiaoshaService miaoshaService;

    public OrderController(MiaoshaService miaoshaService) {
        this.miaoshaService = miaoshaService;
    }

    @GetMapping("/detail")
    @ResponseBody
    @AccessLimit
    public Result<OrderDetailVo> detail(User user, @RequestParam("orderId") long orderId) {
        Order order = miaoshaService.getOrderByIdAndOwnerId(orderId, user.getId());
        if (Objects.isNull(order)) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long itemId = order.getItemId();
        MiaoshaItem item = miaoshaService.getMiaoshaItemById(itemId);
        OrderDetailVo orderDetailVo = OrderDetailVo.builder()
                .order(order)
                .miaoshaItem(item)
                .user(user)
                .build();
        return Result.success(orderDetailVo);
    }


}
