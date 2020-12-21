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
    public ResponseEntity<Object> commentOnPost(@RequestBody Comment comment) {
        return commentService.comment(comment);
    }

    @RequestMapping(value = "/uncomment", method = RequestMethod.DELETE)
    public ResponseEntity<Object> removeCommentFromPost(@RequestBody String comment_id) {
        return commentService.uncomment(comment_id);
    }

}
