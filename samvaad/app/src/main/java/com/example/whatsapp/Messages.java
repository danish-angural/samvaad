package com.example.whatsapp;

import android.widget.RelativeLayout;

public class Messages {
    public Messages(String sender, String message, String datetime, RelativeLayout l) {
        this.sender = sender;
        this.message = message;
        this.datetime = datetime;
        this.l=l;
    }
    public Messages(){}

    public String sender, message, datetime;
    public RelativeLayout l;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getL(){return this.l.getGravity();}

    public void setL(int gravity){this.l.setGravity(gravity);}
}
