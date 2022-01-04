package com.example.instagram.Service;


import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.CookieUtil;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.TokenInfo;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Jwt.JwtTokenProvider;
import com.example.instagram.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Response response;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    public ResponseEntity<?> login(UserRequest.Login login) {
        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("authenticatiton : " + authentication.toString());
            log.info("authentication get name: " + authentication.getName());
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
            //headers.set("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken());

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

    public ResponseEntity<?> login(UserRequest.Login login, HttpServletResponse res){
        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            log.info("authenticatiton : " + authentication.toString());
            log.info("authentication get name: " + authentication.getName());
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

            CookieUtil.create(res, "JWT-ACCESS-TOKEN", "Bearer:" + tokenInfo.getAccessToken(), false, -1, "localhost");
            CookieUtil.create(res, "JWT-REFRESH-TOKEN", "Bearer:" + tokenInfo.getRefreshToken(), false, -1, "localhost");
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

    public ResponseEntity<?> reissue(UserRequest.Reissue reissue) {
        if (!jwtTokenProvider.validateToken((reissue.getRefreshToken()))) {
            return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if (!refreshToken.equals(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

    public UserDetails userProfileImageUrl(String id){
        UserDetails userDetails = null;
        try{
            userDetails = userRepository.findUser(id);
        }catch (Exception e){

        }
        return userDetails;
    }


}
