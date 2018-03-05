package com.hindou.smash.Models;



public class Reservation {
    private String id_reserve,id, bike_name, duration, timestamp;

    public Reservation(String id_reserve,String id, String bike_name, String duration, String timestamp) {
        this.id_reserve= id_reserve;
        this.id = id;
        this.bike_name = bike_name;
        this.duration = duration;
        this.timestamp = timestamp;
    }

    public String getId_reserve() {return id_reserve;}
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
