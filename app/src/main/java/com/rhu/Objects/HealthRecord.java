package com.rhu.Objects;

public class HealthRecord {
    String id;
    String userUid;
    String typeOfRecord;
    String description;
    long timestamp;

    public HealthRecord() {
    }

    public HealthRecord(String id, String userUid, String typeOfRecord, String description, long timestamp) {
        this.id = id;
        this.userUid = userUid;
        this.typeOfRecord = typeOfRecord;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getTypeOfRecord() {
        return typeOfRecord;
    }

    public void setTypeOfRecord(String typeOfRecord) {
        this.typeOfRecord = typeOfRecord;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
