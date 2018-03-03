package com.hindou.smash.Models;

/**
 * Created by amineelouattar on 3/3/18.
 */

public class Reservation {
    private String id, bike_name, duration, timestamp;

    public Reservation(String id, String bike_name, String duration, String timestamp) {
        this.id = id;
        this.bike_name = bike_name;
        this.duration = duration;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getBike_name() {
        return bike_name;
    }

    public String getDuration() {
        return duration;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
