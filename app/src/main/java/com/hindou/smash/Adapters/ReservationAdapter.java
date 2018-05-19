package com.hindou.smash.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hindou.smash.Models.Reservation;
import com.hindou.smash.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static com.google.android.gms.internal.zzahn.runOnUiThread;


public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> mReservationList;
    private Context mContext;
    String writeAPI;
    EditText codeCommand;

    CountDownTimer mCountDownTimer;
    Boolean check = false;
    int i = 0;

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

            lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lock(view,"1");
                }
            }
            );

            unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lock(view,"0");
                }
            });
        }
        public void lock (View v, String c) {
            final MediaPlayer mp = MediaPlayer.create(mContext,R.raw.click);
            i = 0;
            writeAPI = "QGBXNZMVUHNZRW2Z";
            String s = writeAPI;
            mp.start();
            OkHttpClient okHttpClient = new OkHttpClient();
                okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
                okhttp3.Request request = builder.url("https://api.thingspeak.com/update?api_key=" + s + "&field1=" + c).build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        updateView("Error - " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                updateView(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                updateView("Error - " + e.getMessage());
                            }
                        } else {
                            updateView("Not Success - code : " + response.code());
                        }
                    }

                    public void updateView(final String strResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {



                            }
                        });
                    }
                });


            }

        }


    }

