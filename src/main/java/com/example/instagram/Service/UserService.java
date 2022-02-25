package com.example.instagram.Service;


import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.CookieUtil;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.TokenInfo;
import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Jwt.JwtTokenProvider;
import com.example.instagram.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${images.path}")
    private String imgPath;

    private final UserRepository userRepository;
    private final Response response;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    public User getUserInfo(String insta, Model model){
        User user = getUserInfo(insta);
        model.addAttribute("insta", user.getInsta());
        model.addAttribute("profile_image", imgPath + user.getProfile_image());
        return user;
    }
    public User getUserInfo(String insta){
        Map hash = redisTemplate.opsForHash().entries(insta);
        User user = User.builder()
                .insta((String)hash.get("insta"))
                .phone((String)hash.get("phone"))
                .email((String)hash.get("email"))
                .name((String)hash.get("name"))
                .birthday((String)hash.get("birthday"))
                .website((String)hash.get("website"))
                .bio((String)hash.get("bio"))
                .profile_image((String)hash.get("profile_image"))
                .gender((String)hash.get("gender"))
                .build();
        return user;
    }

    public ResponseEntity<?> login(UserRequest.Login login, HttpServletResponse res){
        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            Date date = new Date();
            log.info("authentication : " + authentication.toString());
            log.info("authentication get name: " + authentication.getName());
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

            UserDetails user = (UserDetails) authentication.getPrincipal();
            Map<String, Object> UserMap = UserMap(user);
            UserMap.put("RT", tokenInfo.getRefreshToken());
            UserMap.put("RT_expire", Long.toString(date.getTime() + tokenInfo.getRefreshTokenExpirationTime()));
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            hash.putAll(user.getInsta(), UserMap);
           /*redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);*/

            /*CookieUtil.create(res, "JWT-ACCESS-TOKEN", "Bearer:" + tokenInfo.getAccessToken(), false, 7 * 24 * 60 * 60, "minstagram.kro.kr");
            CookieUtil.create(res, "JWT-REFRESH-TOKEN", "Bearer:" + tokenInfo.getRefreshToken(), false, 7 * 24 * 60 * 60, "minstagram.kro.kr");*/
            jwtTokenProvider.setJwtCookie(res, tokenInfo);
            return response.success(
                    tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
        } catch (InternalAuthenticationServiceException e) {
            log.info(e.toString());
            return response.fail(
                    "입력한 사용자 이름을 사용하는 계정을 찾을 수 없습니다. 사용자 이름을 확인하고 다시 시도하세요.", HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            log.info(e.toString());
            return response.fail(
                    "잘못된 비밀번호입니다. 다시 확인하세요.", HttpStatus.UNAUTHORIZED);
        }
    }


    public ResponseEntity<?> logout(UserRequest.Logout logout){
        if(!jwtTokenProvider.validateToken(logout.getAccessToken())){
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());
        if (redisTemplate.opsForValue().get("RT:"+authentication.getName()) != null){
            redisTemplate.delete("RT:" + authentication.getName());
        }
        Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
        redisTemplate.opsForValue()
                .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
        return response.success("로그아웃 되었습니다.");
    }

    public ResponseEntity<?> changeProfileImage(String insta, MultipartFile file){
        try {
            String[] splitFileName = file.getOriginalFilename().split("\\.");
            String imageType =  splitFileName[splitFileName.length-1];
            String uuid = UUID.randomUUID().toString().replaceAll("-","").substring(0, 15)+ "." + imageType;
            log.info("profile_image_file_name : " + uuid);
            File newFile = new File(uuid);
            file.transferTo(newFile);
            return userRepository.changeProfileImage(insta, uuid);
        }catch (Exception e){
            return response.fail("이미지 저장 실패", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> edit(String insta, UserRequest.Edit edit){
        return response.fail("미구현 입니다...", HttpStatus.NOT_IMPLEMENTED);
    }

    public Map<String, Object> UserMap(UserDetails user){
        Map<String, Object> map = new HashMap<>();
        map.put("insta", user.getInsta());
        map.put("phone", user.getPhone());
        map.put("email", user.getEmail());
        map.put("name", user.getName());
        map.put("birthday", user.getBirthday());
        map.put("website", user.getWebsite());
        map.put("bio", user.getBio());
        map.put("profile_image", user.getProfile_image());
        map.put("gender", user.getGender());
        return map;
    }
}
