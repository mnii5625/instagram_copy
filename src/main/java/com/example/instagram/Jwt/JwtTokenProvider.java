package com.example.instagram.Jwt;


import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.CookieUtil;
import com.example.instagram.Entity.TokenInfo;
import com.example.instagram.Entity.UserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;              // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일


    private final Key key;
    private final RedisTemplate redisTemplate;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfo generateToken(Authentication authentication) {
        String accessToken = setAccessToken(authentication);
        long now = (new Date()).getTime();
        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }
    public String setAccessToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        UserDetails user = (UserDetails)authentication.getPrincipal();
        log.info(user.toString());
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(user.getInsta())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return accessToken;
    }

    public Authentication getAuthentication(String accessToken){
        Claims claims = parseClaims(accessToken);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        //log.info(claims.toString());
        UserDetails principal = UserDetails.builder()
                .insta(claims.getSubject())
                .build();
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.");
        }catch (Exception e){
            log.info("Exception");
        }
        return false;
    }
    public String reissueToken(UserRequest.Reissue reissue){
        return setAccessToken(getAuthentication(reissue.getAccessToken()));
    }
    public boolean validateRefreshToken(UserRequest.Reissue reissue){
        if(!validateToken(reissue.getRefreshToken())){
            return false;
        }
        Authentication authentication = getAuthentication(reissue.getAccessToken());
        Map hash =  redisTemplate.opsForHash().entries(authentication.getName());
        String refreshToken = (String) hash.get("RT");
        if(!refreshToken.equals(reissue.getRefreshToken())){
            return false;
        }
        return true;
    }
    public void setJwtCookie(HttpServletResponse response, TokenInfo tokenInfo){
        CookieUtil.create(response, "JWT-ACCESS-TOKEN", "Bearer:" + tokenInfo.getAccessToken(), false, 7 * 24 * 60 * 60, "minstagram.kro.kr");
        CookieUtil.create(response, "JWT-REFRESH-TOKEN", "Bearer:" + tokenInfo.getRefreshToken(), false, 7 * 24 * 60 * 60, "minstagram.kro.kr");

    }
    public void setJwtAccessCookie(HttpServletResponse response, String accessToken){
        CookieUtil.create(response, "JWT-ACCESS-TOKEN", "Bearer:" + accessToken, false, 7 * 24 * 60 * 60, "minstagram.kro.kr");
    }
    public Claims parseClaims(String accessToken){
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
    public Long getExpiration(String accessToken){
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
