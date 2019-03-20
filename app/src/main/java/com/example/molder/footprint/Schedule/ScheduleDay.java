package com.example.molder.footprint.Schedule;

public class ScheduleDay {
    private String type;
    private int numberOfDay;
    private String messageType;
    private String sender;
    private String receiver;
    private int controlNumber;
    private int tabCount;
    private int tripId;
//    private String landMark;
    private String landMarkList;

    public ScheduleDay(String type, int numberOfDay, String messageType, String sender, String receiver, int controlNumber,int tripId) {
        super();
        this.type = type;
        this.numberOfDay = numberOfDay;
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.controlNumber = controlNumber;
        this.tripId = tripId;
    }

//    public ScheduleDay(String type, int numberOfDay, String messageType, String sender, String receiver, int controlNumber,int tripId,String landMark) {
//        super();
//        this.type = type;
//        this.numberOfDay = numberOfDay;
//        this.messageType = messageType;
//        this.sender = sender;
//        this.receiver = receiver;
//        this.controlNumber = controlNumber;
//        this.tripId = tripId;
//        this.landMark = landMark;
//    }

    public ScheduleDay(String type, int numberOfDay, String messageType, String sender, String receiver, int controlNumber,int tripId,String landMarkList) {
        super();
        this.type = type;
        this.numberOfDay = numberOfDay;
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.controlNumber = controlNumber;
        this.tripId = tripId;
        this.landMarkList = landMarkList;
    }

    public ScheduleDay(String type, String messageType, String sender, String receiver, int controlNumber,int tabCount) {
        super();
        this.type = type;
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.controlNumber = controlNumber;
        this.tabCount = tabCount;
    }

    public int getNumberOfDay() {
        return numberOfDay;
    }

    public void setNumberOfDay(int numberOfDay) {
        this.numberOfDay = numberOfDay;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getControlNumber() {
        return controlNumber;
    }

    public void setControlNumber(int controlNumber) {
        this.controlNumber = controlNumber;
    }

    public int getTabCount() {
        return tabCount;
    }

    public void setTabCount(int tabCount) {
        this.tabCount = tabCount;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

//    public String getLandMark() {
//        return landMark;
//    }
//
//    public void setLandMark(String landMark) {
//        this.landMark = landMark;
//    }

    public String getLandMarkList() {
        return landMarkList;
    }

    public void setLandMarkList(String landMarkList) {
        this.landMarkList = landMarkList;
    }
}
