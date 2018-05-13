package com.hindou.smash.Services;

/**
 * Created by HP on 13/05/2018.
 */

public class Message {
    private String cal, temp, heartb;

    public Message(String cal, String temp, String heartb) {
        this.cal = cal;
        this.temp = temp;
        this.heartb = heartb;
    }

    public String getCal() {
        return cal;
    }

    public String getTemp() {
        return temp;
    }

    public String getHeartb() {
        return heartb;
    }
}
