package com.example.instagram.Repository;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
@Slf4j
public class UserRepository {
    public static final String COLLECTION_User = "Users";

    Firestore db = FirestoreClient.getFirestore();


    public List<User> getUser() throws ExecutionException, InterruptedException{
        List<User> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_User).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for(QueryDocumentSnapshot document : documents){
            list.add(document.toObject(User.class));
        }
        return list;
    }

    public boolean existId(String id) throws Exception{
        String num = "[0-9]+";
        ApiFuture<QuerySnapshot> apiFuture;
        if(id.matches(num)){
            apiFuture = db.collection(COLLECTION_User).whereEqualTo("phone", id).get();
        }else if(id.contains("@")){
            apiFuture = db.collection(COLLECTION_User).whereEqualTo("email", id).get();
        }else{
            return true;
        }
        List<QueryDocumentSnapshot> documentSnapshot = apiFuture.get().getDocuments();
        if(documentSnapshot.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean existInstaId(String instaId) throws Exception{
        ApiFuture<QuerySnapshot> apiFuture = db.collection(COLLECTION_User).whereEqualTo("insta_id", instaId).get();
        List<QueryDocumentSnapshot> documentSnapshot = apiFuture.get().getDocuments();
        if(documentSnapshot.size() > 0){
            return true;
        }else{
            return false;
        }
    }
    // 로그인
    public UserDetails findUser(String username){
        UserDetails user = null;
        try{
            String num = "[0-9]+";
            ApiFuture<QuerySnapshot> apiFuture;
            log.info("로그인 : 유저 아이디 : "+ username);
            if(username==null){
                return user;
            }
            else if(username.matches(num)){
                log.info("로그인 : 휴대폰 번호");
                apiFuture = db.collection(COLLECTION_User).whereEqualTo("phone", username).get();
            }
            else if(username.contains("@")){
                log.info("로그인 : 이메일");
                apiFuture = db.collection(COLLECTION_User).whereEqualTo("email", username).get();
            }
            else{
                log.info("로그인 : 인스타 아이디");
                apiFuture = db.collection(COLLECTION_User).whereEqualTo("insta_id", username).get();
            }
            List<QueryDocumentSnapshot> documents = apiFuture.get().getDocuments();

            if(documents.size() > 0){
                log.info("사용자 있음");

                DocumentSnapshot doc = documents.get(0);
                user = doc.toObject(UserDetails.class);
                log.info(user.toString());
            }else{
                log.info("사용자 없음");
            }
        }catch (Exception e){
            log.info(e.toString());
        }


        return user;

    }
    // 회원가입
    public void saveUser(User user) throws Exception{
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        ApiFuture<WriteResult> future = db.collection(COLLECTION_User).document().set(user);
    }
    public UserRequest.User getUserByInsta(String insta){
        try{
            ApiFuture<QuerySnapshot> apiFuture = db.collection(COLLECTION_User).whereEqualTo("insta_id", insta).get();
            List<QueryDocumentSnapshot> documents = apiFuture.get().getDocuments();
            if(documents.size() > 0) {
                DocumentSnapshot doc = documents.get(0);
                return new UserRequest.User(doc.toObject(UserDetails.class));
            }
            else{
                return null;
            }
        }catch (Exception e){
            log.info(e.toString());
        }
        return null;
    }


}
