package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ParkingHistory extends AppCompatActivity {
   RecyclerView recyclerView;
   DatabaseReference database;
   Adapter myAdapter;
   ArrayList<UserData> list;
   String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
   historyData historyData = new historyData();
   String duration,date,reg_no,time,Uid;
   Boolean historyExist = false;

    private TextView noHistoryText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_history);

        recyclerView = findViewById(R.id.historylist);
        noHistoryText = findViewById(R.id.noHistoryText);

        database = FirebaseDatabase.getInstance().getReference("Bookings");


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new Adapter(this,list);
        recyclerView.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    reg_no = dataSnapshot.child("Reg_No").getValue(String.class);
                    date = dataSnapshot.child("Date").getValue(String.class);
                    time = dataSnapshot.child("Time").getValue(String.class);
                    duration = dataSnapshot.child("Duration").getValue(String.class);
                    Uid = dataSnapshot.child("Uid").getValue(String.class);


                    historyData.setHreg_no(reg_no);
                    historyData.setHdate(date);
                    historyData.setHtime(time);
                    historyData.setHduration(duration);
                    historyData.sethUid(Uid);

            // IF THE UID requested is the same as the uid of the current user then collect the current user's data
                    if(userUid.equals(historyData.gethUid())){

                        historyExist= true;
                        String getUserRegNo = historyData.getHreg_no();
                        String getUserDate = historyData.getHdate();
                        String getUserTime = historyData.getHtime();
                        String getUserDuration = historyData.getHduration();


                        //Sending data to the User Data class
                        UserData newUser = new UserData();
                        newUser.setReg_No(getUserRegNo);
                        newUser.setDate(getUserDate);
                        newUser.setTime(getUserTime);
                        newUser.setMins(getUserDuration);
                        list.add(newUser);

                    }else{

                        historyExist= false;
                    }



                }

                myAdapter.notifyDataSetChanged();

                if(historyExist== false){

                    Toast.makeText(ParkingHistory.this, "You haven't parked with us yet", Toast.LENGTH_LONG).show();
                    noHistoryText.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        //Show loading dialog to ensure that all the data is collected from the database to populate charts
        //
        LoadingDialog loadingDialog = new LoadingDialog(ParkingHistory.this);
        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                loadingDialog.dismissDialog();
            }
        }, 7000); // dismiss the dialog after 7 seconds
}

}