package com.liu.miaosha.service.impl;

import com.liu.miaosha.error.BusinessException;
import com.liu.miaosha.error.EmBusinessError;
import com.liu.miaosha.mapper.ItemMapper;
import com.liu.miaosha.mapper.ItemStockMapper;
import com.liu.miaosha.pojo.Item;
import com.liu.miaosha.pojo.ItemStock;
import com.liu.miaosha.service.ItemService;
import com.liu.miaosha.service.PromoService;
import com.liu.miaosha.service.model.ItemModel;
import com.liu.miaosha.service.model.PromoModel;
import com.liu.miaosha.validator.ValidationResult;
import com.liu.miaosha.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shidacaizi
 * @date 2020/6/5 9:45
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemStockMapper itemStockMapper;

    @Autowired
    private PromoService promoService;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        //转化itemmodel->dataobject
        Item item = this.convertItemDOFromItemModel(itemModel);
        //写入数据库
        itemMapper.insertSelective(item);
        itemModel.setId(item.getId());
        ItemStock itemStock = this.convertItemStockDOFromItemModel(itemModel);
        itemStockMapper.insertSelective(itemStock);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }
    private Item convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        Item item = new Item();
        BeanUtils.copyProperties(itemModel,item);
        item.setPrice(itemModel.getPrice().doubleValue());
        return item;
    }
    private ItemStock convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStock itemStock = new ItemStock();
        itemStock.setItemId(itemModel.getId());
        itemStock.setStock(itemModel.getStock());
        return itemStock;
    }

    @Override
    public List<ItemModel> listItem() {
        List<Item> itemList = itemMapper.listItem();
        return itemList.stream().map(item -> {
            ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());
            return this.convertModelFromDataObject(item, itemStock);
        }).collect(Collectors.toList());
    }

    @Override
    public ItemModel getItemById(Integer id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        if(item == null){
            return null;
        }
        //操作获得库存数量
        ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());
        //将dataobject->model
        ItemModel itemModel = convertModelFromDataObject(item,itemStock);
        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if(promoModel != null && promoModel.getStatus().intValue() != 3){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow = itemStockMapper.decreaseStock(itemId,amount);
        return affectedRow > 0;
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
         itemMapper.increaseSales(itemId,amount);
    }

    private ItemModel convertModelFromDataObject(Item item, ItemStock itemStock) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(item,itemModel);
        itemModel.setPrice(BigDecimal.valueOf(item.getPrice()));
        itemModel.setStock(itemStock.getStock());
        return itemModel;
    }
}
