package com.rhu.Objects;

public class Hour {
    String status;
    String userUid;
    String bookingUid;

    public Hour() {
    }

    public Hour(String status, String userUid, String bookingUid) {
        this.status = status;
        this.userUid = userUid;
        this.bookingUid = bookingUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getBookingUid() {
        return bookingUid;
    }

    public void setBookingUid(String bookingUid) {
        this.bookingUid = bookingUid;
    }
}
