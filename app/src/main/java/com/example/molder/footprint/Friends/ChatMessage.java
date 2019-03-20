package com.example.molder.footprint.Friends;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private int chatId;

    // type: open, close, chat
    private String type;
    private String sender;
    private String receiver;
    private String content;
    // messageType: text, image
    private String messageType;
    private String timeStamp;


    public ChatMessage(){
    }

    public ChatMessage(int chatId, String sender, String receiver, String content, String timeStamp) {
        this.chatId = chatId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public ChatMessage(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public ChatMessage(String type, String sender, String receiver, String content, String messageType) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.messageType = messageType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
