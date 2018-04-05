package com.hindou.smash.Models;

/**
 * Created by HP on 05/04/2018.
 */

public class Event {
    private String id,name, description, Date;
    public Event(String id, String name, String description, String Date){
        this.id = id;
        this.name = name;
        this.description = description;
        this.Date = Date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return Date;
    }

    public String getId() {
        return id;
    }
}
