package com.example.instagram.Repository;

import com.example.instagram.Entity.Verification;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VerificationRepository {

    public static final String COLLECTION_Verification = "Verification";

    Firestore db = FirestoreClient.getFirestore();
    public boolean verification(String id, String code) throws Exception{
        Verification ver = new Verification();
        ApiFuture<DocumentSnapshot> apiFuture = db.collection(COLLECTION_Verification).document(id).get();
        DocumentSnapshot document = apiFuture.get();
        if(document.exists()){
            ver = document.toObject(Verification.class);
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
        ApiFuture<WriteResult> future = db.collection(COLLECTION_Verification).document(ver.getEmailorphone()).set(ver);
    }
}
