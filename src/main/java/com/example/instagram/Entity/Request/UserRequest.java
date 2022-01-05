package com.example.instagram.Entity.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Date;
import java.util.List;

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

    @Getter
    @Setter
    public static class Comment{
        private String bundle;
        private String comment;
        private int depth;
        private String insta;
        private List<String> like;
        private Date date;
        private List<Comment> replies;
        private String post;
    }

    @Getter
    @Setter
    public static class Post{
        private String comment;
        private String insta;
        private List<String> like;
        private Date date;
        private List<String> images;
        private List<Comment> comments;
    }
}
