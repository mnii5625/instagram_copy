package com.example.instagram.Entity.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class UserRequest {

    @Getter
    @Setter
    @ToString
    public static class Login{
        private String id;
        private String password;
        public UsernamePasswordAuthenticationToken toAuthentication(){
            return new UsernamePasswordAuthenticationToken(id, password);
        }
    }

    @Getter
    @Setter
    public static class Reissue{
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class Logout{
        private String accessToken;
        private String refreshToken;
    }
}
