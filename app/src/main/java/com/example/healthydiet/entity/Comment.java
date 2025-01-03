package com.example.healthydiet.entity;

public class Comment {
    private int post_id;
    private String comment_content;
    private String timestamp;
    private int is_offending;
    private String username;
    private String profilePic;
    public Comment(String comment_content,String profilePic){
        this.comment_content=comment_content;
        this.profilePic=profilePic;
    }

    public String getComment_content() {
        return comment_content;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
