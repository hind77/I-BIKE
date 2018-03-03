package com.hindou.smash.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hindou.smash.Models.Bike;
import com.hindou.smash.Models.User;
import com.hindou.smash.R;
import com.hindou.smash.utils.GlobalVars;
import com.hindou.smash.utils.SessionsManager;
import com.hindou.smash.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amineelouattar on 3/3/18.
 */

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.BikeViewHolder> {

    private List<Bike> bikeList;
    private Context mContext;
    private MaterialDialog mDurationDialog;
    private User connectedUser;

    public BikeAdapter(List<Bike> bikeList, Context mContext) {
        this.bikeList = bikeList;
        this.mContext = mContext;
    }

    @Override
    public BikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_item_list, parent, false);
        return new BikeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BikeViewHolder holder, int position) {

        Bike current = bikeList.get(position);

        holder.bikeName.setText(current.getName());
        holder.distance.setText(String.valueOf(current.getDistance()));
        holder.station.setText(current.getStation());
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    class BikeViewHolder extends RecyclerView.ViewHolder{

        private TextView bikeName, distance, station;

        public BikeViewHolder(View itemView) {
            super(itemView);

            bikeName = itemView.findViewById(R.id.bicycle_name);
            distance = itemView.findViewById(R.id.bicycle_distance);
            station = itemView.findViewById(R.id.bicycle_station);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean wrapInScrollView = true;
                    mDurationDialog = new MaterialDialog.Builder(mContext)
                            .title(R.string.reserve_activity_title)
                            .customView(R.layout.reserve_bike_hours, wrapInScrollView)
                            .positiveText(R.string.select_hours_positive)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    View itemView = mDurationDialog.getCustomView();
                                    connectedUser = SessionsManager.getInstance(mContext).getUser();
                                    EditText duration = itemView.findViewById(R.id.duration);

                                    createReservation(getAdapterPosition(), Integer.valueOf(duration.getText().toString()));
                                }
                            })
                            .show();
                }
            });
        }

        private void createReservation(final int position, final int duration){

            StringRequest request = new StringRequest(Request.Method.POST, GlobalVars.API_URL + "createReservation.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("VOLLEY RESPONSE", response);
                            try {
                                JSONObject reponse = (JSONObject) new JSONTokener(response).nextValue();
                                if (reponse.getString("code").equals("200")){

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();

                    params.put("id_user", String.valueOf(connectedUser.getId()));
                    params.put("id_bike", bikeList.get(position).getId_bike());
                    params.put("duration", String.valueOf(duration));

                    return params;
                }
            };

            VolleySingleton.getInstance(mContext).addToRequestQueue(request);
        }
    }
}
