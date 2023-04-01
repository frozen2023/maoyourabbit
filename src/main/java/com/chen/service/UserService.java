package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import org.springframework.web.multipart.MultipartFile;

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
     ReturnType logout(Long userId);
     ReturnType head(MultipartFile file);
     ReturnType email(String email);
     ReturnType getUserDetails();
     ReturnType updatePwd(String pwd);
     ReturnType updateName(String username,String nickname);
     ReturnType auth(String realName,String identityCard);
     ReturnType bindingPhone(String phone);
}
