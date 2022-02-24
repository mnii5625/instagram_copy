package com.example.instagram.Controller;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Repository.PostRepository;
import com.example.instagram.Repository.UserRepository;
import com.example.instagram.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    @Value("${images.path}")
    private String imgPath;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final Response response;

    @GetMapping("/{insta}")
    public String hi(@PathVariable("insta") String insta,
                     Model model,
                     Authentication auth){
        User user = userService.getUserInfo(auth.getName(), model);
        User U = userRepository.getUserByInsta(insta);
        if(U != null){
            model.addAttribute("user", true);

            int postCount = postRepository.getAllPost(insta).size();
            List<String> follow = userRepository.getFollow(U.getInsta());
            List<String> follower = userRepository.getFollower(U.getInsta());
            if(user.getInsta().equals(U.getInsta())){
                model.addAttribute("my", true);
            }else if(follow.contains(user.getInsta())){
                if(follower.contains(user.getInsta())){
                    model.addAttribute("follow", true);
                }else{
                    model.addAttribute("follower", true);
                }
            }
            else if(follower.contains(user.getInsta())){
                model.addAttribute("follow", true);
            }else{
                model.addAttribute("not_follow", true);
            }

            model.addAttribute("user_insta", U.getInsta());
            model.addAttribute("user_profile_image", imgPath + U.getProfile_image());
            model.addAttribute("bio",U.getBio());
            model.addAttribute("postCount", postCount);
            model.addAttribute("followerCount", follower.size());
            model.addAttribute("followCount", follow.size());

        }
        return "user";
    }

    @PostMapping("/follow")
    @ResponseBody
    public ResponseEntity<?> follow(UserRequest.follow follow, Authentication auth){
        User user = userService.getUserInfo(auth.getName());
        follow.setDate(new Date());
        follow.setInsta(user.getInsta());
        follow.setEnabled(true);
        log.info(follow.toString());
        return userRepository.follow(follow);
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public ResponseEntity<?> unfollow(UserRequest.follow follow, Authentication auth){
        User user = userService.getUserInfo(auth.getName());
        follow.setDate(new Date());
        follow.setInsta(user.getInsta());
        return userRepository.unfollow(follow);
    }

    @PostMapping("/like")
    @ResponseBody
    public ResponseEntity<?> like(@RequestParam("id") String id,
                                  @RequestParam("type") String type,
                                  Authentication auth){
        User user = userService.getUserInfo(auth.getName());
        if(type.equals("post")){
            return postRepository.updatePostLike(id, user.getInsta());
        }
        else if(type.equals("comment")){
            return postRepository.updateCommentLike(id, user.getInsta());
        }
        return response.fail("잘못된 타입입니다", HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/unlike")
    @ResponseBody
    public ResponseEntity<?> unLike(@RequestParam("id") String id,
                                    @RequestParam("type") String type,
                                    Authentication auth){
        User user = userService.getUserInfo(auth.getName());
        if(type.equals("post")){
            return postRepository.updatePostUnLike(id, user.getInsta());
        }
        else if(type.equals("comment")){
            return postRepository.updateCommentUnLike(id, user.getInsta());
        }
        return response.fail("잘못된 타입입니다", HttpStatus.BAD_REQUEST);
    }

}
