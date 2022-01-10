package com.example.instagram.Controller;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Repository.PostRepository;
import com.example.instagram.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    @Value("${images.path}")
    private String imgPath;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @GetMapping("/{insta}")
    public String hi(@PathVariable("insta") String insta, Model model, Authentication auth){
        log.info(insta);
        UserRequest.User user = userRepository.getUserByInsta(insta);
        UserDetails u = (UserDetails) auth.getPrincipal();
        model.addAttribute("profile_image", imgPath + u.getProfile_image());
        int postCount = postRepository.getPost(insta).size();
        if(user != null){
            model.addAttribute("bio","bio");
            model.addAttribute("insta", user.getInsta());
            model.addAttribute("postCount", postCount);
            return "user";
        }else{
            return "user";
        }

    }
}
