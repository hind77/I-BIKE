package com.hindou.smash.Controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hindou.smash.R;
import com.hindou.smash.Services.LocationService;

import java.util.Locale;
import java.util.Random;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.LOCATION_SERVICE;

public class BikerFragementActivity extends android.support.v4.app.Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
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
    //---------------------------------
    private GoogleMap mMap;
    // play services location
    private static final int My_permission = 6695;
    private static final int  Play_services_request= 141095;
    private LocationRequest locationRequest;
    private Location mlocation;
    private GoogleApiClient googleApiClient;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    DatabaseReference ref;
    GeoFire geoFire;
    Marker mCurrent;
    private TextToSpeech tts;


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

        //---------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ref = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(ref);
        setUpLocation();

    }

    void checkGps() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {



        }
    }
    //-----------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case My_permission:
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),  Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            // request run time permision
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},My_permission);
        }
        else{
            if (checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),  Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        mlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mlocation != null){
            final double lat = mlocation.getLatitude();
            final double lon = mlocation.getLongitude();
            Log.d("ibike",String.format("your location was changed: %f / %f",lat,lon));
            // update firebase
            geoFire.setLocation("ibike", new GeoLocation(lat, lon), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //add marker
                    if (mCurrent != null){
                        mCurrent.remove();
                    }
                    mCurrent = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat,lon))
                            .title("ibike"));
                    // move camera to this position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),12.0f));
                }
            });
        }
        else
            Log.d("ibike","cannot get your location");
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,getActivity(),Play_services_request).show();
            else{
                Toast.makeText(getContext(),"the device is not supported",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //create the zone area
        LatLng zone = new LatLng(33.991359,-6.849264);
        mMap.addCircle(new CircleOptions()
                .center(zone)
                .radius(500)
                .strokeColor(Color.BLUE)
                .fillColor(Color.RED)
                .strokeWidth(5.0f));
        // add geo query
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(zone.latitude,zone.longitude),0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {


            }

            @Override
            public void onKeyExited(String key) {

                saynotif();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private void saynotif() {
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("warning you are out of legal zone");

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
    }

    private void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();

    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),  Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);// here also
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mlocation = location;
        displayLocation();

    }
    }

