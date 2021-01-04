package com.rainbowrecorder.api.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "likes")
@Table(schema = "main")
@IdClass(LikeKey.class)
public class Like {

    public Like() {
    }

    @Id
    private String user_id;

    @Id
    private String post_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

}

class LikeKey implements Serializable {
    private String user_id;
    private String post_id;
}
