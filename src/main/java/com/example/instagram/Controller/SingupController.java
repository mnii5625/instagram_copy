package com.example.instagram.Controller;

import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Entity.Verification;
import com.example.instagram.Repository.UserRepository;
import com.example.instagram.Repository.VerificationRepository;
import com.example.instagram.Service.coolSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class SingupController{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    coolSMS SMS;


    @GetMapping("/signup")
    public String RegisterPage(){
        return "signup";
    }

    @PostMapping("/signup")
    public String saveUser(User user) throws Exception {
        user.setEnabled(true);
        user.setRole("ROLE_USER");
        user.setBio("");
        user.setWebsite("");
        user.setProfile_image("default.jpg");

        userRepository.saveUser(user);
        return "redirect:/";
    }

    @PostMapping("/signup/username")
    @ResponseBody
    public Map<String, Object> existUsername(@RequestParam("username") String username) throws Exception {
        String phone = "^[0-9]{11}$";
        String email = "^[a-zA-z0-9]([-_.]?[a-zA-z0-9])*@[a-zA-z0-9]([-_.]?[a-zA-z0-9])*.[a-zA-z]{2,3}$";
        Map<String, Object> map = new HashMap<>();

        if(username.matches(phone)){
            if(userRepository.existId(username)){
                map.put("type", "phone");
                map.put("exist", "true");

            }else{
                map.put("type", "phone");
                map.put("exist", "false");
            }
        }
        else if(username.matches(email)){
            if(userRepository.existId(username)){
                map.put("type", "email");
                map.put("exist", "true");
            }else{
                map.put("type", "email");
                map.put("exist", "false");
            }
        }else{
            map.put("type", "none");
        }

        return map;
    }

    @PostMapping("/signup/insta")
    @ResponseBody
    public Map<String, Object> existinstaId(@RequestParam("insta") String insta) throws Exception{
        Map<String, Object> map = new HashMap<>();
        if(insta.length() >= 5 && insta.length()<20) {
            if (userRepository.existInstaId(insta)) {
                map.put("type", "insta");
                map.put("exist", "true");
            } else {
                map.put("type", "insta");
                map.put("exist", "false");
            }
        }else{
            map.put("type", "none");
        }
        return map;
    }

    @PostMapping("/signup/password")
    @ResponseBody
    public Boolean checkPw(@RequestParam("password") String password) throws Exception {
        String pw = "^[a-zA-Z0-9!@#$%^&]*[0-9]+[a-zA-Z0-9!@#$%^&]*$";
        if(password.length() >= 5 && password.length() <= 30 && password.matches(pw)){
            return true;
        }else{
            return false;
        }
    }

    @PostMapping("/signup/email")
    public void emailSend(@RequestParam("email") String email) throws Exception{
        //?????? ??????
        Random random = new Random();
        String key = "";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        int numIndex = random.nextInt(899999) + 100000;
        key += numIndex;
        //?????? ??????, ??????
        message.setSubject("Instagram ???????????? ?????????.");
        message.setText("???????????? : " + key);
        javaMailSender.send(message);
        //???????????? db ??????
        Verification ver = new Verification(key, email, "email", new Date());
        verificationRepository.setVerification(ver);
    }

    @PostMapping("/signup/phone")
    public String phoneSend(@RequestParam("phone") String phone) throws Exception{
        //?????? ??????
        Random random = new Random();
        String key = "";
        int numIndex = random.nextInt(899999) + 100000;
        key += numIndex;
        //?????? ?????????
        SMS.sendSMS(phone, key);
        Verification ver = new Verification(key, phone, "phone", new Date());
        verificationRepository.setVerification(ver);
        return "login";

    }
    @PostMapping("/signup/verification")
    @ResponseBody
    public Boolean verEmail(@RequestParam("code") String code, @RequestParam("username") String username) throws Exception{
        return verificationRepository.verification(username,code);
    }


}
