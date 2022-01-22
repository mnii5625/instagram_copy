package com.example.instagram.Entity;

import com.example.instagram.Entity.Request.UserRequest;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.NamedEntityGraph;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String insta;
    private String phone;
    private String email;
    private String name;
    private String birthday;
    private String website;
    private String bio;
    private String role;
    private String profile_image;
    private String gender;
    private String password;
    private Boolean enabled;

}
