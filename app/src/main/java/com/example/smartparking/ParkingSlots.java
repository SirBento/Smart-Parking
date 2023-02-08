package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ParkingSlots extends AppCompatActivity {

    // Reference to the parking slots booking status in the database
    private DatabaseReference  SlotBookingDatabaseRef;


   //integer values for red and green colors
    private int redColor = Color.parseColor("#FFE60F0F");
    private int greenColor = Color.parseColor("#6AC841");


    // slots cards
    private CardView slot1Card,
                     slot2Card,
                     slot3Card;
    // slots images
    private ImageView carslot1,
                      carslot2,
                      carslot3;

    //An instance of slot data class to set and get values from the database
    SlotData slotData = new SlotData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_slots);




        //instances of image views and card views from the design xml file
         slot1Card = findViewById(R.id.parkingslot1);
         slot2Card = findViewById(R.id.parkingslot2);
         slot3Card = findViewById(R.id.parkingslot3);

         carslot1 =findViewById(R.id.slot1);
         carslot2 =findViewById(R.id.slot2);
         carslot3 =findViewById(R.id.slot3);



        //Database references for all the 3 slots to check if they are booked or not
        SlotBookingDatabaseRef = FirebaseDatabase.getInstance().getReference("Reservations");//Reservations/Slots1_Occ

        //Listen to realtime  changes on the slots whether booked or occupied

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


            }
        });


        //opens booking page if the slot one is clicked

        slot1Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( ParkingSlots.this,SlotBooking.class);
                intent.putExtra("keyname","1");
                startActivity(intent);
            }
        });

        //opens booking page if the slot two is clicked

        slot2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( ParkingSlots.this,SlotBooking.class);
                intent.putExtra("keyname","2");
                startActivity(intent);
            }
        });

        //opens booking page if the slot three is clicked

        slot3Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( ParkingSlots.this,SlotBooking.class);
                intent.putExtra("keyname","3");
                startActivity(intent);

            }
        });

    }




    private void updatingSlot1(){

        String slot1Book = slotData.getSlot1_book_Status();
        String slot1_Occ = slotData.getSlot1_Occ_Status();

        if (slot1Book.equals("Booked")) {

            carslot1.setBackgroundColor(redColor);
            slot1Card.setClickable(false);
            GlobalVariables.slot1Status =true;

        }
        if (slot1Book.equals("NotBooked")) {

            if (slot1_Occ.equals("0")) {

                carslot1.setBackgroundColor(redColor);
                slot1Card.setClickable(false);
                GlobalVariables.slot1Status =true;

            } else {

                carslot1.setBackgroundColor(greenColor);
                slot1Card.setClickable(true);
                GlobalVariables.slot1Status =false;
            }

        }

    }

    private void updatingSlot2(){

        String slot2Book = slotData.getSlot2_book_Status();
        String slot2_Occ = slotData.getSlot2_Occ_Status();

        if (slot2Book.equals("Booked")) {

            carslot2.setBackgroundColor(redColor);
            slot2Card.setClickable(false);

            GlobalVariables.slot2Status =true;
        }
        if (slot2Book.equals("NotBooked")) {

            if (slot2_Occ.equals("0")) {

                carslot2.setBackgroundColor(redColor);
                slot2Card.setClickable(false);

                GlobalVariables.slot2Status =true;
            } else {

                carslot2.setBackgroundColor(greenColor);
                slot2Card.setClickable(true);

                GlobalVariables.slot2Status =false;
            }

        }

    }


    private void updatingSlot3(){

        String slot3Book = slotData.getSlot3_book_Status();
        String slot3_Occ = slotData.getSlot3_Occ_Status();

        if (slot3Book.equals("Booked")) {

            carslot3.setBackgroundColor(redColor);
            slot3Card.setClickable(false);

            GlobalVariables.slot3Status =true;

        }
        if (slot3Book.equals("NotBooked")) {

            if (slot3_Occ.equals("0")) {

                carslot3.setBackgroundColor(redColor);
                slot3Card.setClickable(false);

                GlobalVariables.slot3Status =true;

            } else {

                carslot3.setBackgroundColor(greenColor);
                slot3Card.setClickable(true);

                GlobalVariables.slot3Status =false;
            }
        }

    }






}