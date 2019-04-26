package com.example.suhei.irent;

public class postTenantImage {

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public postTenantImage(String property_id, String owner_id, String user_id) {
        this.property_id = property_id;
        this.owner_id = owner_id;
        this.user_id = user_id;
    }

    public postTenantImage() {
    }

    public String property_id, owner_id, user_id;

}
