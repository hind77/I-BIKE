package com.hindou.smash;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hindou.smash.Models.User;
import com.hindou.smash.utils.GlobalVars;
import com.hindou.smash.utils.SessionsManager;
import com.hindou.smash.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, password;
    private Button login;
    private View holder;
    private SessionsManager sessionsManager;
    private TextView signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionsManager = SessionsManager.getInstance(this);
        if (sessionsManager.isActive()){
            changeActivity(MainActivity.class);
        }



        signup = (TextView) findViewById(R.id.signupText);
        signup.setOnClickListener(this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        holder = findViewById(R.id.loginholder);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    private void changeActivity(Class<?> destination){
        Intent intent = new Intent(LoginActivity.this, destination);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login :
                login();
                break;
            case R.id.signupText :
                changeActivity(SignupActivity.class);
                break;
        }
    }

    private void login(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalVars.API_URL + "login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VOLLEY_NETWORKS", response);

                        try {
                            JSONObject rep = (JSONObject) new JSONTokener(response).nextValue();
                            if (rep.getString("code").equals("200")){
                                JSONObject data = rep.getJSONObject("data");
                                sessionsManager.createSession(new User(
                                        data.getInt("id"),
                                        data.getString("fname"),
                                        data.getString("lname"),
                                        data.getString("email")
                                ));
                                changeActivity(MainActivity.class);


                            }else
                                showSnack(rep.getString("error"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnack(error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                //55
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showSnack(String message){
        Snackbar.make(holder, message, Snackbar.LENGTH_SHORT).show();
    }
}
