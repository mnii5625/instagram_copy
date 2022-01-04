package com.example.instagram.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;

@Builder
@Getter
@AllArgsConstructor
public class TokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpirationTime;


}
