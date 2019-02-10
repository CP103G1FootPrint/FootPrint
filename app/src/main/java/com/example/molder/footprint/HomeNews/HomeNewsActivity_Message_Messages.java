package com.example.molder.footprint.HomeNews;

public class HomeNewsActivity_Message_Messages {
    private int  commentId;
    private String userId;
    private String message;
    private int imageId;

    public HomeNewsActivity_Message_Messages(int commentId, String userId, String message, int imageId) {
        this.commentId = commentId;
        this.userId = userId;
        this.message = message;
        this.imageId = imageId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
