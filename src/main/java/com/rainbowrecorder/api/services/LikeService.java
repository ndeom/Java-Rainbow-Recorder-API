package com.rainbowrecorder.api.services;

import com.rainbowrecorder.api.constants.ResponseTypes;
import com.rainbowrecorder.api.models.Like;
import com.rainbowrecorder.api.models.Post;
import com.rainbowrecorder.api.repositories.LikeRepository;
import org.apache.coyote.Response;
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
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public ResponseEntity<Object> likePost(Like like) {
        try {
            CompletableFuture<Like> completedFuture = likePostAsync(like);
            // .get() should wait until the future returns before proceeding
            var returnedLike = completedFuture.get();
            var responseBody = new HashMap<String, Object>();
            responseBody.put(ResponseTypes.MESSAGE.label, "Posts successfully retrieved");
            responseBody.put("posts", returnedLike);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while fetching posts: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture<Like> likePostAsync(Like like) {
        Like returnedLike = likeRepository.saveAndFlush(like);
        return CompletableFuture.completedFuture(returnedLike);
    }

    public ResponseEntity<Object> unlikePost(Like like) {
        try {
            CompletableFuture<Void> completedFuture = unlikePostAsync(like);
            // .get() should wait until the future returns before proceeding
            var returnedLike = completedFuture.get();
            var responseBody = new HashMap<String, Object>();
            responseBody.put(ResponseTypes.MESSAGE.label, "Posts successfully retrieved");
            responseBody.put("posts", returnedLike);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while fetching posts: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture<Void> unlikePostAsync(Like like) {
        likeRepository.delete(like);
        return null;
    }

}
