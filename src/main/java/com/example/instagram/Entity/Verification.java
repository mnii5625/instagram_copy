package com.example.instagram.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Verification {

    private String code;
    private String EmailOrPhone;
    private String method;
    private Date time;

}
