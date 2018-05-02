package com.hindou.smash.Controllers;

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
import com.hindou.smash.Models.User;
import com.hindou.smash.R;
import com.hindou.smash.Adapters.BikeAdapter;
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

public class ReserveActivity extends AppCompatActivity {

    private SessionsManager sessionsManager;
    private User connectedUser;
    private Toolbar toolbar;
    private Context mContext;

    private RecyclerView mBikeList;
    private BikeAdapter mAdapter;
    private List<Bike> mBikeResourceList;
    private MaterialDialog mLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        initializeComponents();

        //Set screen title
        setTitle(R.string.reserve_activity_title);

        getBikes();
    }


    private void initializeComponents(){
        mContext = this;
        mBikeResourceList = new ArrayList<>();
        mLoader = new MaterialDialog.Builder(this)
                .content(R.string.loading_text)
                .show();
        sessionsManager = SessionsManager.getInstance(this);
        if (!sessionsManager.isActive()){
            changeActivity(LoginActivity.class, true);
        }else{
            connectedUser = sessionsManager.getUser();
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBikeList = findViewById(R.id.bikes_list);
        mBikeList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new BikeAdapter(mBikeResourceList, mContext);
        mBikeList.setAdapter(mAdapter);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

    private void getBikes(){
        StringRequest request = new StringRequest(Request.Method.POST, GlobalVars.API_URL + "getBikes.php",
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

                                    if(data.getJSONObject(i).getString("state").equals("0")){
                                        double distance = Double.valueOf(data.getJSONObject(i).getString("distance"));
                                        distance *= 1000;

                                        mBikeResourceList.add(
                                                new Bike(
                                                        data.getJSONObject(i).getString("id_bike"),
                                                        data.getJSONObject(i).getString("name"),
                                                        data.getJSONObject(i).getString("lat"),
                                                        data.getJSONObject(i).getString("lng"),
                                                        data.getJSONObject(i).getString("state"),
                                                        new Double(distance).intValue(),
                                                        data.getJSONObject(i).getString("station")
                                                        )
                                        );
                                    }

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

                params.put("lat", "33.911397");
                params.put("lng", "-6.925615");

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    public void changeActivity(Class<?> destination, boolean flag) {
        Intent intent = new Intent(ReserveActivity.this, destination);
        startActivity(intent);
        if(flag) finish();
    }

}
