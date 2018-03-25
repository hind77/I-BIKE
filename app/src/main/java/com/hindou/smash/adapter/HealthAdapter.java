package com.hindou.smash.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hindou.smash.Ehealth;
import com.hindou.smash.Models.Health;
import com.hindou.smash.Models.Reservation;
import com.hindou.smash.R;
import com.hindou.smash.utils.GlobalVars;
import com.hindou.smash.utils.VolleySingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HP on 25/03/2018.
 */

public class HealthAdapter extends RecyclerView.Adapter<HealthAdapter.HealthViewHolder> {
    private List<Health> mHealthList;
    private Context mContext;

    public HealthAdapter(List<Health> mHealthList, Context mContext) {
        this.mHealthList = mHealthList;
        this.mContext = mContext;
    }

    @Override
    public HealthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_history_item, parent, false);
        return new HealthViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(HealthViewHolder holder, int position) {

        Health health = mHealthList.get(position);

        holder.temp.setText(health.getTemp());
        holder.beats.setText(health.getBeats());
        holder.calories.setText(health.getCalories());
        holder.date.setText(health.getDate());

    }

    @Override
    public int getItemCount() {
        return mHealthList.size();
    }

    class HealthViewHolder extends RecyclerView.ViewHolder{

        TextView temp,beats, calories, date;
        public HealthViewHolder(View itemView) {
            super(itemView);

            temp = itemView.findViewById(R.id.temperature_data);
            beats = itemView.findViewById(R.id.beats_data);
            calories = itemView.findViewById(R.id.calories_data);
            date = itemView.findViewById(R.id.health_date);

        }



    }



}
