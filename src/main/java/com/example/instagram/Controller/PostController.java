package com.example.instagram.Controller;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.example.instagram.Repository.PostRepository;
import com.example.instagram.Repository.SearchRepository;
import com.example.instagram.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostController {

    @Value("${images.path}")
    private String imgPath;
    private final PostRepository postRepository;
    private final SearchRepository searchRepository;
    private final Response response;
    private final UserService userService;

    @GetMapping("/post")
    public String LoginPage(HttpServletRequest request, Model model, Authentication auth){
        log.info("PostPage");
        log.info(auth.toString());

        model.addAttribute("home", true);
        //model.addAttribute("chat", true);
        //model.addAttribute("new", true);
        //model.addAttribute("explore", true);
        //model.addAttribute("heart", true);

        //UserDetails users = (UserDetails) auth.getPrincipal();
        User user = userService.getUserInfo(auth.getName(),model);
        return "post";
    }

    @PostMapping("/post")
    @ResponseBody
    public ResponseEntity<?> getPost(Authentication auth,
                                       @RequestParam("Date") Date date,
                                       @RequestParam("insta") String insta,
                                       @RequestParam("n") int n){
        UserDetails user = (UserDetails) auth.getPrincipal();
        try{
            if(insta.equals("")){
                return postRepository.getPost(user.getInsta(), date, n);
            }else{
                return postRepository.getPost(insta, date, n);
            }
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/header/search")
    @ResponseBody
    public ResponseEntity<?> Search(@RequestParam("word") String word){
        return searchRepository.Search(word);
    }
    @PostMapping("/header/recently")
    @ResponseBody
    public ResponseEntity<?> Recently(Authentication auth){
        UserDetails user = (UserDetails) auth.getPrincipal();
        return searchRepository.getRecentlySearch(user.getInsta());
    }
    @PostMapping("/header/recently/delete")
    public ResponseEntity<?> Delete(Authentication auth, @RequestParam("insta") String insta){
        UserDetails user = (UserDetails)  auth.getPrincipal();
        log.info(user.getInsta() + " " + insta);
        return searchRepository.delRecentlySearch(user.getInsta(), insta);
    }
    @PostMapping("/header/recently/deleteAll")
    public ResponseEntity<?> DeleteAll(Authentication auth){
        UserDetails user = (UserDetails) auth.getPrincipal();
        log.info(user.getInsta());
        return searchRepository.delAllRecentlySearch(user.getInsta());
    }
    @PostMapping("/getPost")
    @ResponseBody
    public ResponseEntity<?> getPost(@RequestParam("id") String id,
                                     Authentication auth){
        User user = userService.getUserInfo(auth.getName());
        return postRepository.getPost(id);
    }
    @PostMapping("upload")
    @ResponseBody
    public ResponseEntity<?> files(@RequestParam("fileData") String fileData,
                                   @RequestParam("files") MultipartFile[] files,
                                   @RequestParam("comment") String comment,
                                   @RequestParam("rate") String rate,
                                   Authentication auth) throws IOException {

        User user = userService.getUserInfo(auth.getName());
        JsonArray jsonArray = (JsonArray) JsonParser.parseString(fileData);
        UserRequest.uploadData Data = new UserRequest.uploadData(comment, user.getInsta(), rate);
        log.info("rate");
        log.info(rate);

        for(int i = 0; i< files.length; i++){
            JsonObject object = (JsonObject) jsonArray.get(i);
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequest.imageData imageData = objectMapper.readValue(object.toString(), UserRequest.imageData.class);
            imageData.setFile(files[i]);
            BufferedImage bufferedImage = ImageIO.read(files[i].getInputStream());
            log.info("test");
            log.info(String.valueOf(bufferedImage.getWidth()));
            log.info(String.valueOf(bufferedImage.getHeight()));
            Data.getImageData().add(imageData);
        }

        log.info(Data.toString());
        return postRepository.uploadPost(Data);
    }
    @PostMapping("comment")
    @ResponseBody
    public ResponseEntity<?> comment(@RequestParam("id") String id,
                                     @RequestParam("comment") String comment,
                                     Authentication auth){
        log.info( id);
        log.info(comment);
        User user = userService.getUserInfo(auth.getName());
        UserRequest.Comment Comment = new UserRequest.Comment();
        Comment.setComment(comment);
        Comment.setInsta(user.getInsta());
        Comment.setPost(id);
        return postRepository.saveComment(Comment);
    }
    @PostMapping("reply")
    @ResponseBody
    public ResponseEntity<?> reply(@RequestParam("id") String id,
                                   @RequestParam("comment") String comment,
                                   @RequestParam("bundle") String bundle,
                                   Authentication auth){
        log.info(id);
        log.info(comment);
        log.info(bundle);
        return response.success("성공", HttpStatus.OK);
    }
}
