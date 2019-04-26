package com.example.suhei.irent;

public class postTenant {

    public postTenant() {
    }

    private String user_id, property_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public postTenant(String user_id, String property_id) {
        this.user_id = user_id;
        this.property_id = property_id;
    }
}
