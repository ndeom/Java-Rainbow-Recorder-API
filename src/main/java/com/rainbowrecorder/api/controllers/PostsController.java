package com.rainbowrecorder.api.controllers;

import com.rainbowrecorder.api.models.Post;
import com.rainbowrecorder.api.repositories.PostRepository;
import com.rainbowrecorder.api.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    // can be removed
    @Autowired
    private PostRepository postRepository;

    private final PostService postService;

    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<Object> getPostsInGeoBounds(
            @RequestParam float w,
            @RequestParam float s,
            @RequestParam float e,
            @RequestParam float n) {
            return postService.getPostsInBounds(w, s, e, n);
    }

    @GetMapping
    @RequestMapping("/singlepost")
    public ResponseEntity<Object> getSinglePost(@RequestParam String post_id) {
        return postService.getPost(post_id);
    }

    @PostMapping
    public ResponseEntity<Object> createPost(@RequestBody Map<String, Object> body) {
        return postService.submitPost(body);
    }

}