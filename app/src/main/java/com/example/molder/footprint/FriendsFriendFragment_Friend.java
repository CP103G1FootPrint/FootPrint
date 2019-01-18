package com.example.molder.footprint;

import java.io.Serializable;

public class FriendsFriendFragment_Friend implements Serializable {
    private int friends_CvProfilePicId;
    private String friends_TvFriendsName;

    public FriendsFriendFragment_Friend(int friends_CvProfilePicId, String friends_TvFriendsName) {
        this.friends_CvProfilePicId = friends_CvProfilePicId;
        this.friends_TvFriendsName = friends_TvFriendsName;
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
}
