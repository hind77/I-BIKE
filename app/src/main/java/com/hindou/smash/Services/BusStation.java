package com.hindou.smash.Services;

import com.squareup.otto.Bus;

/**
 * Created by HP on 13/05/2018.
 */

public class BusStation {
    private static Bus bus = new Bus();

    public static Bus getBus() {
        return bus;
    }
}
