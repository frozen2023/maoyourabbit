package com.chen.security;

import com.chen.common.ResultConstant;
import com.chen.common.ReturnType;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 自定义security异常处理

@Component
public class JwtAuthError implements AuthenticationEntryPoint,AccessDeniedHandler {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(ResultConstant.CODE_SUCCESS);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = response.getWriter();
        ReturnType rt = new ReturnType().code(401).message("认证失败");
        printWriter.print(rt.toJSONString());
        printWriter.flush();
        printWriter.close();
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(ResultConstant.CODE_SUCCESS);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = response.getWriter();
        ReturnType rt = new ReturnType().code(403).message("权限不足");
        printWriter.print(rt.toJSONString());
        printWriter.flush();
        printWriter.close();
    }
}
