package com.example.suhei.irent;

import ai.api.model.ResponseMessage;

public class postAva {
    public postAva() {
    }

    private String msgText;
    private String msgUser;

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public postAva(String msgText, String msgUser) {
        this.msgText = msgText;
        this.msgUser = msgUser;
    }
}
