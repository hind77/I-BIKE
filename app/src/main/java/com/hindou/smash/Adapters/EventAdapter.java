package com.hindou.smash.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hindou.smash.Models.Event;

import com.hindou.smash.R;

import java.util.List;

/**
 * Created by HP on 05/04/2018.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{
private List<Event> mEventList;
private Context mContext;

public EventAdapter(List<Event> mEventList,Context mContext){
        this.mEventList=mEventList;
        this.mContext=mContext;
        }

@Override
public EventViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        return new EventViewHolder(itemView);
        }


@Override
public void onBindViewHolder(EventViewHolder holder,int position){

       Event event =mEventList.get(position);

        holder.title.setText(event.getName());
        holder.description.setText(event.getDescription());
        holder.Date.setText(event.getDate());


        }

@Override
public int getItemCount(){
        return mEventList.size();
        }

class EventViewHolder extends RecyclerView.ViewHolder {

    TextView title, description, Date;

    public EventViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.event_title_data);
        description = itemView.findViewById(R.id.body_event);
        Date = itemView.findViewById(R.id.event_date_data);

    }


}}





