package com.rainbowrecorder.api.controllers;

import com.rainbowrecorder.api.models.Like;
import com.rainbowrecorder.api.repositories.LikeRepository;
import com.rainbowrecorder.api.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class LikesController {

    @Autowired
    private LikeRepository likeRepository;

    private final LikeService likeService;

    public LikesController(LikeService likeService) {
        this.likeService = likeService;
    }

    @RequestMapping(value = "/like", method = RequestMethod.PUT)
    public ResponseEntity<Object> likePost(@RequestBody Like like) {
        return likeService.likePost(like);
    }

    @RequestMapping(value = "/unlike", method = RequestMethod.DELETE)
    public ResponseEntity<Object> unlikePost(@RequestBody Like like) {
        return likeService.unlikePost(like);
    }

}
