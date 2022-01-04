package com.example.instagram.test;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Jwt.JwtTokenProvider;
import com.example.instagram.Service.UserService;
import com.google.api.Http;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


@Controller
@Slf4j
@RequiredArgsConstructor
public class Test {

    @Value("${images.path}")
    private String imgPath;
    private final UserService userService;

    /*@PostMapping("/test/login")
    public ResponseEntity<?> testLogin(UserRequest.Login login, HttpServletRequest request) throws ExecutionException, InterruptedException {
        log.info(login.getId() + " " + login.getPassword());
        return userService.login(login, request);
    }*/
    /*@GetMapping("/test")
    public String test(Authentication auth, Model model, HttpServletRequest request){
        UserDetails user = (UserDetails) auth.getPrincipal();
        model.addAttribute("insta_id", user.getInsta_id());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phone", user.getPhone());
        model.addAttribute("profile_image", imgPath + user.getProfile_image());
        //이미지
        log.info("imgPath : " + imgPath);
        model.addAttribute("img", imgPath + user.getProfile_image());
        return "test";
    }*/

    @PostMapping("/test/imgTest")
    @ResponseBody
    public void testImg(@RequestParam("imgTest") MultipartFile file, Model model, HttpServletRequest request) throws IOException {
        log.info(request.getSession().getServletContext().getRealPath("/").concat("resources"));
        log.info(request.getSession().getServletContext().getRealPath("/").concat("resources") + File.separator+"test");
        //UserDetails user = (UserDetails) auth.getPrincipal();
        String uploadPath = request.getSession().getServletContext().getRealPath("/").concat("resources") + File.separator+"test";
        UUID uid = UUID.randomUUID();
        //file.getBytes()
        String filename = uid + "_" + file.getOriginalFilename();
        log.info("filename" + filename);
        File newfile = new File(filename);
        file.transferTo(newfile);
        /*File target = new File(uploadPath, filename);
        FileCopyUtils.copy(file.getBytes(), target);*/

    }

    @PostMapping("api/test")
    @ResponseBody
    public testDTO apiTest(){
        log.info("POST요청");
        testDTO test = new testDTO(5138, "KIM");
        return test;
    }
    @GetMapping("api/test")
    @ResponseBody
    public List<testDTO> apiTest2(){
        List<testDTO> t = new ArrayList<testDTO>();
        log.info("GET요청");
        testDTO test = new testDTO(5138, "KIM");
        log.info(test.toString());
        t.add(test);
        return t;
    }
}
