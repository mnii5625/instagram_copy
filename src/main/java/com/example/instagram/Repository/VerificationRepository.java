package com.example.instagram.Repository;

import com.example.instagram.Entity.Verification;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class VerificationRepository {

    public static final String COLLECTION_Verification = "Verification";

    Firestore db = FirestoreClient.getFirestore();
    public boolean verification(String id, String code) throws Exception{
        ApiFuture<DocumentSnapshot> apiFuture = db.collection(COLLECTION_Verification).document(id).get();
        DocumentSnapshot document = apiFuture.get();
        if(document.exists()){
            Verification ver = document.toObject(Verification.class);
            if(ver.getCode().equals(code)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public void setVerification(Verification ver) throws Exception{
        ver.setTime(new Date());
        ApiFuture<WriteResult> future = db.collection(COLLECTION_Verification).document(ver.getEmailOrPhone()).set(ver);
    }

    public void test(){
        Map<String, Object> doc = new HashMap<>();
        doc.put("date", new Date());
        ApiFuture<WriteResult> future = db.collection(COLLECTION_Verification).document("DateTest").set(doc);
        log.info(doc.toString());
    }
}
