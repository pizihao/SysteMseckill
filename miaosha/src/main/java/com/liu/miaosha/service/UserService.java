package com.liu.miaosha.service;

import com.liu.miaosha.error.BusinessException;
import com.liu.miaosha.service.model.UserModel;

/**
 * @author shidacaizi
 * @date 2020/6/3 14:17
 */
public interface UserService {
    //通过用户ID获取用户信息
    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;
    /*
    telphone:用户注册手机
    password:用户加密后的密码
     */
    UserModel validateLogin(String telphone,String encrptPassword) throws BusinessException;
}
