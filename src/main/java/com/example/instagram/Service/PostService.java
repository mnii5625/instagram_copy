package com.example.instagram.Service;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    Response response;


    public ResponseEntity<?> uploadPost(UserRequest.uploadData data){

        return response.success("성공", HttpStatus.OK);
    }
}
