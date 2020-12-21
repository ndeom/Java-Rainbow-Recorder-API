package com.rainbowrecorder.api.services;

import com.rainbowrecorder.api.constants.ResponseTypes;
import com.rainbowrecorder.api.models.Post;
import com.rainbowrecorder.api.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public ResponseEntity<Object> getPostsInBounds(float w, float s, float e, float n) {
        try {
            CompletableFuture<List<Post>> completedFuture = getPostsAsync(w, s, e, n);
            // .get() should wait until the future returns before proceeding
            var posts = completedFuture.get();
            var responseBody = new HashMap<String, Object>();
            responseBody.put(ResponseTypes.MESSAGE.label, "Posts successfully retrieved");
            responseBody.put("posts", posts);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while fetching posts: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture <List<Post>> getPostsAsync(float w, float s, float e, float n) {
        List<Post> posts = postRepository.getPostsInBounds(w, s, e, n);
        return CompletableFuture.completedFuture(posts);
    }

    public ResponseEntity<Object> getPost(String post_id) {
        try {
            CompletableFuture<Post> completedFuture = getPostAsync(post_id);
            var post = completedFuture.get();
            var responseBody = new HashMap<String, Object>();
            responseBody.put(ResponseTypes.MESSAGE.label, "Post successfully retrieved");
            responseBody.put("post", post);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while fetching post: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture<Post> getPostAsync(String post_id) {
        Post post = postRepository.getPost(post_id);
        return CompletableFuture.completedFuture(post);
    }

    public ResponseEntity<Object> submitPost(Post post) {
        try {
            CompletableFuture<Post> completedFuture = submitPostAsync(post);
            var submittedPost = completedFuture.get();
            var responseBody = new HashMap<String, Object>();
            responseBody.put(ResponseTypes.MESSAGE.label, "Post successfully retrieved");
            responseBody.put("post", submittedPost);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while submitting post: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture<Post> submitPostAsync(Post post) {
        Post submittedPost = postRepository.saveAndFlush(post);
        return CompletableFuture.completedFuture(submittedPost);
    }

}
