package com.example.molder.footprint.Friends;

public class FriendList {
    private String sender;
    private String friend;

    public FriendList(String friend) {
        this.friend = friend;
    }

    public FriendList(String sender, String friend) {
        this.sender = sender;
        this.friend = friend;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}
