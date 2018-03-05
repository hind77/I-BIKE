package com.hindou.smash.Models;



public class Bike {

    private String id_bike, name, lat, lng, state, station;
    private int distance;

    public Bike(String id_bike, String name, String lat, String lng, String state, int distance, String station) {
        this.id_bike = id_bike;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.state = state;
        this.distance = distance;
        this.station = station;
    }

    public String getId_bike() {
        return id_bike;
    }

    public String getName() {
        return name;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getState() {
        return state;
    }

    public int getDistance() {
        return distance;
    }

    public String getStation() {
        return station;
    }
}
