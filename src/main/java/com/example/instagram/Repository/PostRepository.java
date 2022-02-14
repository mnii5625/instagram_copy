package com.example.instagram.Repository;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.example.instagram.Entity.Request.UserRequest;
import com.example.instagram.Entity.Response.Response;
import com.example.instagram.Entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import sun.security.krb5.internal.PAData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostRepository {
    public static final String COLLECTION_POST = "Posts";
    public static final String COLLECTION_COMMENT = "Comments";
    private final Response response;
    private final UserRepository userRepository;
    Firestore db = FirestoreClient.getFirestore();

    public ResponseEntity<?> setPost(UserRequest.Post post){
        db.collection(COLLECTION_POST).document().set(post);
        return response.success("게시글이 저장되었습니다.");
    }
    public ResponseEntity<?> getPost(String id, Date date, int n) throws ExecutionException, InterruptedException {
        // Post 가져오기
        CollectionReference PostCollection = db.collection(COLLECTION_POST);
        User user = userRepository.getUserByInsta(id);
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

            post.setId(postDoc.getId());
            post.setProfileImage(user.getProfile_image());
            post.setComments(getComment(postDoc.getId()));
            posts.add(post);
        }

        return response.success(posts, "불러오기", HttpStatus.OK);
    }
    public ResponseEntity<?> getPost(String docId){
        try {

            CollectionReference collection = db.collection(COLLECTION_POST);
            UserRequest.Post post = collection.document(docId).get().get().toObject(UserRequest.Post.class);
            User user = userRepository.getUserByInsta(post.getInsta());
            post.setId(docId);
            post.setProfileImage(user.getProfile_image());
            post.setComments(getComment(docId));
            return response.success( post, "불러오기", HttpStatus.OK);

        }
        catch (Exception e){
            log.error(e.toString());
            return response.fail( "불러오기 실패", HttpStatus.BAD_REQUEST);
        }

    }
    public List<UserRequest.Post> getAllPost(String id){
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
    public ResponseEntity<?> uploadPost(UserRequest.uploadData data){
        try{
            UserRequest.Post post = new UserRequest.Post();
            List<String> imagesName = new ArrayList<>();
            for(int i =0; i<data.getImageData().size(); i++){
                //이미지 저장, 이미지 이름 리스트 추가
                imagesName.add(saveImage(data.getImageData().get(i)));
            }
            post.setInsta(data.getInsta());
            post.setComment(data.getComment());
            post.setImages(imagesName);

            db.collection(COLLECTION_POST).document().set(post);
            return response.success("게시물 저장 성공", HttpStatus.OK);
        }catch (Exception e){
            return response.fail("게시물 저장 실패", HttpStatus.BAD_REQUEST);
        }

    }
    public String saveImage(UserRequest.imageData imageData){
        try{

            File file = convert(imageData.getFile());

            int orientation = getOrientation(file);
            BufferedImage rotateImage = checkImage(file, orientation);

            String[] splitFileName = imageData.getFile().getOriginalFilename().split("\\.");
            String imageType =  splitFileName[splitFileName.length-1];
            String imageName = UUID.randomUUID().toString().replaceAll("-","").substring(0, 15)+ "." + imageType;
            BufferedImage original = ImageIO.read(imageData.getFile().getInputStream());


            ImageIO.write(rotateImage, imageType, new File("D:\\\\SpringBootStorage","test1"));
            BufferedImage cropImage = Scalr.crop(rotateImage, Math.round(imageData.getSx()), Math.round(imageData.getSy()), Math.round(imageData.getSw()), Math.round(imageData.getSh()));

            //cropImage.getWidth()
            File newFile = new File("D:\\\\SpringBootStorage",imageName);

            ImageIO.write(cropImage, imageType, newFile);
            return imageName;
        }
        catch (Exception e){
            log.warn(e.toString());
        }

        return null;
    }
    public ResponseEntity<?> saveComment(UserRequest.Comment Comment){
        CollectionReference collectionReference = db.collection(COLLECTION_COMMENT);
        collectionReference.document().set(Comment);
        return response.success("성공", HttpStatus.OK);
    }
    public File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }
    public int getOrientation(File file) throws ImageProcessingException, IOException, MetadataException {
        int orientation = 1;
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
        if(directory != null){
            orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        }
        log.info(String.valueOf(orientation));
        return orientation;
    }
    public BufferedImage checkImage(File file, int orientation) throws IOException {
        BufferedImage bi = ImageIO.read(file);
        if(orientation == 1){
            return bi;
        }else if(orientation == 6){
            return rotateImage(bi, 90);
        }else if(orientation == 3){
            return rotateImage(bi, 180);
        }else if(orientation == 8){
            return rotateImage(bi, 270);
        }else{
            return bi;
        }
    }
    public BufferedImage rotateImage(BufferedImage bImage, int radians){
        BufferedImage newImage;
        if(radians == 90 || radians == 270){
            newImage = new BufferedImage(bImage.getHeight(), bImage.getWidth(), bImage.getType());
        }else if(radians == 180){
            newImage = new BufferedImage(bImage.getWidth(), bImage.getHeight(), bImage.getType());
        } else{
            return bImage;
        }
        Graphics2D graphics = (Graphics2D) newImage.getGraphics();
        graphics.rotate(Math.toRadians(radians), newImage.getWidth() / 2, newImage.getHeight() / 2);
        graphics.translate((newImage.getWidth() - bImage.getWidth()) / 2 , (newImage.getHeight() - bImage.getHeight())/2);
        graphics.drawImage(bImage, 0, 0, bImage.getWidth(), bImage.getHeight(), null);
        return newImage;
    }
}
