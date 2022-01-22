package com.example.instagram.test;

import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.User;
import com.example.instagram.Repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@Slf4j
@RequiredArgsConstructor
public class Test {

    @Value("${images.path}")
    private String imgPath;
    private final Response response;
    private final RedisTemplate redisTemplate;

    @GetMapping("/test")
    public String test(Authentication auth, Model model, HttpServletRequest request){

        return "test";
    }

    @PostMapping("test/redis")
    @ResponseBody
    public ResponseEntity<?> redis(){
        String temp = (String) redisTemplate.opsForHash().get("ming._.i", "name");
        log.info(temp);
        return response.success("성공", HttpStatus.OK);
    }
    @PostMapping("test/post")
    @ResponseBody
    public ResponseEntity<?> post(MultipartHttpServletRequest mtfRequest){
        log.info(mtfRequest.getFiles("files").get(0).getOriginalFilename());
        log.info(mtfRequest.getFiles("files").get(1).getOriginalFilename());
        return response.success("성공", HttpStatus.OK);
    }
}
