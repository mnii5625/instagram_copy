package com.example.instagram.Jwt;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.CookieUtil;
import com.example.instagram.Entity.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessToken = resolveAccessToken((HttpServletRequest) request);
        String refreshToken = resolveRefreshToken((HttpServletRequest) request);
        UserRequest.Reissue reissue = new UserRequest.Reissue(accessToken, refreshToken);

        if(accessToken != null){
            if(jwtTokenProvider.validateToken(accessToken)){
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else if(jwtTokenProvider.validateRefreshToken(reissue)){
                String newAccessToken = jwtTokenProvider.reissueToken(reissue);
                jwtTokenProvider.setJwtAccessCookie((HttpServletResponse) response, newAccessToken);
                Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) throws UnsupportedEncodingException {
        // APP
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        // WEB
        bearerToken = CookieUtil.getCookie(request, "JWT-ACCESS-TOKEN");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
            return null;
    }
    private String resolveRefreshToken(HttpServletRequest request) throws UnsupportedEncodingException {
        // APP
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        // WEB
        bearerToken = CookieUtil.getCookie(request, "JWT-REFRESH-TOKEN");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
