package com.chen.util;


import java.lang.reflect.Field;
import java.util.*;

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

    public static Double toDouble(Object o) {
        if (Objects.isNull(o)) {
            return null;
        }
        return Double.valueOf(o.toString());
    }

    public static List<Long> toLongList(Object o) {
        if (Objects.isNull(o)) {
            return null;
        }
        List<String> list = (List<String>) o;
        List<Long> result = new ArrayList<>();
        for (String item : list) {
            result.add(Long.valueOf(item));
        }
        return result;
    }


}
