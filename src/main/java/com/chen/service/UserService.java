package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Frozen
 * @since 2023-03-22
 */
public interface UserService  {
     ReturnType register(User user);
     ReturnType login(User user);
}