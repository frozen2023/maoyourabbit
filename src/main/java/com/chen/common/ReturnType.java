package com.chen.common;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

// 返回值类型

@Data
public class ReturnType{
    private Integer code;
    private String message;
    private Object data;

    public ReturnType code(int code){
        this.code=code;
        return this;
    }

    public ReturnType message(String msg) {
        this.message=msg;
        return this;
    }

    public ReturnType data(Object o) {
        this.data=o;
        return this;
    }

    public ReturnType success(Object o) {
        code(ResultConstant.CODE_SUCCESS);
        message(ResultConstant.MESSAGE_DEFAULT_SUCCESS);
        data(o);
        return this;
    }

    public ReturnType success() {
        return success(null);
    }

    public ReturnType error(String message) {
        code(ResultConstant.CODE_ERROR);
        message(message);
        return this;
    }

    public ReturnType error() {
        return error(ResultConstant.MESSAGE_DEFAULT_ERROR);
    }

     public String toJSONString()
    {
        return JSONObject.toJSONString(this);
    }
}
