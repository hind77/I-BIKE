package com.hindou.smash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hindou.smash.Models.User;
import com.hindou.smash.utils.GlobalVars;
import com.hindou.smash.utils.SessionsManager;
import com.hindou.smash.utils.VolleySingleton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class StationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private View holder;
    private SessionsManager sessionsManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private User connectedUser;
    private Toolbar toolbar;
    private Context mContext;

    private JSONArray mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        sessionsManager = SessionsManager.getInstance(this);


        if (!sessionsManager.isActive()){
            changeActivity(LoginActivity.class, true);
        }else {
            initializeComponents();
            //Set screen title
            setTitle(R.string.stations_activity_title);
        }


    }

    private void initializeComponents(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        holder = findViewById(R.id.mainholder);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mContext = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectedUser = sessionsManager.getUser();

        supportMapFragment.getMapAsync(this);
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        getCurrentLocaiton();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(StationActivity.this, "This permission is required to run the map", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout :
                Log.d("test", "Logout selected");
                sessionsManager.destroySession();
                changeActivity(LoginActivity.class, true);
                return true;
            case android.R.id.home :
                onBackPressed();
                return true;

            default: return true;
        }
    }

    private void changeActivity(Class<?> destination, boolean finishFlag){
        Intent intent = new Intent(StationActivity.this, destination);
        startActivity(intent);
        if (finishFlag) finish();
    }

    private void showSnack(String message){
        Snackbar.make(holder, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(StationActivity.this, StationRoutingActivity.class);
        for(int i = 0; i < mData.length(); i++){
            try {
                JSONObject dump = mData.getJSONObject(i);
                if(dump.getString("name").equals(marker.getTitle())){
                    intent.putExtra("name", marker.getTitle());
                    intent.putExtra("lat", dump.getString("lat"));
                    intent.putExtra("lng", dump.getString("lng"));
                    intent.putExtra("num_bikes", dump.getString("num_bikes"));
                    intent.putExtra("ulat", String.valueOf(mLocation.getLatitude()));
                    intent.putExtra("ulng", String.valueOf(mLocation.getLongitude()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        startActivity(intent);
        return false;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocaiton(){
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLocation = location;
                        if (location == null) {
                            Toast.makeText(StationActivity.this, "No location", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Current Location", mLocation.getLatitude() + " , " + mLocation.getLongitude());

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalVars.API_URL + "getStations.php"
                                    , new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d("VOLLEY_NETWORKS", response);
                                    if (mLocation != null) {
                                        mGoogleMap.setMyLocationEnabled(true);
                                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 17));
                                    }
                                    try {
                                        JSONObject rep = (JSONObject) new JSONTokener(response).nextValue();
                                        if (rep.getString("code").equals("200")) {
                                            JSONArray data = rep.getJSONArray("data");
                                            mData = data;
                                            for (int i = 0; i < data.length(); i++) {
                                                mGoogleMap.addMarker(new MarkerOptions().position(
                                                        new LatLng(Double.valueOf(data.getJSONObject(i).getString("lat")),
                                                                Double.valueOf(data.getJSONObject(i).getString("lng")))
                                                ).title(data.getJSONObject(i).getString("name")));
                                            }
                                        } else {
                                            showSnack(rep.getString("error"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {

                                    Map<String, String> params = new HashMap<>();

                                    params.put("lat", String.valueOf(mLocation.getLatitude()));
                                    params.put("lng", String.valueOf(mLocation.getLongitude()));

                                    return params;

                                }
                            };

                            VolleySingleton.getInstance(StationActivity.this).addToRequestQueue(stringRequest);
                        }
                    }
                });
    }
}
