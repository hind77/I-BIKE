package com.hindou.smash.Controllers;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hindou.smash.R;
import com.hindou.smash.Services.LocationService;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.LOCATION_SERVICE;

public class BikerFragementActivity extends android.support.v4.app.Fragment {
    boolean status;
    LocationManager locationManager;
    public static TextView distance;
    public static TextView time;
    public static TextView speed;
    Button start,stop,pause;
    public static  long startTime;
    public static long endTime;
    View view;
    public static int p=0;
    LocationService myservice;


    public BikerFragementActivity(){

    }

    protected ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.localBinder binder = (LocationService.localBinder) service;
            myservice = binder.getService();
            status= true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;

        }
    };
    @Override
    public void onDestroy() {
        if (status == true){
            unbindService();
        }
        super.onDestroy();
    }
    void bindService() {
        if (status == true)
            return;
         Intent i = new Intent(getContext(), LocationService.class);
        getActivity().bindService(i, this.sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    private void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getContext(), LocationService.class);
        getActivity().unbindService(sc);
        status = false;
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_tracking,container,false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        time = (TextView) view.findViewById(R.id.duration_data);
        distance=(TextView) view.findViewById(R.id.distance_data);
        speed = (TextView) view.findViewById(R.id.speed_data);
        start = (Button) view.findViewById(R.id.start);
        stop = (Button) view.findViewById(R.id.stop);
        pause = (Button) view.findViewById(R.id.pause);
        // btn listernners
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //The method below checks if Location is enabled on device or not. If not, then an alert dialog box appears with option
                //to enable gps.
                checkGps();
                locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    return;
                }


                if (status == false)
                    //Here, the Location Service
                    // gets bound and the GPS Speedometer gets Active.
                {
                    bindService();
                }


                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                stop.setVisibility(View.VISIBLE);


            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause.getText().toString().equalsIgnoreCase("pause")) {
                    pause.setText("Resume");
                    p = 1;

                } else if (pause.getText().toString().equalsIgnoreCase("Resume")) {
                    checkGps();
                    locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pause.setText("Pause");
                    p = 0;

                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == true)
                    unbindService();
                start.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                pause.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                p = 0;
            }
        });

    }
    void checkGps() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {



        }
    }
    }

