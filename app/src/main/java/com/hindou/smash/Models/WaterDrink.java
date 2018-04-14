package com.hindou.smash.Models;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by HP on 13/04/2018.
 */

public class WaterDrink extends RealmObject {
    private int drink;
    public WaterDrink(){}
    public WaterDrink(int drink){
        this.drink = drink;
    }

    public int getDrink() {
        return drink;
    }

    public void setDrink(int drink) {
        this.drink = drink;
    }
}
