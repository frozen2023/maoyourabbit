package com.chen.util;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

// 解决Object转其他类型时冗余的判空操作

public class ObjectUtils {

    public static Long toLong(Object o) {
        if(Objects.isNull(o)) {
            return null;
        }
        return Long.valueOf(o.toString());
    }

    public static String toString(Object o) {
        if(Objects.isNull(o)) {
            return null;
        }
        return o.toString();
    }

    public static Integer toInteger(Object o) {
        if (Objects.isNull(o)) {
            return null;
        }
        return Integer.valueOf(o.toString());
    }

    public static <T> T mapToObj(Map source,Class<T> target) throws Exception {
        Field[] fields = target.getDeclaredFields();
        T o = target.newInstance();
        for(Field field:fields){
            Object val;
            if((val=source.get(field.getName()))!=null){
                field.setAccessible(true);
                field.set(o,val);
            }
        }
        return o;
    }

}
