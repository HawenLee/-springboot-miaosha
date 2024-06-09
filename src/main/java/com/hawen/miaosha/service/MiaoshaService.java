package com.hawen.miaosha.service;

import com.hawen.miaosha.dao.MiaoshaItemMapper;
import com.hawen.miaosha.dao.MiaoshaOrderMapper;
import com.hawen.miaosha.dao.OrderMapper;
import com.hawen.miaosha.domain.MiaoshaItem;
import com.hawen.miaosha.domain.MiaoshaOrder;
import com.hawen.miaosha.domain.Order;
import com.hawen.miaosha.domain.User;
import com.hawen.miaosha.redis.FkRedisUtil;
import com.hawen.miaosha.redis.MiaoshaKey;
import com.hawen.miaosha.redis.OrderKey;
import com.hawen.miaosha.util.MD5Util;
import com.hawen.miaosha.util.UUIDUtil;
import com.hawen.miaosha.util.VercodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.sql.Struct;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/9
 */
@Service
public class MiaoshaService {
    private final FkRedisUtil fkRedisUtil;
    private final MiaoshaItemMapper miaoshaItemMapper;
    private final OrderMapper orderMapper;
    private final MiaoshaOrderMapper miaoshaOrderMapper;

    public MiaoshaService(FkRedisUtil fkRedisUtil, MiaoshaItemMapper miaoshaItemMapper,
                          OrderMapper orderMapper, MiaoshaOrderMapper miaoshaOrderMapper) {
        this.fkRedisUtil = fkRedisUtil;
        this.miaoshaItemMapper = miaoshaItemMapper;
        this.orderMapper = orderMapper;
        this.miaoshaOrderMapper = miaoshaOrderMapper;
    }

    public List<MiaoshaItem> listMiaoshaItem() {
        return miaoshaItemMapper.findAll();
    }

    public MiaoshaItem getMiaoshaItemById(long itemId) {
        return miaoshaItemMapper.findById(itemId);
    }

    public BufferedImage createVerifyCode(User user, long itemId) {
        if (Objects.isNull(user) || itemId < 0) {
            return null;
        }
        Random rdm = new Random();

        String verifyCode = VercodeUtil.generateVerifyCode(rdm);
        int rnd = VercodeUtil.calc(verifyCode);
        fkRedisUtil.set(MiaoshaKey.miaoshaVerifyCode, user.getId() + "," + itemId, rnd);
        return VercodeUtil.createVerifyImage(verifyCode, rdm);
    }

    public boolean checkVerifyCode(User user, long itemId, int verifyCode) {
        if (Objects.isNull(user) || itemId < 0) {
            return false;
        }
        Integer codeOld = fkRedisUtil.get(MiaoshaKey.miaoshaVerifyCode, user.getId() + "," + itemId, Integer.class);
        if (Objects.isNull(codeOld) || codeOld - verifyCode != 0) {
            return false;
        }
        fkRedisUtil.delete(MiaoshaKey.miaoshaVerifyCode, user.getId() + "," + itemId);
        return true;
    }

    public String createMiaoshaPath(User user, long itemId) {
        if (Objects.isNull(user) || itemId < 0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid());
        fkRedisUtil.set(MiaoshaKey.miaoshaPath, "" + user.getId() + "_" + itemId, str);
        return str;
    }

    /**
     * 判断用户输入的秒杀地址是否正确
     *
     * @param user
     * @param itemId
     * @param path
     * @return
     */
    public boolean checkPath(User user, long itemId, String path) {
        if (Objects.isNull(user) || Objects.isNull(path)) {
            return false;
        }
        String pathOld = fkRedisUtil.get(MiaoshaKey.miaoshaPath, "" + user.getId() + "_" + itemId, String.class);
        //与redis缓存比较
        return path.equals(pathOld);
    }

    public MiaoshaOrder getMiaoshaOrderByUserIdAndItemId(Long userId, long itemId) {
        return fkRedisUtil.get(OrderKey.miaoshaOrderByUserIdAndItemId, userId + "_" + itemId, MiaoshaOrder.class);
    }

    @Transactional
    public Order miaosha(User user, MiaoshaItem item) {
        boolean success = reduceStock(item);
        if (success) {
            return createOrder(user, item);
        } else {
            fkRedisUtil.set(MiaoshaKey.isItemOver, "" + item.getId(), true);
            return null;
        }

    }

    @Transactional
    public Order createOrder(User user, MiaoshaItem item) {
        Order order = Order.builder()
                .userId(user.getId())
                .createDate(new Date())
                .orderNum(1)
                .itemId(item.getId())
                .itemName(item.getItemName())
                .orderPrice(item.getMiaoshaPrice())
                .orderChannel(1)
                .status(0)
                .build();
        orderMapper.save(order);

        MiaoshaOrder miaoshaOrder = MiaoshaOrder.builder()
                .userId(user.getId())
                .itemId(item.getItemId())
                .orderId(order.getId())
                .build();
        miaoshaOrderMapper.save(miaoshaOrder);

        fkRedisUtil.set(OrderKey.miaoshaOrderByUserIdAndItemId, "" + user.getId() + "_" + item.getId(), miaoshaOrder);
        return order;
    }

    private boolean reduceStock(MiaoshaItem item) {
        int ret = miaoshaItemMapper.reduceStock(item);
        return ret > 0;
    }

    //根据用户ID 和商品ID 返回秒杀订单的ID
    //如果没有秒杀成功，则秒杀结束时返回-1，秒杀未结束返回0
    public long getMiaoshaResult(Long userId, long itemId) {
        MiaoshaOrder order = getMiaoshaOrderByUserIdAndItemId(userId, itemId);
        if (Objects.nonNull(order)) {
            return order.getId();
        } else {
            Boolean isOver = fkRedisUtil.exists(MiaoshaKey.isItemOver, "" + itemId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public Order getOrderByIdAndOwnerId(long orderId, Long userId) {
        return orderMapper.findByIdOwerId(orderId, userId);
    }
}
