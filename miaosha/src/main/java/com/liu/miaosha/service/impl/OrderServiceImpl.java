package com.liu.miaosha.service.impl;

import com.liu.miaosha.error.BusinessException;
import com.liu.miaosha.error.EmBusinessError;
import com.liu.miaosha.mapper.OrderMapper;
import com.liu.miaosha.mapper.SequenceMapper;
import com.liu.miaosha.pojo.Order;
import com.liu.miaosha.pojo.Sequence;
import com.liu.miaosha.service.ItemService;
import com.liu.miaosha.service.OrderService;
import com.liu.miaosha.service.UserService;
import com.liu.miaosha.service.model.ItemModel;
import com.liu.miaosha.service.model.OrderModel;
import com.liu.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author shidacaizi
 * @date 2020/6/5 15:27
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SequenceMapper sequenceMapper;

    @Override
    public void createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId) throws BusinessException {
        //校验下单状态,下单的商品是否存在，用户是否合法，购买的数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
        if (amount <= 0 || amount > 99){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }
        //校验活动信息
        if (promoId != null){
            //（1）校验对应活动是否存在这个适用商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
                //（2）校验活动是否正在进行中
            }else if (itemModel.getPromoModel().getStatus().intValue() != 2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }

        //落单减库存
        boolean result = itemService.decreaseStock(itemId,amount);
        if (!result){
            throw  new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //订单入库，创建订单表
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if (promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        //生成交易流水号订单号
        orderModel.setId(generateOrderNo());
        Order order = convertFromOrderModel(orderModel);
        orderMapper.insertSelective(order);
        //加上商品的销量
        itemService.increaseSales(itemId,amount);
        //返回前端
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo(){
        //订单号有位
        StringBuilder stringBuilder = new StringBuilder();
        //前八位是时间信息 年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);
        //中间六位是自增序列
        //获取当前sequence
        int sequence = 0;
        Sequence sequenceInfo = sequenceMapper.getSequenceByName("order_info");
        sequence = sequenceInfo.getCurrentValue();
        sequenceInfo.setCurrentValue(sequenceInfo.getCurrentValue() + sequenceInfo.getStep());
        sequenceMapper.updateByPrimaryKeySelective(sequenceInfo);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6-sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //最后两位为分库分表位
        stringBuilder.append("00");
        return stringBuilder.toString();
    }

    private Order convertFromOrderModel(OrderModel orderModel){
        if (orderModel == null){
            return null;
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderModel,order);
        order.setItemPrice(orderModel.getItemPrice().doubleValue());
        order.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return order;
    }

}
