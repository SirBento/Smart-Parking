package com.example.smartparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class MarshBookingAdapter extends RecyclerView.Adapter<MarshBookingAdapter.ViewHolder> {

        Context context;
        ArrayList<MarshBookingData> list;


public MarshBookingAdapter(Context context, ArrayList<MarshBookingData> list) {
        this.context = context;
        this.list = list;
        }

public void setFilteredList(ArrayList<MarshBookingData> filteredList){

    this.list = filteredList;
    notifyDataSetChanged();

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

    /***
        holder.MarshalBookRegNo.setText("Reg_No: "+ list.get(position).reg_No);
        holder.MarshalBookhistoryDate.setText("Date:"+list.get(position).date);
        holder.MarshalBookhistoryTime.setText("Time: "+list.get(position).time);
        holder.MarshalBookhistoryDuration.setText("Minutes: "+ list.get(position).mins);

     */
        holder.MarshalBookRegNo.setText("Reg_No: "+ list.get(position).getReg_No());
        holder.MarshalBookDate.setText("Date:"+list.get(position).getDate());
        holder.MarshalBookTime.setText("Time: "+list.get(position).getTime());
        holder.MarshalBookDuration.setText("Minutes: "+ list.get(position).getMins());


        }

@Override
public int getItemCount() {
        return list.size();
        }

public static class ViewHolder extends RecyclerView.ViewHolder{

    TextView MarshalBookRegNo,MarshalBookDate,MarshalBookTime,MarshalBookDuration;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);


                MarshalBookRegNo =itemView.findViewById(R.id.historyregNo);
                MarshalBookDate =itemView.findViewById(R.id.historydate);
                MarshalBookTime = itemView.findViewById(R.id.historyTime);
                MarshalBookDuration = itemView.findViewById(R.id.historyDuration);
                /***
                    MarshalBookRegNo =itemView.findViewById(R.id.MarshalBookhistoryregNo);
                    MarshalBookDate =itemView.findViewById(R.id.MarshalBookhistorydate);
                    MarshalBookTime = itemView.findViewById(R.id.MarshalBookhistoryTime);
                    MarshalBookDuration = itemView.findViewById(R.id.MarshalBookhistoryDuration);


                 */

    }
}
}
