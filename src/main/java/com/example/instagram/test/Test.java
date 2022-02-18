package com.example.instagram.test;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.User;
import com.example.instagram.Repository.*;
import com.example.instagram.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Controller
@Slf4j
@RequiredArgsConstructor
public class Test {

    @Value("${images.path}")
    private String imgPath;
    private final Response response;
    private final RedisTemplate redisTemplate;
    private final UserService userService;
    private final PostRepository postRepository;

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

    /*@PostMapping("test/file")
    @ResponseBody
    public ResponseEntity<?> files(@RequestParam("fileData") String fileData,
                                   @RequestParam("files") MultipartFile[] files,
                                   @RequestParam("comment") String comment,
                                   Authentication auth) throws IOException {

        User user = userService.getUserInfo(auth.getName());
        JsonArray jsonArray = (JsonArray) JsonParser.parseString(fileData);
        UserRequest.uploadData Data = new UserRequest.uploadData(comment, user.getInsta());

        for(int i = 0; i< files.length; i++){
            JsonObject object = (JsonObject) jsonArray.get(i);
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequest.imageData imageData = objectMapper.readValue(object.toString(), UserRequest.imageData.class);
            imageData.setFile(files[i]);
            BufferedImage bufferedImage = ImageIO.read(files[i].getInputStream());
            log.info("test");
            log.info(String.valueOf(bufferedImage.getWidth()));
            log.info(String.valueOf(bufferedImage.getHeight()));
            Data.getImageData().add(imageData);
        }

        log.info(Data.toString());
        return postRepository.uploadPost(Data);
    }*/
    /*@PostMapping("test/comment")
    @ResponseBody
    public ResponseEntity<?> comment(@RequestParam("id") String id,
                                     @RequestParam("comment") String comment,
                                     Authentication auth){
        log.info( id);
        log.info(comment);
        User user = userService.getUserInfo(auth.getName());
        UserRequest.Comment Comment = new UserRequest.Comment();
        Comment.setComment(comment);
        Comment.setInsta(user.getInsta());
        Comment.setPost(id);
        return postRepository.saveComment(Comment);

    }*/
}
