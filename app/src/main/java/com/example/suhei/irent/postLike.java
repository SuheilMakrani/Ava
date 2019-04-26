package com.example.suhei.irent;

import java.util.Date;

public class postLike {
    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public postLike(String property_id, String user_id, Date timestamp) {
        this.property_id = property_id;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String property_id, user_id;
    public Date timestamp;


    public postLike() {
    }

}
