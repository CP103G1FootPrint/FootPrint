package com.example.molder.footprint.Schedule;

public class TripPlanFriend {
    private String createID;
    private String invitee;
    private int tripId;

    public TripPlanFriend(String createID, String invitee, int tripId) {
        this.createID = createID;
        this.invitee = invitee;
        this.tripId = tripId;
    }

    public TripPlanFriend(String createID, String invitee) {
        this.createID = createID;
        this.invitee = invitee;
    }

    public String getCreateID() {
        return createID;
    }

    public void setCreateID(String createID) {
        this.createID = createID;
    }

    public String getInvitee() {
        return invitee;
    }

    public void setInvitee(String invitee) {
        this.invitee = invitee;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}
