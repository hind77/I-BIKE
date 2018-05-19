package com.hindou.smash.Adapters;

import android.content.Context;
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
import com.hindou.smash.Models.Reservation;
import com.hindou.smash.R;
import com.hindou.smash.utils.GlobalVars;
import com.hindou.smash.utils.VolleySingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CancelReservationAdapter extends RecyclerView.Adapter<CancelReservationAdapter.ReservationViewHolder> {

    private List<Reservation> mReservationList;
    private Context mContext;

    public CancelReservationAdapter(List<Reservation> mReservationList, Context mContext) {
        this.mReservationList = mReservationList;
        this.mContext = mContext;
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_reservation_item, parent, false);
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
        Button Cancel;

        public ReservationViewHolder(View itemView) {
            super(itemView);

            bike_name = itemView.findViewById(R.id.bike_name_cancel);
            duration = itemView.findViewById(R.id.bike_reservation_duration_cancel);
            timeStamp = itemView.findViewById(R.id.bike_timestamp_cancel);
            Cancel = itemView.findViewById(R.id.cancel);


            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelReserve(getAdapterPosition());
                }
            });


        }

        private void cancelReserve(int position){
            final Reservation reservation = mReservationList.get(position);
            StringRequest request = new StringRequest(Request.Method.POST, GlobalVars.API_URL+"cancelReservation.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        Log.d("volley",response);

                            mReservationList.remove(getAdapterPosition());
                           notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();


                    //55
                    params.put("id_reserve",reservation.getId_reserve());
                    params.put("id_bike",reservation.getId());


                    return params;
                }
            };
            VolleySingleton.getInstance(mContext).addToRequestQueue(request);
        }
    }
}
