package com.rainbowrecorder.api.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity(name = "users")
@Table(name = "users", schema = "main")
public class User {

    public User() {
    }

    @Id
    @GeneratedValue(generator = "uuid") // generator denotes name of primary key generator
    @GenericGenerator(name = "uuid", strategy = "uuid2") // generates id using uuid2 strategy
    private String user_id;

    private String username;

    private String hash;

    private String profile_picture;

    private String screen_name;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

}
