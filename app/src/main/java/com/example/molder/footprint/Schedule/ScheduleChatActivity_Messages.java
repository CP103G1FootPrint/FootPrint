package com.example.molder.footprint.Schedule;

public class ScheduleChatActivity_Messages {
    private int  chatId;
    private String userId;
    private String message;
    private int imageId;

    public ScheduleChatActivity_Messages(int chatId, String userId, String message, int imageId) {
        this.chatId = chatId;
        this.userId = userId;
        this.message = message;
        this.imageId = imageId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
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
