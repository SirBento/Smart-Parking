package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MarshalHomeActivity extends AppCompatActivity {

    private CardView bookedTickets,paymentTickets,stats,userLogout;
    // Reference to the parking slots booking status in the database
    DatabaseReference SlotBookingDatabaseRef;
    //An instance of slot data class to set and get values from the database
    SlotData slotData = new SlotData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marshal_home);

        bookedTickets = findViewById(R.id.marshalBookedTickets);
        paymentTickets =findViewById(R.id.marshalPaymentTicket);
        stats = findViewById(R.id.marshalStatistics);
        userLogout =findViewById(R.id.marshalLogout);

        //Database references for all the 3 slots to check if they are booked or not
        SlotBookingDatabaseRef = FirebaseDatabase.getInstance().getReference("Reservations");

        bookedTickets.setOnClickListener(v -> {

            //open the booking history for all tickets
            startActivity(new Intent( MarshalHomeActivity.this, MarshalBookingActivity.class) );
            finish();

        });

        paymentTickets.setOnClickListener(v -> {

            //open the parking history for all tickets
            startActivity(new Intent( MarshalHomeActivity.this, MarshalSlotTicketsActivity.class) );
            finish();

        });

        stats.setOnClickListener(v -> {

            //open the parking history for all tickets
            startActivity(new Intent( MarshalHomeActivity.this, MarshalActiveTicketActivity.class) );
            finish();
        });


        userLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Toast.makeText(MarshalHomeActivity.this, "Log Out Successful", Toast.LENGTH_LONG).show();

            startActivity(new Intent( MarshalHomeActivity.this, Log_In.class) );
            finish();
        });


        SlotBookingDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String slot1bookStatus = snapshot.child("Slot1").getValue(String.class);
                    String slot1OccStatus = snapshot.child("Slots1_Occ").getValue(String.class);

                    String slot2bookStatus = snapshot.child("Slot2").getValue(String.class);
                    String slot2OccStatus = snapshot.child("Slots2_Occ").getValue(String.class);

                    String slot3bookStatus = snapshot.child("Slot3").getValue(String.class);
                    String slot3OccStatus = snapshot.child("Slots3_Occ").getValue(String.class);

                    slotData.setSlot1_book_Status(slot1bookStatus);
                    slotData.setSlot1_Occ_Status(slot1OccStatus);
                    slotData.setSlot2_book_Status(slot2bookStatus);
                    slotData.setSlot2_Occ_Status(slot2OccStatus);
                    slotData.setSlot3_book_Status(slot3bookStatus);
                    slotData.setSlot3_Occ_Status(slot3OccStatus);

                }

                // Functions for realtime update to the slot status

                updatingSlot1();
                updatingSlot2();
                updatingSlot3();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MarshalHomeActivity.this, "Something Wrong Happened, Please check your internet connection.", Toast.LENGTH_LONG).show();

            }
        });
    }





    private void updatingSlot1(){

        String slot1Book = slotData.getSlot1_book_Status();
        String slot1_Occ = slotData.getSlot1_Occ_Status();

        if (slot1Book.equals("Booked")) {

            sendBookingNotification(1);
            NotifyChannelOreoBooking(1);

        }
        if (slot1Book.equals("NotBooked")) {

            if (slot1_Occ.equals("0")) {

                sendNotification(1);
                NotifyChannelOreoParking(1);
            }

        }

    }

    private void updatingSlot2(){

        String slot2Book = slotData.getSlot2_book_Status();
        String slot2_Occ = slotData.getSlot2_Occ_Status();

        if (slot2Book.equals("Booked")) {

            sendBookingNotification(2);
            NotifyChannelOreoBooking(2);
        }
        if (slot2Book.equals("NotBooked")) {

            if (slot2_Occ.equals("0")) {

                sendNotification(2);
                NotifyChannelOreoParking(2);
            }

        }

    }


    private void updatingSlot3(){

        String slot3Book = slotData.getSlot3_book_Status();
        String slot3_Occ = slotData.getSlot3_Occ_Status();

        if (slot3Book.equals("Booked")) {

            sendBookingNotification(3);
            NotifyChannelOreoBooking(3);

        }
        if (slot3Book.equals("NotBooked")) {

            if (slot3_Occ.equals("0")) {

                sendNotification(3);
                NotifyChannelOreoParking(3);
            }
            }


    }

    //deleting a notification channel
    // notificationManager.deleteNotificationChannel("channel_id");

    private void sendNotification(int slot){


        NotificationCompat.Builder builder  = new NotificationCompat.Builder(MarshalHomeActivity.this,"Parking Notification");
        builder.setContentTitle("Smart Parking");
        builder.setContentText("A car has just parked in slot "+ slot);
        builder.setSmallIcon(R.drawable.notifcation);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MarshalHomeActivity.this);
        managerCompat.notify(1,builder.build());


    }


    private void sendBookingNotification(int slot){

        NotificationCompat.Builder builder  = new NotificationCompat.Builder(MarshalHomeActivity.this,"Booking Notification");
        builder.setContentTitle("Smart Parking");
        builder.setContentText(slot +"Has been booked!");
        builder.setSmallIcon(R.drawable.notifcation);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MarshalHomeActivity.this);
        managerCompat.notify(1,builder.build());


    }

    private void NotifyChannelOreoBooking(int slot){
        //for oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new  NotificationChannel("Booking Notification","Booking Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder= new NotificationCompat.Builder(MarshalHomeActivity.this,"Parking Notification");
            builder.setContentTitle("Smart Parking");
            builder.setContentText("Slot "+slot +" Has been booked!");
            builder.setSmallIcon(R.drawable.notifcation);
            builder.setAutoCancel(true);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MarshalHomeActivity.this);
            managerCompat.notify(1,builder.build());


        }
    }

    private void NotifyChannelOreoParking(int slot){
        //for oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new  NotificationChannel("Parking Notification","Parking Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder= new NotificationCompat.Builder(MarshalHomeActivity.this,"Parking Notification");
            builder.setContentTitle("Smart Parking");
            builder.setContentText("A car has just parked in slot "+ slot);
            builder.setSmallIcon(R.drawable.notifcation);
            builder.setAutoCancel(true);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MarshalHomeActivity.this);
            managerCompat.notify(1,builder.build());




        }
    }
}