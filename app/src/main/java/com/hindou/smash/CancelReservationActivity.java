package com.hindou.smash;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hindou.smash.Models.Bike;
import com.hindou.smash.Models.Reservation;
import com.hindou.smash.Models.User;
import com.hindou.smash.adapter.BikeAdapter;
import com.hindou.smash.adapter.CancelReservationAdapter;
import com.hindou.smash.adapter.ReservationAdapter;
import com.hindou.smash.utils.GlobalVars;
import com.hindou.smash.utils.SessionsManager;
import com.hindou.smash.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancelReservationActivity extends AppCompatActivity {

    private SessionsManager sessionsManager;
    private User connectedUser;
    private Toolbar toolbar;
    private Context mContext;

    private RecyclerView mReservationList;
    private CancelReservationAdapter mAdapter;
    private List<Reservation> mReservationSrcList;
    private MaterialDialog mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reservation);
        sessionsManager = SessionsManager.getInstance(this);

        if (!sessionsManager.isActive()){
            changeActivity(LoginActivity.class, true);
        }else{
            initializeComponents();
            getReservations();
        }
    }

    private void initializeComponents(){


        //Set screen title
        setTitle(R.string.cancel_activity_title);

        mContext = this;
        mReservationSrcList = new ArrayList<>();

        connectedUser = sessionsManager.getUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectedUser = sessionsManager.getUser();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mReservationList = findViewById(R.id.reservation_list_cancel);
        mReservationList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CancelReservationAdapter(mReservationSrcList, mContext);
        mReservationList.setAdapter(mAdapter);

        mLoader = new MaterialDialog.Builder(this)
                .content(R.string.loading_text)
                .show();

    }


    private void getReservations(){


        StringRequest request = new StringRequest(Request.Method.POST, GlobalVars.API_URL + "getReservation.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VOLLEY RESPONSE", response);
                        mLoader.dismiss();
                        try {
                            JSONObject reponse = (JSONObject) new JSONTokener(response).nextValue();
                            if (reponse.getString("code").equals("200")){
                                JSONArray data = reponse.getJSONArray("data");
                                for(int i = 0; i < data.length(); i++){

                                    mReservationSrcList.add(
                                            new Reservation(
                                                    data.getJSONObject(i).getString("id_reserve"),
                                                    data.getJSONObject(i).getString("id"),
                                                    data.getJSONObject(i).getString("name"),
                                                    data.getJSONObject(i).getString("duration"),
                                                    data.getJSONObject(i).getString("timestamp")

                                            )
                                    );

                                }

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mLoader.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("id_user", String.valueOf(connectedUser.getId()));

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    private void changeActivity(Class<?> destination, boolean flag) {
        Intent intent = new Intent(CancelReservationActivity.this, destination);
        startActivity(intent);
        if(flag) finish();
    }

}
