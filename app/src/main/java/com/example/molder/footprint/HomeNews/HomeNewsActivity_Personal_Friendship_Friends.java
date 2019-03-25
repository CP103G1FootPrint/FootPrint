package com.example.molder.footprint.HomeNews;

import java.io.Serializable;

public class HomeNewsActivity_Personal_Friendship_Friends implements Serializable {
    private int friendsId;
    private String type;
    private String inviter;
    private String invitee;
    private String message;
    private String messageType;
    private int stste;


    public HomeNewsActivity_Personal_Friendship_Friends(String type, String inviter, String invitee, String message, String messageType) {
        this.type = type;
        this.inviter = inviter;
        this.invitee = invitee;
        this.message = message;
        this.messageType = messageType;
    }

    public HomeNewsActivity_Personal_Friendship_Friends(int friendsId, String inviter, String invitee, String message) {
        this.friendsId = friendsId;
        this.inviter = inviter;
        this.invitee = invitee;
        this.message = message;
    }

    public HomeNewsActivity_Personal_Friendship_Friends(int friendsId) {
        this.friendsId = friendsId;
    }

    public HomeNewsActivity_Personal_Friendship_Friends(String inviter, String invitee) {
        super();
        this.inviter = inviter;
        this.invitee = invitee;
    }


    public HomeNewsActivity_Personal_Friendship_Friends(String inviter, String invitee, String message) {
        super();
        this.inviter = inviter;
        this.invitee = invitee;
        this.message = message;
    }

    public HomeNewsActivity_Personal_Friendship_Friends(String inviter, String invitee, int stste) {
        super();
        this.inviter = inviter;
        this.invitee = invitee;
        this.stste = stste;
    }

    public int getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(int friendsId) {
        this.friendsId = friendsId;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getInvitee() {
        return invitee;
    }

    public void setInvitee(String invitee) {
        this.invitee = invitee;
    }

    public int getStste() {
        return stste;
    }

    public void setStste(int stste) {
        this.stste = stste;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}




