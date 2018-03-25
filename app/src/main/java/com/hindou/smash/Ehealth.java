package com.hindou.smash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hindou.smash.Models.User;
import com.hindou.smash.adapter.ReservationAdapter;
import com.hindou.smash.utils.SessionsManager;

import java.util.ArrayList;

/**
 * Created by HP on 23/03/2018.
 */

public class Ehealth extends AppCompatActivity {
    private SessionsManager sessionsManager;
    private User connectedUser;
    private Toolbar toolbar;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ehealth);
        sessionsManager = SessionsManager.getInstance(this);

        if (!sessionsManager.isActive()){
            changeActivity(LoginActivity.class, true);
        }else{
            initializeComponents();

        }
    }
    private void initializeComponents(){


        //Set screen title
       setTitle("E-Health"
       );

        mContext = this;

    Button history;
    history = (Button) findViewById(R.id.showb);

        connectedUser = sessionsManager.getUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectedUser = sessionsManager.getUser();
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity(HealthHistoryActivity.class, true);
            }
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



    }
    private void changeActivity(Class<?> destination, boolean flag) {
        Intent intent = new Intent(Ehealth.this, destination);
        startActivity(intent);
        if(flag) finish();
    }

}
