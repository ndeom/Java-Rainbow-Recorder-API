package com.rainbowrecorder.api.services;

import com.rainbowrecorder.api.constants.ResponseTypes;
import com.rainbowrecorder.api.models.Comment;
import com.rainbowrecorder.api.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public ResponseEntity<Object> comment(Comment comment) {
        try {
            CompletableFuture<Comment> completedFuture = commentAsync(comment);
            // .get() should wait until the future returns before proceeding
            var returnedComment = completedFuture.get();
            var responseBody = new HashMap<String, Object>();
            responseBody.put(ResponseTypes.MESSAGE.label, "Comment successfully added");
            responseBody.put("posts", returnedComment);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put("error", String.format("Error while adding comment: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture<Comment> commentAsync(Comment comment) {
        return CompletableFuture.completedFuture(commentRepository.saveAndFlush(comment));
    }

    public ResponseEntity<Object> uncomment(String comment_id) {
        try {
            CompletableFuture<Void> completedFuture = uncommentAsync(comment_id);
            // .get() should wait until the future returns before proceeding
            var returnedComment = completedFuture.get();
            var responseBody = new HashMap<String, Object>();
            responseBody.put(ResponseTypes.MESSAGE.label, "Comment successfully added");
            responseBody.put("posts", returnedComment);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch(InterruptedException | ExecutionException err) {
            var errResponse = new HashMap<String, String>();
            errResponse.put(ResponseTypes.ERROR.label, String.format("Error while adding comment: %s", Arrays.toString(err.getStackTrace())));
            return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Async
    private CompletableFuture<Void> uncommentAsync(String comment_id) {
        commentRepository.deleteById(comment_id);
        return null;
    }
}
