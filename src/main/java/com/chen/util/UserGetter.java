package com.chen.util;

import com.chen.pojo.User;
import com.chen.security.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Objects;

// 从SecurityContext中获取用户信息

@Component
public class UserGetter {
    public User getUser() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser.getUser();
    }

    public Long getUserId() {
        User user = getUser();
        if (!Objects.isNull(user))
            return getUser().getUserId();
        return null;
    }

    public User getNewUser() {
        User user = new User();
        user.setUserId(getUserId());
        return user;
    }
}
