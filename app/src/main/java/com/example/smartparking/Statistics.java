package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Statistics extends AppCompatActivity {

    //Buttons to open the charts
    private Button OpenPieChart, OpenBarchart;
  // the project database reference
    private DatabaseReference slotsDataRef;

    // Date variable to store the date ranges.
    Date userDate= null,
         six = null,
         nine = null,
         twelve = null,
         fifteen = null,
         eighteen =null;

    // storing time for range comparison to determine the busiest period
    String sixAM = "6:00",
           timeString2 = "9:00",
           timeString3 = "12:00",
           timeString4 = "15:00",
           timeString5 = "18:00";

    // An instance of the of the date sample to be used during determining pick hours.
    SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

    //values to store the day and time from the database
    String SlotDay,slotTime;

    //time counters
    int beforeNineCount = 0 ,
        afterNineCount = 0 ,
        afterTwelveCount = 0 ,
        afterThreeCount = 0 ,
        afterSixCount=0;

    //day counters
   int SundayCount = 0,
       MondayCount = 0 ,
       TuesdayCount = 0,
       WednesdayCount = 0,
       ThursdayCount = 0,
       FridayCount = 0,
       SaturdayCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        OpenPieChart = findViewById(R.id.btnViewPieChart);
        OpenBarchart = findViewById(R.id.btnViewBarChart);

        //A reference to the node in the database that stores information about all the slots
        slotsDataRef = FirebaseDatabase.getInstance().getReference("Slots/Data");


        slotsDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {


                        SlotDay = dataSnapshot.child("0").getValue().toString();
                        slotTime = dataSnapshot.child("1").getValue().toString();

                        countDateValue();
                        DateParsing();
                        determineTimeRange();


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });

        // WHEN CLICKED, OPENS AND DISPLAY A PIE CHART WITH TEH NUMBER OF CARS PARKED EACH DAY.
        OpenPieChart.setOnClickListener(v -> {

          //  Intent intent = new Intent( Statistics.this,PieChartStats.class);

            Intent intent = new Intent( Statistics.this,PieChartStats.class);
            intent.putExtra("keySundayValues",SundayCount);
            intent.putExtra("keyMondayValues",MondayCount);
            intent.putExtra("keyTuesdayValues",TuesdayCount);
            intent.putExtra("keyWednesdayValues",WednesdayCount);
            intent.putExtra("keyThursdayValues",ThursdayCount);
            intent.putExtra("keyFridayValues",FridayCount);
            intent.putExtra("keySaturdayValues",SaturdayCount);
            startActivity(intent);
        });

        // WHEN CLICKED, OPENS AND DISPLAY A PIE CHART WITH TEH NUMBER OF CARS PARKED EACH DAY.
        OpenBarchart.setOnClickListener(v -> {

            Intent intent = new Intent( Statistics.this,BarChartStats.class);
            intent.putExtra("keyBeforeNineValues",beforeNineCount);
            intent.putExtra("keyAfterNineValues",afterNineCount );
            intent.putExtra("keyAfterTwelveValues",afterTwelveCount);
            intent.putExtra("keyAfterThreeValues",afterThreeCount);
            intent.putExtra("keyAfterSixValues",afterSixCount);
            startActivity(intent);

        });

        //Show loading dialog to ensure that all the data is collected from the database to populate charts
        //
        LoadingDialog loadingDialog = new LoadingDialog(Statistics.this);
        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                loadingDialog.dismissDialog();
            }
        }, 7000); // dismiss the dialog after 7 seconds
    }





    //Function to parse the string time range to the actual time
    private void DateParsing(){

        try {
            six = parser.parse(sixAM);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            nine = parser.parse(timeString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            twelve = parser.parse(timeString3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            fifteen = parser.parse(timeString4);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            eighteen = parser.parse(timeString5);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            userDate = parser.parse(slotTime);

        } catch (ParseException e) {

            // Invalid date was entered
            String error = e.toString();
        }
    }

    // conditions to determine the time range of the time from the database.
    private void determineTimeRange(){


        if(userDate.equals(six)|| userDate.equals(nine)){

            beforeNineCount++;
        }

        if (userDate.after(six) && userDate.before(nine)) {

            beforeNineCount++;
        }

        if(userDate.after(nine)&& userDate.before(twelve)){

            afterNineCount++;
        }

        if(userDate.equals(twelve)){

            afterNineCount++;
        }

        if(userDate.after(twelve)&& userDate.before(fifteen)){

            afterTwelveCount++;
        }

        if(userDate.equals(fifteen)){

            afterTwelveCount++;
        }

        if(userDate.after(fifteen)&& userDate.before(eighteen)){

            afterThreeCount++;
        }

        if(userDate.equals(eighteen) || userDate.after(eighteen)){

            afterSixCount++;
        }
    }

    //counts the number of cars packed each day.
    private void countDateValue(){

        if(SlotDay.equals("Sunday")){

            SundayCount++;
        }

        if(SlotDay.equals("Monday")){

            MondayCount++;
        }

        if(SlotDay.equals("Tuesday")){

            TuesdayCount++;
        }

        if(SlotDay.equals("Wednesday")){

            WednesdayCount++;
        }

        if(SlotDay.equals("Thursday")){

            ThursdayCount++;
        }

        if(SlotDay.equals("Friday")){

            FridayCount++;
        }

        if(SlotDay.equals("Saturday")){

            SaturdayCount++;
        }

    }

}