package com.liu.miaosha.service;

import com.liu.miaosha.service.model.PromoModel;

/**
 * @author shidacaizi
 * @date 2020/6/5 21:55
 */
public interface PromoService {

    PromoModel getPromoByItemId(Integer itemId);
}
