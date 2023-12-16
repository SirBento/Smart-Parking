package com.example.smartparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MarshSlotsAdapter extends RecyclerView.Adapter<MarshSlotsAdapter.ViewHolder> {


    Context context;
    ArrayList<MarshBookingData> list;  ///change to a new class if found errors

//private LayoutInflater layoutInflater;
//private List<String> data;

    public MarshSlotsAdapter(Context context, ArrayList<MarshBookingData> list) {
        this.context = context;
        this.list = list;
    }

    public void setFilteredList(ArrayList<MarshBookingData> filteredList){

        this.list = filteredList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MarshSlotsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // View view =LayoutInflater.from(context).inflate(R.layout.customlayout,parent,false);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlayout,null);

        return new MarshSlotsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarshSlotsAdapter.ViewHolder holder, int position) {

        // edits

        holder.MarshalSlotRegNo.setText("Reg_No: "+ list.get(position).getReg_No());
        holder.MarshalSlotDate.setText("Date:"+list.get(position).getDate());
        holder.MarshalSlotTime.setText("Time: "+list.get(position).getTime());
        holder.MarshalSlotDuration.setText("Minutes: "+ list.get(position).getMins());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView MarshalSlotRegNo,MarshalSlotDate,MarshalSlotTime,MarshalSlotDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            MarshalSlotRegNo =itemView.findViewById(R.id.historyregNo);
            MarshalSlotDate =itemView.findViewById(R.id.historydate);
            MarshalSlotTime = itemView.findViewById(R.id.historyTime);
            MarshalSlotDuration = itemView.findViewById(R.id.historyDuration);

        }
    }
}
