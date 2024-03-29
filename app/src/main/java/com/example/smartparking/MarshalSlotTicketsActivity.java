package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MarshalSlotTicketsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    MarshBookingAdapter myAdapter;
    ArrayList<MarshBookingData> list;
    private SearchView searchView;

    historyData historyData = new historyData();
    String duration,date,reg_no,time,Uid;

    private TextView noHistoryText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marshal_slot_tickets);


        searchView = findViewById(R.id.MarshalSlotSearchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.MarshalSlotHistoryList);
        noHistoryText = findViewById(R.id.MarshalSlotnoHistoryText);

        database = FirebaseDatabase.getInstance().getReference("ParkingPayments");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new MarshBookingAdapter(this,list);
        recyclerView.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        reg_no = dataSnapshot.child("Reg_No").getValue(String.class);
                        date = dataSnapshot.child("Date").getValue(String.class);
                        time = dataSnapshot.child("Time").getValue(String.class);
                        duration = dataSnapshot.child("Duration").getValue(String.class);

                        historyData.setHreg_no(reg_no);
                        historyData.setHdate(date);
                        historyData.setHtime(time);
                        historyData.setHduration(duration);
                        historyData.sethUid(Uid);


                        String getUserRegNo = historyData.getHreg_no();
                        String getUserDate = historyData.getHdate();
                        String getUserTime = historyData.getHtime();
                        String getUserDuration = historyData.getHduration();


                        //Sending data to the User Data class
                        MarshBookingData BookingData = new MarshBookingData();
                        BookingData.setReg_No(getUserRegNo);
                        BookingData.setDate(getUserDate);
                        BookingData.setTime(getUserTime);
                        BookingData.setMins(getUserDuration);
                        list.add(BookingData);

                    }

                    myAdapter.notifyDataSetChanged();

                }else{

                    Toast.makeText(MarshalSlotTicketsActivity.this, "No parking tickets yet", Toast.LENGTH_LONG).show();

                        noHistoryText.setVisibility(View.VISIBLE);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        //Show loading dialog to ensure that all the data is collected from the database to populate charts
        //
        LoadingDialog loadingDialog = new LoadingDialog(MarshalSlotTicketsActivity.this);
        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                loadingDialog.dismissDialog();
            }
        }, 7000); // dismiss the dialog after 7 seconds

    }

    private void filterList(String text) {

        ArrayList<MarshBookingData> filteredlist = new ArrayList<>();

        for(MarshBookingData marshBookingData: list){

            if(marshBookingData.getReg_No().toLowerCase().contains(text.toLowerCase())){

                filteredlist.add(marshBookingData);
            }
        }

        if(filteredlist.isEmpty()){
            Toast.makeText(this, "No ticket by that Plate Number", Toast.LENGTH_SHORT).show();
        }else{

            myAdapter.setFilteredList(filteredlist);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent( MarshalSlotTicketsActivity.this, MarshalHomeActivity.class) );
        finish();
    }
}
