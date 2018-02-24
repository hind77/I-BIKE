package com.hindou.smash.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hindou.smash.LoginActivity;
import com.hindou.smash.Models.User;

/**
 * Created by HP on 19/12/2017.
 */

public class SessionsManager {

    public static SessionsManager sessionsManager;

    private final String  ID_KEY = "ID";
    private final String  FNAME_KEY = "FNAME";
    private final String  LNAME_KEY = "LNAME";
    private final String  EMAIL_KEY = "EMAIL";
    private final String ISACTIVE_KEY = "ISACTIVE";
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SessionsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(GlobalVars.SHAREDPREF_NAME, GlobalVars.SHAREDPREF_MODE);
        editor = sharedPreferences.edit();
        this.context = context;
    }

    public static synchronized SessionsManager getInstance(Context context){
        if (sessionsManager == null){
            sessionsManager = new SessionsManager(context.getApplicationContext());
        }
        return sessionsManager;
    }

    public void createSession(User user){
        editor.putInt(ID_KEY, user.getId());
        editor.putString(FNAME_KEY, user.getFname());
        editor.putString(LNAME_KEY, user.getLname());
        editor.putString(EMAIL_KEY, user.getEmail());
        editor.putBoolean(ISACTIVE_KEY, true);
        editor.commit();
    }

    public User getUser(){
        return new User(
                sharedPreferences.getInt(ID_KEY, 0),
                sharedPreferences.getString(FNAME_KEY, ""),
                sharedPreferences.getString(LNAME_KEY, ""),
                sharedPreferences.getString(EMAIL_KEY, "")
        );
    }

    public boolean isActive(){
        return sharedPreferences.getBoolean(ISACTIVE_KEY, false);
    }

    public void destroySession(){
        editor.clear();
        editor.commit();
    }
}
