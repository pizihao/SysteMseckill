package com.liu.miaosha.service.impl;

import com.liu.miaosha.mapper.PromoMapper;
import com.liu.miaosha.pojo.Promo;
import com.liu.miaosha.service.PromoService;
import com.liu.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author shidacaizi
 * @date 2020/6/5 21:57
 */
@Service
public class PromoServerImpl implements PromoService {

    @Autowired
    private PromoMapper promoMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        Promo promo = promoMapper.selectByItemId(itemId);
        //dataobject -> model
        PromoModel promoModel = converFromDataObject(promo);
        if (promoModel == null) {
            return null;
        }
        //判断当前时间是否是秒杀活动即将开始或正在进行
        DateTime now = new DateTime();
        if (promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if (promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else {
            promoModel.setStatus(2);
        }
        return promoModel;
    }
    private PromoModel converFromDataObject(Promo promo){
        if (promo == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promo,promoModel);
        promoModel.setPromoItemPrice(BigDecimal.valueOf(promo.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promo.getStartDate()));
        promoModel.setEndDate(new DateTime(promo.getEndDate()));
        return promoModel;
    }
}
