package com.chen.common;

import com.chen.util.SnowFlakeUtil;

public interface Authority {
    String USER = "ROLE_USER"; // 普通用户
    String BLACKLIST = "ROLE_BLACKLIST"; // 黑名单用户
    String ADMIN = "ROLE_ADMIN"; // 管理员

    String DEFAULT_HEAD = "http://rrz6xq6jx.hn-bkt.clouddn.com/default_head_sculpture.png";
}
