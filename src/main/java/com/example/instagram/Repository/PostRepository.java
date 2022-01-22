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
import sun.security.krb5.internal.PAData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostRepository {
    public static final String COLLECTION_POST = "Posts";
    public static final String COLLECTION_COMMENT = "Comments";
    private final Response response;
    Firestore db = FirestoreClient.getFirestore();

    public ResponseEntity<?> setPost(UserRequest.Post post){
        db.collection(COLLECTION_POST).document().set(post);
        return response.success("게시글이 저장되었습니다.");
    }
    public ResponseEntity<?> getPost(String id, Date date, int n) throws ExecutionException, InterruptedException {
        // Post 가져오기
        CollectionReference PostCollection = db.collection(COLLECTION_POST);
        Query postQuery;
        if( n == -1){
            postQuery =  PostCollection.whereEqualTo("insta", id).whereLessThan("date", date).orderBy("date", Query.Direction.DESCENDING);
        }else{
            postQuery =  PostCollection.whereEqualTo("insta", id).whereLessThan("date", date).orderBy("date", Query.Direction.DESCENDING).limit(n);
        }
        ApiFuture<QuerySnapshot> postSnapShot = postQuery.get();
        List<UserRequest.Post> posts = new ArrayList<>();
        if(postSnapShot.get().getDocuments().size() == 0){
            return response.success("더 이상 불러올 게시글이 없습니다.", HttpStatus.NO_CONTENT);
        }
        for (DocumentSnapshot postDoc : postSnapShot.get().getDocuments()){
            UserRequest.Post post = postDoc.toObject(UserRequest.Post.class);
            post.setComments(getComment(postDoc.getId()));
            posts.add(post);
        }

        return response.success(posts, "불러오기", HttpStatus.OK);
    }
    public List<UserRequest.Post> getPost(String id){
        List<UserRequest.Post> posts = new ArrayList<>();
        try{
            CollectionReference Collection = db.collection(COLLECTION_POST);
            Query query = Collection.whereEqualTo("insta", id).orderBy("date");
            ApiFuture<QuerySnapshot> Snapshot = query.get();

            for(DocumentSnapshot doc : Snapshot.get().getDocuments()){
                UserRequest.Post post = doc.toObject(UserRequest.Post.class);
                posts.add(post);
            }
        }catch (Exception e){
            log.info(e.toString());
        }
        return posts;
    }
    public List<UserRequest.Comment> getComment(String postId) throws ExecutionException, InterruptedException {
        // 반환할 List 생성
        List<UserRequest.Comment> comments = new ArrayList<>();
        // Comments 컬렉션 참조
        CollectionReference CommentCollection = db.collection(COLLECTION_COMMENT);
        // 쿼리 생성
        Query commentQuery = CommentCollection.whereEqualTo("post", postId).whereEqualTo("depth", 0).orderBy("date");
        // 데이터 가져오기
        ApiFuture<QuerySnapshot> commentSnapShot = commentQuery.get();
        List<QueryDocumentSnapshot> documents = commentSnapShot.get().getDocuments();

        for(DocumentSnapshot commentDoc : documents) {
            UserRequest.Comment comment = commentDoc.toObject(UserRequest.Comment.class);
            if(!comment.getBundle().equals("")){
                List<UserRequest.Comment> replies = getReply(comment.getBundle());
                comment.setReplies(replies);

            }
            comments.add(comment);
        }
        return comments;
    }
    public List<UserRequest.Comment> getReply(String bundle) throws ExecutionException, InterruptedException {
        List<UserRequest.Comment> replies = new ArrayList<>();
        // Comments 컬렉션 참조
        CollectionReference CommentCollection = db.collection(COLLECTION_COMMENT);
        // 쿼리 생성
        Query replyQuery = CommentCollection.whereEqualTo("bundle", bundle).whereEqualTo("depth", 1).orderBy("date");
        // 데이터 가져오기
        ApiFuture<QuerySnapshot> replySnapShot = replyQuery.get();
        List<QueryDocumentSnapshot> documents = replySnapShot.get().getDocuments();
        for(DocumentSnapshot replyDoc : documents) {
            UserRequest.Comment comment = replyDoc.toObject(UserRequest.Comment.class);
            replies.add(comment);
        }
        return replies;
    }
}
