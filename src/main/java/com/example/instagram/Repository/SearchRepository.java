package com.example.instagram.Repository;

import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
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

@Repository
@Slf4j
@RequiredArgsConstructor
public class SearchRepository {
    public static final String COLLECTION_RECENTLY = "RecentlySearch";
    private final Response response;
    Firestore db = FirestoreClient.getFirestore();

    public ResponseEntity<?> getRecentlySearch(String id){
        try {
            CollectionReference RecentlyCollection = db.collection(COLLECTION_RECENTLY);
            Query RecentlyQuery = RecentlyCollection.whereEqualTo("insta", id).whereEqualTo("deleted", false);
            ApiFuture<QuerySnapshot> RecentlySnapshot = RecentlyQuery.get();
            List<UserRequest.User> users = new ArrayList<>();
            for (DocumentSnapshot doc : RecentlySnapshot.get().getDocuments()) {
                UserRequest.User user = doc.toObject(UserRequest.User.class);
                users.add(user);
            }
            return response.success(users, "최근 검색 항목", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.toString(), HttpStatus.BAD_REQUEST);
        }

    }
    public UserRequest.User RecentlyUser(DocumentSnapshot doc){

    }

}
