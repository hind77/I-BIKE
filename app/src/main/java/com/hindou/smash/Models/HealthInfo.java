package com.hindou.smash.Models;

/**
 * Created by HP on 12/04/2018.
 */
import io.realm.RealmObject;

public class HealthInfo extends RealmObject {

    private int gender;
    private int weight, age;

    public HealthInfo() {
    }

    public HealthInfo(int gender, int weight, int age) {
        this.gender = gender;
        this.weight = weight;
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public int getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }
    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setAge(int age) {
        this.age = age;
    }
}