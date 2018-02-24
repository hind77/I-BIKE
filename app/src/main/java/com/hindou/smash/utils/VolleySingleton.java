package com.hindou.smash.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by HP on 19/12/2017.
 */

public class VolleySingleton {

    public static VolleySingleton volleySingleton;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleySingleton(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if (volleySingleton == null)
            volleySingleton = new VolleySingleton(context);

        return volleySingleton;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
