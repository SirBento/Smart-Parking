package com.example.smartparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MarshalActiveTicketAdapter extends RecyclerView.Adapter<MarshalActiveTicketAdapter.ViewHolder> {

    Context context;
    ArrayList<MarshBookingData> list;


    public MarshalActiveTicketAdapter(Context context, ArrayList<MarshBookingData> list) {
        this.context = context;
        this.list = list;
    }

    public void setFilteredList(ArrayList<MarshBookingData> filteredList){

        this.list = filteredList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MarshalActiveTicketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // View view =LayoutInflater.from(context).inflate(R.layout.customlayout,parent,false);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlayout,null);

        return new MarshalActiveTicketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarshalActiveTicketAdapter.ViewHolder holder, int position) {



        // edits

        /***
         holder.MarshalBookRegNo.setText("Reg_No: "+ list.get(position).reg_No);
         holder.MarshalBookhistoryDate.setText("Date:"+list.get(position).date);
         holder.MarshalBookhistoryTime.setText("Time: "+list.get(position).time);
         holder.MarshalBookhistoryDuration.setText("Minutes: "+ list.get(position).mins);

         */
        holder.MarshalActiveTicketName.setText("Name: "+ list.get(position).getReg_No());
        holder.MarshalActiveTicketDate.setText("Date:"+list.get(position).getDate());
        holder.MarshalActiveTicketNum.setText("TicketNo: "+list.get(position).getTime());
        holder.MarshalActiveTicketDuration.setText("Minutes: "+ list.get(position).getMins());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView MarshalActiveTicketName,MarshalActiveTicketDate,MarshalActiveTicketNum,MarshalActiveTicketDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            MarshalActiveTicketName =itemView.findViewById(R.id.historyregNo);
            MarshalActiveTicketDate =itemView.findViewById(R.id.historydate);
            MarshalActiveTicketNum= itemView.findViewById(R.id.historyTime);
            MarshalActiveTicketDuration = itemView.findViewById(R.id.historyDuration);
            /***
             MarshalBookRegNo =itemView.findViewById(R.id.MarshalBookhistoryregNo);
             MarshalBookDate =itemView.findViewById(R.id.MarshalBookhistorydate);
             MarshalBookTime = itemView.findViewById(R.id.MarshalBookhistoryTime);
             MarshalBookDuration = itemView.findViewById(R.id.MarshalBookhistoryDuration);


             */

        }
    }


}
