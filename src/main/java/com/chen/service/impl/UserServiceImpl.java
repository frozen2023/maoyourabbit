package com.chen.service.impl;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import com.chen.mapper.UserMapper;
import com.chen.security.LoginUser;
import com.chen.service.UserService;
import com.chen.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Resource
    private UserGetter userGetter;
    @Resource
    private ImageUtils imageUtils;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public ReturnType register(User user) {
        user.setUserId(SnowFlakeUtil.getSnowFlakeId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        int r = userMapper.insert(user);
        if (r>0){
            log.info("id为{}的用户注册成功",user.getUserId());
            return new ReturnType().success();
        }
        else {
            log.info("id为{}的用户注册失败",user.getUserId());
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("验证失败");
        }
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if(redisCache.getCacheObject("login:"+userId) != null) {
            // 顶号操作
            return new ReturnType().error("账号已被登录");
        }
        String token = TokenUtil.createToken(userId);
        String authority = loginUser.getUser().getAuthority();
        Map<String,Object> map = new HashMap();
        redisCache.setCacheObject("login:"+userId,loginUser);
        map.put("token",token);
        map.put("authority",authority);
        log.info("id为{}的用户登录成功!",userId);
        return new ReturnType().success(map);
    }

    @Override
    public ReturnType logout(Long userId) {
        log.info("id为{}的用户退出登录",userId);
        redisCache.deleteObject("login:" + userId);
        return new ReturnType().success();
    }

    @Override
    public ReturnType head(MultipartFile file) {
        if (Objects.isNull(file)) {
            return new ReturnType().error("文件为空");
        }
        if (!imageUtils.isImageAllowed(file)) {
            return new ReturnType().error("文件格式错误");
        }
        Map<String, String> map = imageUtils.uploadImage(file);
        String url = map.get("imageUrl");
        User user = userGetter.getUser();
        user.setHeadUrl(url);
        if (userMapper.updateById(user) > 0) {
            return new ReturnType().success(map);
        } else {
            return new ReturnType().error();
        }
    }

    /*
    * 左大括号前不换行
      左大括号后换行
     右大括号前换行
    右大括号后还有else等代码则不换行；表示终止的右大括号后必须换行*/
    @Override
    public ReturnType email(String email) {
        if (AuthUtil.authEmail(email)) {
            User user = userGetter.getUser();
            user.setEmail(email);
            if (userMapper.updateById(user) > 0) {
                return new ReturnType().success();
            } else {
                return new ReturnType().error();
            }
        } else {
            return new ReturnType().error("邮箱格式错误");
        }
    }

    @Override
    public ReturnType getUserDetails() {
        User user = userGetter.getUser();
        if (Objects.isNull(user)) {
            return new ReturnType().error();
        }
        return new ReturnType().success(user);
    }

    @Override
    public ReturnType updatePwd(String pwd) {
        User user = userGetter.getUser();
        user.setPassword(passwordEncoder.encode(pwd));
        if (userMapper.updateById(user) > 0) {
            return new ReturnType().success();
        } else {
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType updateName(String username, String nickname) {
        User user = userGetter.getUser();
        if (!Objects.isNull(username) && username != "") {
            user.setUsername(username);
        } else {
            return new ReturnType().error("用户名不能为空");
        }
        if (!Objects.isNull(nickname) && nickname != "") {
            user.setNickname(nickname);
        }
        if (userMapper.updateById(user) > 0) {
            return new ReturnType().success();
        }
        return new ReturnType().error();
    }

    @Override
    public ReturnType auth(String realName, String identityCard) {

        if (AuthUtil.authIdentity(realName,identityCard)) {
            return new ReturnType().error("实名认证失败");
        }
        User user = userGetter.getUser();
        user.setAuthenticated(1);
        user.setRealName(realName);
        user.setIdentityCard(identityCard);
        if (userMapper.updateById(user) > 0) {
            return new ReturnType().success();
        }
        return new ReturnType().error();
    }

    @Override
    public ReturnType bindingPhone(String phone) {
        if (AuthUtil.authPhone(phone)) {
            User user = userGetter.getUser();
            user.setPhoneNumber(phone);
            if (userMapper.updateById(user) > 0) {
                return new ReturnType().success();
            }
        }
        return new ReturnType().error();
    }
}
