package com.rhu.Objects;

public class Appointment {
    String uid;
    String userUid;
    String appointmentType;
    String location;
    String dateCode;
    int timeIndex;
    long schedule;
    String status;
    long timestamp;

    public Appointment() {
    }

    public Appointment(String uid, String userUid, String appointmentType, String location, String dateCode, int timeIndex, long schedule, String status, long timestamp) {
        this.uid = uid;
        this.userUid = userUid;
        this.appointmentType = appointmentType;
        this.location = location;
        this.dateCode = dateCode;
        this.timeIndex = timeIndex;
        this.schedule = schedule;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateCode() {
        return dateCode;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public long getSchedule() {
        return schedule;
    }

    public void setSchedule(long schedule) {
        this.schedule = schedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}