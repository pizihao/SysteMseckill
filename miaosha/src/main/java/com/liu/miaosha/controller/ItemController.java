package com.liu.miaosha.controller;

import com.liu.miaosha.controller.viewobject.ItemVo;
import com.liu.miaosha.error.BusinessException;
import com.liu.miaosha.response.CommonReturnType;
import com.liu.miaosha.service.CacheService;
import com.liu.miaosha.service.ItemService;
import com.liu.miaosha.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author shidacaizi
 * @date 2020/6/5 10:14
 */
@Controller("item")
@RequestMapping("/item")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class ItemController extends BaseController{
    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheService cacheService;

    //创建商品的controller
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FROMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name="title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVo itemVo = convertVoFromModel(itemModelForReturn);

        return CommonReturnType.create(itemVo);
    }

    //商品详情页浏览
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id") Integer id){
        ItemModel itemModel = null;
        itemModel = (ItemModel) cacheService.getFromCommonCache("item_" + id);
        if (itemModel == null){
            //根据商品的id到redis中获取
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id);
            //若redis内不存在对应的itemModel则访问下游的service
            if (itemModel == null){
                itemModel = itemService.getItemById(id);
                //设置itemModel到redis内
                redisTemplate.opsForValue().set("item_"+id,itemModel);
                redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
            }
            //增加本地缓存
            cacheService.setCommonCache("item_"+id,itemModel);
        }
        ItemVo itemVo = convertVoFromModel(itemModel);
        return CommonReturnType.create(itemVo);
    }

    //商品列表页面浏览展示
    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItemList(){
        List<ItemModel> itemList = itemService.listItem();
        List<ItemVo> collect = itemList.stream().map(this::convertVoFromModel).collect(Collectors.toList());
        return CommonReturnType.create(collect);
    }

    private ItemVo convertVoFromModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(itemModel,itemVo);
        if(itemModel.getPromoModel() != null){
            //有正在进行或即将进行的秒杀活动
            itemVo.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVo.setPromoId(itemModel.getPromoModel().getId());
            itemVo.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVo.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            itemVo.setPromoStatus(0);
        }
        return itemVo;
    }
}
