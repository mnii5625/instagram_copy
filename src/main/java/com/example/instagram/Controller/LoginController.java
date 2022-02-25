package com.example.instagram.Controller;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.CookieUtil;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final Response response;

    @GetMapping("/")
    public String LoginPage(HttpServletRequest request, Authentication auth){
        if(auth != null){
            return "forward:/post";
        }
        return "login";
    }
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<?> Login(UserRequest.Login user, HttpServletRequest request, HttpServletResponse response){
        log.info("Login Request Info: "+ user.toString());

        return userService.login(user, response);
    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ResponseEntity<?> Logout(HttpServletResponse res){
        CookieUtil.clear(res, "JWT-ACCESS-TOKEN");

        return response.success("로그아웃", HttpStatus.OK);
    }



}
