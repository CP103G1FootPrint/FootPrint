package com.example.molder.footprint.Schedule;

import java.io.Serializable;

public class GroupAlbum implements Serializable {
    private int albumID ;
    private int tripID ;



    public GroupAlbum(int albumID, int tripID) {
        this.albumID = albumID;
        this.tripID = tripID;

    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }


}
