package com.hindou.smash.Models;

/**
 * Created by HP on 25/03/2018.
 */

public class Health {
    private String  id,id_user,temp,beats,calories, date;

    public Health(String id, String id_user, String temp, String beats, String calories, String date){
        this.id= id;
        this.id_user = id_user;
        this.temp = temp;
        this.beats = beats;
        this.calories = calories;
        this.date = date;
    }

    public String getId() {return id;}

    public String getCalories() {return calories;}

    public String getBeats() {return beats;}

    public String getId_user() {return id_user;}

    public String getDate() {return date;}

    public String getTemp() {return temp;}
}
