package com.example.molder.footprint;

import java.io.Serializable;

public class FriendsMessageFragment_Message implements Serializable {
    private int friends_CvProfilePicId;
    private String friends_TvFriendsName,friends_TvMessage;

    public FriendsMessageFragment_Message(int friends_CvProfilePicId, String friends_TvFriendsName, String friends_TvMessage) {
        this.friends_CvProfilePicId = friends_CvProfilePicId;
        this.friends_TvFriendsName = friends_TvFriendsName;
        this.friends_TvMessage = friends_TvMessage;
    }

    public int getFriends_CvProfilePicId() {
        return friends_CvProfilePicId;
    }

    public void setFriends_CvProfilePicId(int friends_CvProfilePicId) {
        this.friends_CvProfilePicId = friends_CvProfilePicId;
    }

    public String getFriends_TvFriendsName() {
        return friends_TvFriendsName;
    }

    public void setFriends_TvFriendsName(String friends_TvFriendsName) {
        this.friends_TvFriendsName = friends_TvFriendsName;
    }

    public String getFriends_TvMessage() {
        return friends_TvMessage;
    }

    public void setFriends_TvMessage(String friends_TvMessage) {
        this.friends_TvMessage = friends_TvMessage;
    }
}
