package com.hindou.smash.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hindou.smash.Models.User;
import com.hindou.smash.R;


import java.util.ArrayList;
import java.util.List;

public class StationRoutingActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {


    private User connectedUser;
    private Toolbar toolbar;
    private GoogleMap mGoogleMap;
    private TextView mStationName, mCoordination, mBikesNumber;
    private ProgressBar progressBar;
    private LatLng start, end;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_routing);

        Intent intent = getIntent();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mStationName = (TextView) findViewById(R.id.station_name);
        mCoordination = (TextView) findViewById(R.id.station_xy);
        mBikesNumber = (TextView) findViewById(R.id.bikes_number);
        //progressBar = (ProgressBar) findViewById(R.id.progress);


        mStationName.setText(mStationName.getText().toString() + intent.getStringExtra("name"));
        mCoordination.setText(intent.getStringExtra("lat") + " , " + intent.getStringExtra("lng"));
        mBikesNumber.setText(mBikesNumber.getText().toString() + " " + intent.getStringExtra("num_bikes"));
        //progressBar.setProgress(Integer.valueOf(intent.getStringExtra("progress")));

        polylines = new ArrayList<>();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.singleStationMap);
        supportMapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Double.valueOf(getIntent().getStringExtra("lat")),
                        Double.valueOf(getIntent().getStringExtra("lng")))
                , 17));

        start = new LatLng(Double.valueOf(getIntent().getStringExtra("ulat")),
                Double.valueOf(getIntent().getStringExtra("ulng")));
        end = new LatLng(Double.valueOf(getIntent().getStringExtra("lat")),
                        Double.valueOf(getIntent().getStringExtra("lng")));

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(start, end)
                .build();

        routing.execute();


    }

    @Override
    public void onRoutingFailure(RouteException e) {
        //comment
        //Amine's comment
        //3rd comment
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mGoogleMap.moveCamera(center);


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int index = 0; index <route.size(); index++) {

            //In case of more than 5 alternative routes
            int colorIndex = index % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + index * 3);
            polyOptions.addAll(route.get(index).getPoints());
            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
            polylines.add(polyline);

        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        mGoogleMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_golf_course_red_500_48dp));
        mGoogleMap.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;

            default:
                return true;
        }
    }
}
