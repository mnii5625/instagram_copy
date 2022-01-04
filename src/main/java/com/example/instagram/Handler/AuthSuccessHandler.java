package com.example.instagram.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        ObjectMapper om = new ObjectMapper();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "200");
        map.put("message", "성공");

        String jsonString = om.writeValueAsString(map);
        OutputStream out = response.getOutputStream();
        out.write(jsonString.getBytes());

    }
}
