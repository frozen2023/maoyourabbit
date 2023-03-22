package com.chen.service.impl;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import com.chen.mapper.UserMapper;
import com.chen.security.LoginUser;
import com.chen.service.UserService;
import com.chen.util.RedisCache;
import com.chen.util.SnowFlakeUtil;
import com.chen.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;
    @Override
    public ReturnType register(User user) {
        user.setUserId(SnowFlakeUtil.getSnowFlakeId());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        int r = userMapper.insert(user);
        if(r>0){
            log.info("id为{}的用户注册成功",user.getUserId());
            return new ReturnType().code(200).message("注册成功");
        }
        else {
            log.info("id为{}的用户注册失败",user.getUserId());
            return new ReturnType().code(404).message("注册失败");
        }
    }

    @Override
    public ReturnType login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("验证失败");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        String token = TokenUtil.createToken(userId);
        Map<String,Object> map = new HashMap();
        redisCache.setCacheObject("login:"+userId,loginUser);
        log.info("存在redis里的用户信息:" + redisCache.getCacheObject("login:"+userId));
        map.put("token",token);
        log.info("id为{}的用户登录成功!",userId);
        return new ReturnType().code(200).message("登录成功").data(map);
    }
}
