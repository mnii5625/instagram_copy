package com.example.instagram.Handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        Model model;
        String errorMessage;
        if(exception instanceof AuthenticationServiceException){
            errorMessage = "입력한 사용자 이름을 사용하는 계정을 찾을 수 없습니다. 사용자 이름을 확인하고 다시 시도하세요.";
            System.out.println("입력한 사용자 이름을 사용하는 계정을 찾을 수 없습니다. 사용자 이름을 확인하고 다시 시도하세요.");
        }else if(exception instanceof BadCredentialsException){
            errorMessage = "잘못된 비밀번호입니다. 다시 확인하세요.";
            System.out.println("잘못된 비밀번호입니다. 다시 확인하세요.");
        }else {
            errorMessage = "알수 없는 이유로 로그인에 실패하였습니다.";
            System.out.println("알수 없는 이유로 로그인에 실패하였습니다.");
        }
        System.out.println(request.getParameter("id"));
        System.out.println(request.getParameter("password"));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "999");
        map.put("message", errorMessage);
        ObjectMapper om = new ObjectMapper();
        String jsonString = om.writeValueAsString(map);

        OutputStream out = response.getOutputStream();
        out.write(jsonString.getBytes());


    }
}
