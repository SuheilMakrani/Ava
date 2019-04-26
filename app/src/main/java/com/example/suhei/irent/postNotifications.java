package com.example.suhei.irent;

public class postNotifications {

   String sendeeID, message;

   public postNotifications() {
   }

   public postNotifications(String sendeeID, String message) {
      this.sendeeID = sendeeID;
      this.message = message;
   }

   public String getSendeeID() {
      return sendeeID;
   }

   public void setSendeeID(String sendeeID) {
      this.sendeeID = sendeeID;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}

