package com.hindou.smash.Controllers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import com.hindou.smash.Services.BusStation;
import com.hindou.smash.Services.HealthServices;
import com.hindou.smash.Services.Message;
import com.squareup.otto.Subscribe;

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
    Button  stop;
    RadioButton male, female;
    RadioGroup sexe;
    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1, calories;
    String cal,temp,beats;
    private BroadcastReceiver receiver;

    HealthServices healthData;
    boolean bound = false;
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
        stop = (Button) view.findViewById(R.id.stop);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().stopService(new Intent(getContext(),HealthServices.class));
               HealthServices.startstop = false;
            }
        });




    }

  /*  private  void showData(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (healthData!=null){
                    temp =healthData.gettemp();
                    beats = healthData.getheart();

                }
                Log.d("temp",""+temp);
                Log.d("beats",""+beats);
                sensorView0.setText(temp);
                sensorView1.setText(beats);
                handler.postDelayed(this,1000);
            }
        });
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health,container,false);

        return view;

    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();
        BusStation.getBus().register(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        BusStation.getBus().unregister(this);



    }
    @Subscribe
    public void receiveMsg(Message msg){
        sensorView0.setText(msg.getTemp());
        Log.d("temp",""+msg.getTemp());
        sensorView1.setText(msg.getHeartb());
        calories.setText(msg.getCal());

    }







}
