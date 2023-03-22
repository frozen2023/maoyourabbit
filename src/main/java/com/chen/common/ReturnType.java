package com.chen.common;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class ReturnType{
    private Integer code;
    private String message;
    private Object data;

    public ReturnType code(int code){
        this.code=code;
        return this;
    }
    public ReturnType message(String msg)
    {
        this.message=msg;
        return this;
    }
    public ReturnType data(Object o)
    {
        this.data=o;
        return this;
    }

     public String toJSONString()
    {
        return JSONObject.toJSONString(this);
    }
}
