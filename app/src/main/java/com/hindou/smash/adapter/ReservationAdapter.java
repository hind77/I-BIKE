package com.hindou.smash.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hindou.smash.Models.Reservation;
import com.hindou.smash.R;

import java.util.List;

/**
 * Created by amineelouattar on 3/3/18.
 */

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> mReservationList;
    private Context mContext;

    public ReservationAdapter(List<Reservation> mReservationList, Context mContext) {
        this.mReservationList = mReservationList;
        this.mContext = mContext;
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lock_bike_item, parent, false);
        return new ReservationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {

        Reservation reservation = mReservationList.get(position);

        holder.bike_name.setText(reservation.getBike_name());
        holder.duration.setText(reservation.getDuration());
        holder.timeStamp.setText(reservation.getTimestamp());

    }

    @Override
    public int getItemCount() {
        return mReservationList.size();
    }

    class ReservationViewHolder extends RecyclerView.ViewHolder{

        TextView bike_name, duration, timeStamp;
        Button lock, unlock;

        public ReservationViewHolder(View itemView) {
            super(itemView);

            bike_name = itemView.findViewById(R.id.bike_name);
            duration = itemView.findViewById(R.id.bike_reservation_duration);
            timeStamp = itemView.findViewById(R.id.bike_timestamp);
            lock = itemView.findViewById(R.id.lock_bike);
            unlock = itemView.findViewById(R.id.unlock_bike);
        }
    }
}
