package com.hindou.smash.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.hindou.smash.Models.Health;
import com.hindou.smash.Models.User;
import com.hindou.smash.R;
import com.hindou.smash.Adapters.HealthAdapter;
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

/**
 * Created by HP on 25/03/2018.
 */

public class HealthHistoryActivity extends AppCompatActivity {
    private SessionsManager sessionsManager;
    private User connectedUser;
    private Toolbar toolbar;
    private Context mContext;

    private RecyclerView mHealthHistoryList;
    private HealthAdapter mAdapter;
    private List<Health> mHealthResourceList;
    private MaterialDialog mLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_history);

        initializeComponents();

        //Set screen title
        setTitle(R.string.Health_History_title);

        getHistory();
    }
    private void initializeComponents(){
        mContext = this;
        mHealthResourceList = new ArrayList<>();
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

        mHealthHistoryList = findViewById(R.id.historyList);
        mHealthHistoryList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new HealthAdapter(mHealthResourceList, mContext);
        mHealthHistoryList.setAdapter(mAdapter);

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

    private void getHistory(){
        StringRequest request = new StringRequest(Request.Method.POST, GlobalVars.API_URL + "getHealthHistory.php",
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




                                        mHealthResourceList.add(
                                                new Health(
                                                            data.getJSONObject(i).getString("id"),
                                                            data.getJSONObject(i).getString("id_user"),
                                                            data.getJSONObject(i).getString("temp"),
                                                            data.getJSONObject(i).getString("beats"),
                                                            data.getJSONObject(i).getString("calories"),
                                                            data.getJSONObject(i).getString("date")
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
    public void changeActivity(Class<?> destination, boolean flag) {
        Intent intent = new Intent(HealthHistoryActivity.this, destination);
        startActivity(intent);
        if(flag) finish();
    }


}
