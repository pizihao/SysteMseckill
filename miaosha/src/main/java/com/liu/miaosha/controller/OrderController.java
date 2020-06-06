package com.liu.miaosha.controller;

import com.liu.miaosha.error.BusinessException;
import com.liu.miaosha.error.EmBusinessError;
import com.liu.miaosha.response.CommonReturnType;
import com.liu.miaosha.service.OrderService;
import com.liu.miaosha.service.model.OrderModel;
import com.liu.miaosha.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shidacaizi
 * @date 2020/6/5 16:36
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", origins = {"*"})
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FROMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId",required = false) Integer promoId) throws BusinessException {
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户没有登录");
        }
        //获取用户的登录信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        orderService.createOrder(userModel.getId(), itemId, amount, promoId);
        return CommonReturnType.create(null);

    }


}
