package com.rainbowrecorder.api.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity(name = "posts")
public class Post {

    public Post() {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String post_id;

    // likes object
    // may need to set up @Query annotation in likes Class instead to get json aggregate
    @OneToMany(targetEntity = com.rainbowrecorder.api.models.Like.class, cascade = CascadeType.ALL, mappedBy = "user_id")
    private Map<String, String> likes;

    // comments list
    @OneToMany(targetEntity = com.rainbowrecorder.api.models.Comment.class, cascade= CascadeType.ALL, mappedBy = "post_id")
    private List<Comment> comments;

    private String username;

    private Timestamp timestamp;

    private String location;

    private String image;

    private String caption;

    private String user_id;

    public Map<String, String> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, String> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
