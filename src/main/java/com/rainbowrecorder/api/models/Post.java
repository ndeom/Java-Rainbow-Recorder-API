package com.rainbowrecorder.api.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.GenericGenerator;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity(name = "posts")
@Table(schema = "main")
public class Post {

    public Post() {
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String post_id;

    @Column(insertable = false)
    @JsonRawValue
    private String likes;

    @OneToMany(targetEntity = Comment.class, mappedBy = "post_id", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Timestamp timestamp;

    @JsonIgnore
    @Column(columnDefinition = "geometry", nullable = false)
    private Point location;

    @Column(insertable = false)
    private String location_point;

    @Column(nullable = false)
    private String image;

    private String caption;

    @Column(nullable = false)
    private String user_id;

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public String getLikes() {
        return likes;
    }

    public List<Comment> getComments() {
        return comments;
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

    public String getLocation_point() {
        return location_point;
    }

    public void setLocation_point(String location_point) {
        this.location_point = location_point;
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
