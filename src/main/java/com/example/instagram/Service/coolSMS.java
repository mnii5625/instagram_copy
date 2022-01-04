package com.example.instagram.Service;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;
import org.springframework.stereotype.Component;


import java.util.HashMap;


@Component
@ToString
public class coolSMS {

    @Value("${coolsms.api.key}")
    private String APIKey;

    @Value("${coolsms.api.secret}")
    private String APISecret;

    @Value("${coolsms.api.phone}")
    private String PHONE;

    public void sendSMS(String phone, String code){

        Message coolsms = new Message(APIKey, APISecret);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phone);
        params.put("from", PHONE);
        params.put("type", "SMS");
        params.put("text", "인증번호는 ["+code+"] 입니다.");
        params.put("app_version", "test app 1.2");

        try{
            coolsms.send(params);
        } catch (CoolsmsException e){
        }
    }

}
