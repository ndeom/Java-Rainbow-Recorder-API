package com.rainbowrecorder.api.controllers;

import com.rainbowrecorder.api.models.Comment;
import com.rainbowrecorder.api.repositories.CommentRepository;
import com.rainbowrecorder.api.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/posts")
public class CommentsController {

    @Autowired
    private CommentRepository commentRepository;

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping(value = "/comment", method = RequestMethod.PUT)
    public ResponseEntity<Object> commentOnPost(@RequestBody CommentRequest body) {
        Comment comment = new Comment();
        comment.setPost_id(body.getPostID());
        comment.setUsername(body.getUsername());
        comment.setComment(body.getComment());
        comment.setTimestamp(body.getTimestamp());
        return commentService.comment(comment);
    }

    @RequestMapping(value = "/uncomment", method = RequestMethod.DELETE)
    public ResponseEntity<Object> removeCommentFromPost(@RequestBody String commentID) {
        return commentService.uncomment(commentID);
    }

}

class CommentRequest {
    private String user_id;
    private String postID;
    private String username;
    private String comment;
    private Timestamp timestamp;

    public CommentRequest() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
