package com.example.smartparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<UserData> list;

    public Adapter(Context context, ArrayList<UserData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       // View view =LayoutInflater.from(context).inflate(R.layout.customlayout,parent,false);

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.customlayout,null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // edits
        holder.userhistoryRegNo.setText("Reg_No: "+list.get(position).getReg_No());
        holder.userhistoryDate.setText("Date:"+list.get(position).getDate());
        holder.userhistoryTime.setText("Time: "+list.get(position).getTime());
        holder.userhistoryDuration.setText("Minutes: "+ list.get(position).getMins());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userhistoryRegNo,userhistoryDate,userhistoryTime,userhistoryDuration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userhistoryRegNo =itemView.findViewById(R.id.historyregNo);
            userhistoryDate =itemView.findViewById(R.id.historydate);
            userhistoryTime = itemView.findViewById(R.id.historyTime);
            userhistoryDuration = itemView.findViewById(R.id.historyDuration);

        }
    }
}



