package com.rainbowrecorder.api.services;

import com.rainbowrecorder.api.constants.ResponseTypes;
import com.rainbowrecorder.api.models.Post;
import com.rainbowrecorder.api.repositories.PostRepository;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AmazonClient amazonClient;

    public ResponseEntity<Object> getPostsInBounds(float w, float s, float e, float n) {
        try {
            CompletableFuture<List<Post>> completedFuture = getPostsAsync(w, s, e, n);
            var posts = completedFuture.get();
            return ResponseEntity.ok().body(
                    new HashMap<String, Object>() {{
                        put(ResponseTypes.MESSAGE.label, "Posts successfully retrieved");
                        put("posts", posts);
                    }}
            );
        } catch(InterruptedException | ExecutionException err) {
            System.out.println("Error while fetching posts: ");
            System.out.println(Arrays.toString(err.getStackTrace()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new HashMap<String, String>() {{
                        put(ResponseTypes.ERROR.label, "Error while getting posts");
                    }}
            );
        }
    }

    @Async
    private CompletableFuture<List<Post>> getPostsAsync(float w, float s, float e, float n) {
        List<Post> posts = postRepository.getPostsInBounds(w, s, e, n);
        return CompletableFuture.completedFuture(posts);
    }

    public ResponseEntity<Object> getPost(String post_id) {
        try {
            CompletableFuture<Post> completedFuture = getPostAsync(post_id);
            var post = completedFuture.get();
            return ResponseEntity.ok().body(
                    new HashMap<String, Object>() {{
                        put(ResponseTypes.MESSAGE.label, "Post successfully retrieved");
                        put("posts", post);
                    }}
            );
        } catch(InterruptedException | ExecutionException err) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new HashMap<String, String>() {{
                        put(ResponseTypes.ERROR.label, "Error while getting single post");
                    }}
            );
        }
    }

    @Async
    private CompletableFuture<Post> getPostAsync(String post_id) {
        Post post = postRepository.getPost(post_id);
        return CompletableFuture.completedFuture(post);
    }

    public ResponseEntity<Object> submitPost(Map<String, Object> body) {
        try {
            // Get image url
            var imageUrl = uploadAndRetrieveAmazonImageUrl(
                    (String) body.get("user_id"),
                    (String) body.get("timestamp"),
                    (String) body.get("image"));

            // Create new post and add post attributes
            var post = new Post();
            post.setUsername((String) body.get("username"));

            // Convert ISO 8601 timestamp from String to Timestamp
            String dateStr = (String) body.get("timestamp");
            Timestamp timestamp = convertISO8601StrToTimestamp(dateStr);
            post.setTimestamp(timestamp);

            post.setCaption((String) body.get("caption"));

            // Convert point from String to Point
            Map<String, Double> location = (Map<String, Double>) body.get("location");
            Point point = createPoint(location);
            post.setLocation(point);

            post.setUser_id((String) body.get("user_id"));
            post.setImage(imageUrl);
            CompletableFuture<Post> completedFuture = submitPostAsync(post);
            var submittedPost = completedFuture.get();
            return ResponseEntity.ok().body(new HashMap<String, Object>() {{
                put(ResponseTypes.MESSAGE.label, "Post successfully retrieved");
                put("post", submittedPost);
            }});
        } catch(Exception err) {
            System.out.println("Error while submitting post: " + err);
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while submitting post: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    private String uploadAndRetrieveAmazonImageUrl(String userId, String timestamp, String image) throws Exception {
        var imageUrl = amazonClient.uploadImage(userId, timestamp, image);
        if (imageUrl.isEmpty()) {
            throw new Exception("Image did not upload correctly");
        }
        return imageUrl.get();
    }

    private Timestamp convertISO8601StrToTimestamp(String dateString) {
        Calendar cal = DatatypeConverter.parseDateTime(dateString);
        Date date = cal.getTime();
        return new Timestamp(date.getTime());
    }

    private Point createPoint(Map<String, Double> location) {
        return new Point(
                new CoordinateArraySequence(
                        new Coordinate[] { new Coordinate(location.get("lng"), location.get("lat"))}
                ),
                new GeometryFactory(
                        new PrecisionModel(PrecisionModel.FLOATING),
                        4326)
        );
    }

    @Async
    private CompletableFuture<Post> submitPostAsync(Post post) {
        Post submittedPost = postRepository.saveAndFlush(post);
        return CompletableFuture.completedFuture(submittedPost);
    }

}
