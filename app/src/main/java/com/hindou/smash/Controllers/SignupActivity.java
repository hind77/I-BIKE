package com.hindou.smash.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hindou.smash.Models.User;
import com.hindou.smash.R;
import com.hindou.smash.utils.GlobalVars;
import com.hindou.smash.utils.SessionsManager;
import com.hindou.smash.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mFirstName, mLastName, mPassword, mEmail;
    private Button mSignup;
    private TextView mLogin;
    private SessionsManager sessionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionsManager = SessionsManager.getInstance(this);

        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText) findViewById(R.id.email);

        mLogin = (TextView) findViewById(R.id.signupText);
        mLogin.setOnClickListener(this);

        mSignup = (Button) findViewById(R.id.signgup);
        mSignup.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signgup :
                createUser();
                break;

            case R.id.signupText:
                changeActivity(LoginActivity.class, true);
                break;
        }
    }

    private void createUser(){

        Log.d("VOLLEY", "Request sent to " + GlobalVars.API_URL + "createUser.php");
        StringRequest request = new StringRequest(Request.Method.POST, GlobalVars.API_URL + "createUser.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Volley", "Response from " + response);
                        try{
                            JSONObject rep = (JSONObject) new JSONTokener(response).nextValue();
                            if(rep.getString("code").equals("200")){
                                sessionsManager.createSession(new User(
                                        0,
                                        mFirstName.getText().toString(),
                                        mLastName.getText().toString(),
                                        mEmail.getText().toString()
                                ));
                                changeActivity(StationActivity.class, true);
                            }else{
                                Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("first_name", mFirstName.getText().toString());
                params.put("last_name", mLastName.getText().toString());
                params.put("password", mPassword.getText().toString());
                params.put("email", mEmail.getText().toString());

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void changeActivity(Class<?> destination, boolean finishFlag){
        Intent intent = new Intent(SignupActivity.this, destination);
        startActivity(intent);
        if (finishFlag) finish();
    }
}
