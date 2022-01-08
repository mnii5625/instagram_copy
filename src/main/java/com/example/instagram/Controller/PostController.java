package com.example.instagram.Controller;

import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostController {

    @Value("${images.path}")
    private String imgPath;
    private final PostRepository postRepository;
    private final Response response;

    @GetMapping("/post")
    public String LoginPage(HttpServletRequest request, Model model, Authentication auth){
        log.info("PostPage");
        log.info(auth.toString());

        UserDetails user = (UserDetails) auth.getPrincipal();
        model.addAttribute("profile_image", imgPath + user.getProfile_image());
        return "post";
    }

    @PostMapping("/post")
    @ResponseBody
    public ResponseEntity<?> LoginPage(Authentication auth,
                                       @RequestParam("Date") Date date,
                                       @RequestParam("n") int n){
        UserDetails user = (UserDetails) auth.getPrincipal();
        try{
            return postRepository.getPost(user.getInsta_id(), date, n);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/header/search")
    @ResponseBody
    public ResponseEntity<?> Search(@RequestParam("word") String word){
        return response.success("성공!", HttpStatus.OK);
    }
}
