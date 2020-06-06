package com.liu.miaosha.service;

import com.liu.miaosha.error.BusinessException;

/**
 * @author shidacaizi
 * @date 2020/6/5 15:25
 */
public interface OrderService {
    //使用 1,通过前端url上传过来秒杀活动ID 然后下单接口内校检对应ID是否属于对应商品且活动已开始
    //2,直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
    void createOrder(Integer userId, Integer itemId, Integer amount,Integer promoId) throws BusinessException;
}
