package com.hindou.smash.Controllers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hindou.smash.Models.Health;
import com.hindou.smash.Models.HealthInfo;
import com.hindou.smash.R;
import com.hindou.smash.Services.HealthServices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.google.android.gms.cast.CastRemoteDisplayLocalService.startService;
import static com.google.android.gms.cast.CastRemoteDisplayLocalService.stopService;

/**
 * Created by HP on 12/04/2018.
 */

public class HealthFragementActivity extends Fragment {
    View view;
    Button start, stop;
    RadioButton male, female;
    RadioGroup sexe;
    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1, calories;
    String cal,temp,beats;
    BroadcastReceiver receiver;


    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    public HealthFragementActivity(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txtString = (TextView) view.findViewById(R.id.txtString);
        txtStringLength = (TextView) view.findViewById(R.id.testView1);
        sensorView0 = (TextView) view.findViewById(R.id.sensorView0);
        sensorView1 = (TextView) view.findViewById(R.id.sensorView1);
        calories = (TextView) view.findViewById(R.id.calories_data);



        //list = realm.where(HealthInfo.class).findAll();
        /*start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startstop = true;
                myAsync = new MyAsync();
                myAsync.execute(count);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               myAsync.cancel(true);
            }
        });*/



         receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
               cal = intent.getStringExtra("calories");
               temp = intent.getStringExtra("temp");
               beats = intent.getStringExtra("beats");

                //or
                //exercises = ParseJSON.ChallengeParseJSON(intent.getStringExtra(MY_KEY));
                sensorView0.setText(temp);
                sensorView1.setText(beats);
                calories.setText(cal);

            }
        };










    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health,container,false);

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(getActivity().getPackageName()));

    }

    @Override
    public void onPause()
    {
        super.onPause();
        getActivity().unregisterReceiver(receiver);

    }


    //create new class for connect thread




}
