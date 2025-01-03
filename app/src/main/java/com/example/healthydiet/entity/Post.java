package com.example.healthydiet.entity;

import java.io.Serializable;

public class Post implements Serializable {
    public int post_id;
    public String post_title;
    public int user_id;
    public String post_content;
    public String tags;
    public String timestamp;
    public int is_offending;
    public Post(int post_id,int user_id,String post_title,String post_content,String tags,String timestamp,int is_offending){
        this.user_id=user_id;
        this.post_title=post_title;
        this.post_id=post_id;
        this.post_content=post_content;
        this.tags=tags;
        this.timestamp=timestamp;
        this.is_offending=is_offending;
    }
    public Post(int post_id,String post_title,int is_offending){
        this.post_id=post_id;
        this.post_title=post_title;
        this.is_offending=is_offending;
    }
    public String getPost_content() {
        return post_content;
    }

    public String getPost_title() {
        return post_title;
    }

    public String getTags() {
        return tags;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getPost_id() {
        return post_id;
    }

    public int getIs_offending() {
        return is_offending;
    }

    public void setIs_offending(int is_offending) {
        this.is_offending = is_offending;
    }
}
