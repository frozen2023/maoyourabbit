package com.chen.util;

import java.util.regex.Pattern;

public class AuthUtil {

    // 实名认证具体实现
    public static boolean authIdentity(String realName,String code) {
        return true;
    }

    // 验证邮箱的具体实现，这里只判断了邮箱格式
    public static boolean authEmail(String email){
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

    // 验证手机号的具体实现
    public static boolean authPhone(String phone) {
        return true;
    }
}
