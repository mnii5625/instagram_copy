package com.example.instagram.Repository;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.User;
import com.example.instagram.Entity.UserDetails;
import com.google.api.Http;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.querydsl.QuerydslRepositoryInvokerAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepository {
    public static final String COLLECTION_USER = "Users";
    public static final String COLLECTION_FOLLOW = "Follow";
    private final Response response;
    private final RedisTemplate redisTemplate;
    Firestore db = FirestoreClient.getFirestore();


    public List<User> getUser() throws ExecutionException, InterruptedException{
        List<User> list = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_USER).get();
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
            apiFuture = db.collection(COLLECTION_USER).whereEqualTo("phone", id).get();
        }else if(id.contains("@")){
            apiFuture = db.collection(COLLECTION_USER).whereEqualTo("email", id).get();
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

    public boolean existInstaId(String instaId){
        try {
            ApiFuture<QuerySnapshot> apiFuture = db.collection(COLLECTION_USER).whereEqualTo("insta", instaId).get();
            List<QueryDocumentSnapshot> documentSnapshot = apiFuture.get().getDocuments();
            if(documentSnapshot.size() > 0){
                return true;
            }else{
                return false;
            }
        }
        catch (Exception e){
            log.error(e.toString());
        }
        return true;

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
                apiFuture = db.collection(COLLECTION_USER).whereEqualTo("phone", username).get();
            }
            else if(username.contains("@")){
                log.info("로그인 : 이메일");
                apiFuture = db.collection(COLLECTION_USER).whereEqualTo("email", username).get();
            }
            else{
                log.info("로그인 : 인스타 아이디");
                apiFuture = db.collection(COLLECTION_USER).whereEqualTo("insta", username).get();
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

        ApiFuture<WriteResult> future = db.collection(COLLECTION_USER).document().set(user);
    }
    
    public User getUserByInsta(String insta){
        try{
            ApiFuture<QuerySnapshot> apiFuture = db.collection(COLLECTION_USER).whereEqualTo("insta", insta).get();
            List<QueryDocumentSnapshot> documents = apiFuture.get().getDocuments();
            if(documents.size() > 0) {
                DocumentSnapshot doc = documents.get(0);
                return doc.toObject(User.class);
            }
            else{
                return null;
            }
        }catch (Exception e){
            log.info(e.toString());
        }
        return null;
    }
    
    public ResponseEntity<?> Edit(UserRequest.Edit edit,String insta){
        try {
            WriteBatch batch = db.batch();
            ApiFuture<QuerySnapshot> snapshot = db.collection(COLLECTION_USER).whereEqualTo("insta", insta).get();
            DocumentReference docRef= snapshot.get().getDocuments().get(0).getReference();
            batch.update(docRef, "insta", edit.getInsta());
            batch.update(docRef, "name", edit.getName());
            batch.update(docRef, "website", edit.getWebsite());
            batch.update(docRef, "bio", edit.getBio());
            batch.update(docRef, "email", edit.getEmail());
            batch.update(docRef, "phone", edit.getPhone());
            batch.update(docRef, "gender", edit.getGender());
            batch.commit();
            return response.success("성공", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    public boolean existEmail(String email){
        try {
            ApiFuture<QuerySnapshot> apiFuture= db.collection(COLLECTION_USER).whereEqualTo("email", email).get();
            if(apiFuture.get().getDocuments().size() == 0 ){
                return false;
            }
            else{
                return true;
            }

        }catch (Exception e){
            log.error(e.toString());
        }
        return true;
    }
    public boolean existPhone(String phone){
        try {
            ApiFuture<QuerySnapshot> apiFuture= db.collection(COLLECTION_USER).whereEqualTo("phone", phone).get();
            if(apiFuture.get().getDocuments().size() == 0 ){
                return false;
            }
            else{
                return true;
            }

        }catch (Exception e){
            log.error(e.toString());
        }
        return true;
    }

    public ResponseEntity<?> changeProfileImage(String insta, String fileName){
        try {
            ApiFuture<QuerySnapshot> snapshot = db.collection(COLLECTION_USER).whereEqualTo("insta", insta).get();
            DocumentReference docRef = snapshot.get().getDocuments().get(0).getReference();
            docRef.update("profile_image", fileName);
            redisTemplate.opsForHash().put(insta,"profile_image", fileName);
            return response.success(fileName,"프로필 사진 변경 완료", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<String> getFollow(String insta){
        List<String> follow = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_FOLLOW).whereEqualTo("insta", insta).get();
            for(DocumentSnapshot doc : future.get().getDocuments()){
                follow.add(doc.get("follow", String.class));
            };
        }
        catch (Exception e){
            log.error(e.toString());
        }
        return follow;

    }
    public List<String> getFollower(String insta){
        List<String> follower = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_FOLLOW).whereEqualTo("follow", insta).whereEqualTo("enabled", true).get();
            for(DocumentSnapshot doc : future.get().getDocuments()){
                follower.add(doc.get("insta", String.class));
            };
        }
        catch (Exception e){
            log.error(e.toString());
        }
        return follower;
    }
    public ResponseEntity<?> follow(UserRequest.follow follow){
        try{
            ApiFuture<WriteResult> future = db.collection(COLLECTION_FOLLOW).document().set(follow);
            return response.success("팔로우 하였습니다.", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<?> unfollow(UserRequest.follow follow){
        try {
            CollectionReference Collection = db.collection(COLLECTION_FOLLOW);
            Query query = Collection.whereEqualTo("insta", follow.getInsta()).whereEqualTo("follow", follow.getFollow());
            ApiFuture<QuerySnapshot> SnapShot = query.get();
            DocumentSnapshot doc = SnapShot.get().getDocuments().get(0);
            doc.getReference().update("enabled", false);
            return response.success("언팔로우 완료", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
