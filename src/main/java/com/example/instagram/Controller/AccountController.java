package com.example.instagram.Controller;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Repository.UserRepository;
import com.example.instagram.Service.UserService;
import com.google.api.Http;
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
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    @Value("${images.path}")
    private String imgPath;
    private final Response response;
    private final UserRepository userRepository;
    private final UserService userService;


    @GetMapping("/edit")
    public String edit(Authentication auth, Model model){

        User user = userService.getUserInfo(auth.getName(), model);

        model.addAttribute("insta", user.getInsta());
        model.addAttribute("name", user.getName());
        model.addAttribute("profile_image", imgPath + user.getProfile_image());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phone", user.getPhone());
        model.addAttribute("gender", user.getGender());
        return "account";

    }
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<?> Edit(Authentication auth, UserRequest.Edit edit){
        UserDetails user = (UserDetails) auth.getPrincipal();
        if(userRepository.existInstaId(edit.getInsta())){
            return response.fail("사용할 수 없는 사용자 이름입니다. 다른 이름을 사용하세요.", HttpStatus.BAD_REQUEST);
        }
        if(!edit.getEmail().contains("@")){
            return response.fail("유효한 이메일 주소를 입력하세요.", HttpStatus.BAD_REQUEST);
        }
        if(edit.getEmail().equals("") && edit.getPhone().equals("")){
            return response.fail("이메일 또는 확인된 전화번호가 필요합니다.", HttpStatus.BAD_REQUEST);
        }
        if(!user.getEmail().equals(edit.getEmail())){
            if(userRepository.existEmail(edit.getEmail())){
                return response.fail("다른 계정에서 "+ edit.getEmail()+" 주소를 사용하고 있습니다.", HttpStatus.BAD_REQUEST);
            }
        }
        if(!user.getPhone().equals(edit.getPhone())){
            if(userRepository.existPhone(edit.getPhone())){
                return response.fail("다른 계정에서 "+ edit.getPhone()+" 번호를 사용하고 있습니다.", HttpStatus.BAD_REQUEST);
            }
        }
        if(!user.getInsta().equals(edit.getInsta())){
            if(userRepository.existInstaId(edit.getInsta())){
                return response.fail("사용할 수 없는 사용자 이름입니다. 다른 이름을 사용하세요.", HttpStatus.BAD_REQUEST);
            }
        }

        log.info(edit.toString());
        log.info(user.toString());
        return userRepository.Edit(edit, user.getInsta());
    }

    @PostMapping("/edit/profileImage")
    @ResponseBody
    public ResponseEntity<?> changeProfileImage(@RequestParam("profile_file_image") MultipartFile file, Authentication auth){
        UserDetails user = (UserDetails) auth.getPrincipal();
        return userService.changeProfileImage(user.getInsta(), file);
    }
}
