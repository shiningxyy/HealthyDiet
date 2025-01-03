package com.example.healthydiet.entity;

public class Comment {
    private int post_id;
    private int comment_id;
    private String comment_content;
    private String timestamp;
    private int is_offending;
    private String username;
    private String profilePic;
    public Comment(String comment_content,String profilePic){
        this.comment_content=comment_content;
        this.profilePic=profilePic;
    }

    public Comment(int post_id,int comment_id,String comment_content,int is_offending){
        this.comment_content=comment_content;
        this.comment_id=comment_id;
        this.post_id=post_id;
        this.is_offending=is_offending;
    }
    public String getComment_content() {
        return comment_content;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getIs_offending() {
        return is_offending;
    }

    public void setIs_offending(int is_offending) {
        this.is_offending = is_offending;
    }

    public int getComment_id() {
        return comment_id;
    }
}
