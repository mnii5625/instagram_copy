package com.example.instagram.Controller;

import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class MainController {

    @Value("${images.path}")
    private String imgPath;

    @GetMapping("/main")
    public String LoginPage(HttpServletRequest request, Model model, Authentication auth){
        log.info("MAINPAGE");
        log.info(auth.toString());

        UserDetails user = (UserDetails) auth.getPrincipal();
        model.addAttribute("profile_image", imgPath + user.getProfile_image());
        return "main";
    }
}
