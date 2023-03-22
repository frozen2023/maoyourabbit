package com.chen.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class TokenUtil {

    private static String SIGNATURE = "cjksgjkkg74455";

    public static String createToken(Long id) {
         String token = JWT.create()
                .withClaim("userId",id)//payload  //自定义用户名
                .withExpiresAt(new Date(new Date().getTime() + 86400000))//指定令牌过期时间
                .sign(Algorithm.HMAC256(SIGNATURE));//签名
        System.out.println(token);
        return token;
    }
    public static Long parseToken(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
        Long userId=jwt.getClaim("userId").asLong();
        return userId;
    }
}
