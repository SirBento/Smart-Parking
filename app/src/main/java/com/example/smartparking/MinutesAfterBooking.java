package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MinutesAfterBooking extends AppCompatActivity {

    private TextView bookcountDown;
    private Button iHaveParked;
    private String slotVal;
    private static final String FORMAT = "%02d:%02d:%02d";
    private TextView ticketName, ticketDate ,ticketSlot, ticketDuration,bookticketMessage;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference BookedslotRef = mDatabase.getReference().child("Reservations");
    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference databaseBookingRef = mDatabase .getReference("BookingTicket");
    private DatabaseReference tickect = mDatabase.getReference().child("CurrentTicket");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minutes_after_booking);

        bookcountDown =  findViewById(R.id.BookingCountdown);
        iHaveParked = findViewById(R.id.btnParked);
        ticketName = findViewById(R.id.bookerName);
        ticketDate = findViewById(R.id.bookTime);
        ticketSlot = findViewById(R.id.slotBooked);
        ticketDuration = findViewById(R.id.minutesBooked);
        bookticketMessage = findViewById(R.id.bookticketMessage);

        //assign in to the value in the booking ticket

        slotVal = GlobalVariables.bookSlot;

        //starting the service
        Intent intent = new Intent(this, BroadcastServiceBooking.class);
        startService(intent);

        //if the current user id matches the uid on the ticket, it gets deleted
        if(userUid.equals(GlobalVariables.bookUid)) {

            ticketName.setText("Name: "+ GlobalVariables.bookTicketname);
            ticketDate.setText("Date: " + GlobalVariables.bookTicketDate );
            ticketSlot.setText("Slot: "  + GlobalVariables.bookSlot);
            ticketDuration.setText("Mins: "  + GlobalVariables.bookTicketDuration);
            bookticketMessage.setVisibility(View.INVISIBLE);

        }

        // If there is no current ticket  then set the data to default
        if (GlobalVariables.bookingTicketExist){
            bookcountDown.setVisibility(View.VISIBLE);
        }else{

            ticketName.setText("Name: Null");
            ticketDate.setText("Date: Null");
            ticketSlot.setText("Slot: Null");
            ticketDuration.setText("Mins: Null");
            bookticketMessage.setVisibility(View.VISIBLE);
            bookcountDown.setText("00:00");
            bookcountDown.setVisibility(View.INVISIBLE);
        }



        iHaveParked.setOnClickListener(view -> {


            if (GlobalVariables.bookingTicketrunning){

                revokeReservation();

                createCurrentTicket();
                GlobalVariables.bookStopTimer = true;
                GlobalVariables.bookuserMillisec = 0000;
                BroadcastServiceBooking.countDownTimer.cancel();
                //reset global variables
                startActivity(new Intent( MinutesAfterBooking.this, TimeLeft.class) );
                finish();

            }else{

                Toast.makeText(MinutesAfterBooking.this, "Your Booking Was terminated because you failed to park in time!!!", Toast.LENGTH_LONG).show();

            }

        });


    }




    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Update GUI
            updateGUI(intent);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastServiceBooking.COUNTDOWN_BR));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,BroadcastServiceBooking.class));
        super.onDestroy();
    }

    private void updateGUI(Intent intent){

        if(intent.getExtras() != null){

            long millisUntilFinished = intent.getLongExtra("countdown",0);

            bookcountDown.setText(""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            String timeValue = bookcountDown.getText().toString();

            if(timeValue.equals("00:00:01")){

                revokeReservation();
            }
        }else{

            //Terminate ticket as soon as the count down reaches zero

            revokeReservation();
        }

    }



    @Override
    protected void onStop() {

        try {

            unregisterReceiver(broadcastReceiver);

        }catch (Exception e){

            // broadcast receiver was probably already unregistered
        }

        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }




    private void revokeReservation(){

        if(slotVal.equals("1")){

            HashMap Slot1booking = new HashMap<>();
            Slot1booking.put("Slot1", "NotBooked" );
            BookedslotRef.updateChildren(Slot1booking);



        }else if(slotVal.equals("2")){

            HashMap Slot2booking = new HashMap<>();
            Slot2booking.put("Slot2", "NotBooked" );
            BookedslotRef.updateChildren(Slot2booking);



        }else if(slotVal.equals("3")){

            HashMap Slot3booking = new HashMap<>();
            Slot3booking.put("Slot3", "NotBooked" );
            BookedslotRef.updateChildren(Slot3booking);

        }


                        //if the current user id matches the uid on the ticket, it gets deleted
         if(userUid.equals(GlobalVariables.bookUid)){

            databaseBookingRef.removeValue();

             ticketName.setText("Name: Null");
             ticketDate.setText("Date: Null" );
             ticketSlot.setText("Slot: Null");
             ticketDuration.setText("Mins: Null");
             bookticketMessage.setVisibility(View.VISIBLE);
             bookcountDown.setText("00:00");
         }

     }


     private void createCurrentTicket(){

             HashMap CurrentTicket = new HashMap<>();

             CurrentTicket.put("Driver", GlobalVariables.bookTicketname );
             CurrentTicket.put("Reg_No", GlobalVariables.bookregNo);
             CurrentTicket.put("Date", GlobalVariables.bookTicketDate);
             CurrentTicket.put("Duration", GlobalVariables.bookTicketDuration);
             CurrentTicket.put("Uid", userUid );

             tickect.push().setValue(CurrentTicket);

     }



}