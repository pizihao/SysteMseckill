package com.liu.miaosha.error;

/**
 * @author shidacaizi
 * @date 2020/6/4 11:50
 */
public interface CommonError {
    public int getErrCode();

    public String getErrMsg();
    
    public CommonError setErrMsg(String errMsg);

}
