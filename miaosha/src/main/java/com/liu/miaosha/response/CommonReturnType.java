package com.liu.miaosha.response;

/**
 * @author shidacaizi
 * @date 2020/6/4 11:25
 */
public class CommonReturnType {

    //表明对应请求的返回处理结果，"success"或者"fail"
    private String  status;
    //如果status==success 则data返回前端需要的json数据
    //否则status==fail data内使用通用的错误码格式
    private Object data;

    public static CommonReturnType create(Object result){
         return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result, String status){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
