package com.example.instagram.Entity;

import lombok.*;

import javax.persistence.NamedEntityGraph;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String insta_id;
    private String phone;
    private String email;
    private String password;
    private String name;
    private String role;
    private String birthday;
    private boolean enabled;
    /*
    private String id;
    private String insta_id;
    private String type;
    private String name;
    private String password;
    private String role;
    private Boolean enabled;
    private String birthday;

     */
}
