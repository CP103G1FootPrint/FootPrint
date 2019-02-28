package com.example.molder.footprint.Schedule;

public class ScheduleChatActivity_Messages {
    private int  chatId;
    private String userId;
    private String message;
    private int tripId;

    public ScheduleChatActivity_Messages(int chatId, String userId, String message, int tripId) {
        this.chatId = chatId;
        this.userId = userId;
        this.message = message;
        this.tripId = tripId;
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

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}
