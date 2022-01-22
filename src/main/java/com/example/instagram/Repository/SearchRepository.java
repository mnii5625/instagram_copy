package com.example.instagram.Repository;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.UserDetails;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SearchRepository {
    public static final String COLLECTION_USER = "Users";
    public static final String COLLECTION_RECENTLY = "RecentlySearch";
    private final UserRepository userRepository;
    private final Response response;
    Firestore db = FirestoreClient.getFirestore();

    public ResponseEntity<?> Search(String word){
        try{
            CollectionReference Collection = db.collection(COLLECTION_USER);
            String filteredWord = word.replaceAll("\\.", "").replaceAll("_","");
            log.info("Search Word : "+ filteredWord);

            ApiFuture<QuerySnapshot> Snapshot = Collection.get();

            List<UserRequest.User> users = new ArrayList<>();
            for(DocumentSnapshot doc : Snapshot.get().getDocuments()){
                UserDetails user = doc.toObject(UserDetails.class);
                String filteredInsta = user.getInsta().replaceAll("\\.", "").replaceAll("_","");
                if(user.getInsta().contains(word) || (!filteredWord.equals("") && filteredInsta.contains(filteredWord))){
                    users.add(new UserRequest.User(user));
                }
            }
            if (users.size() == 0){
                return response.success("검색 정보 없음", HttpStatus.NO_CONTENT);
            }
            return response.success(users, "검색 성공", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getRecentlySearch(String id){
        try {
            CollectionReference RecentlyCollection = db.collection(COLLECTION_RECENTLY);
            Query RecentlyQuery = RecentlyCollection.whereEqualTo("insta", id).whereEqualTo("deleted", false).orderBy("date");
            ApiFuture<QuerySnapshot> RecentlySnapshot = RecentlyQuery.get();
            List<UserRequest.User> users = new ArrayList<>();
            for (DocumentSnapshot doc : RecentlySnapshot.get().getDocuments()) {
                String who = doc.getString("who");
                UserRequest.User user = new UserRequest.User(userRepository.findUser(who));
                users.add(user);
            }
            return response.success(users, "최근 검색 항목", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<?> delRecentlySearch(String id, String who){
        try {
            CollectionReference RecentlyCollection = db.collection(COLLECTION_RECENTLY);
            Query Query = RecentlyCollection.whereEqualTo("insta", id).whereEqualTo("deleted", false).whereEqualTo("who", who);
            ApiFuture<QuerySnapshot> Snapshot = Query.get();
            DocumentSnapshot doc = Snapshot.get().getDocuments().get(0);
            doc.getReference().update("deleted", true);

            return response.success("최근 검색 항목 삭제 완료", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<?> delAllRecentlySearch(String id){
        try {
            CollectionReference RecentlyCollection = db.collection(COLLECTION_RECENTLY);
            Query Query = RecentlyCollection.whereEqualTo("insta", id).whereEqualTo("deleted", false);
            ApiFuture<QuerySnapshot> Snapshot = Query.get();
            for(DocumentSnapshot doc : Snapshot.get().getDocuments()){
                doc.getReference().update("deleted", true);
            }
            return response.success("최근 모든 검색 항목 삭제 완료", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

}
