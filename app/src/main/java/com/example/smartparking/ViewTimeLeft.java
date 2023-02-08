package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewTimeLeft extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_time_left);

       Button ViewBookingTicket = findViewById(R.id.btnViewBookingTicket);
       Button ViewCurrentTicket = findViewById(R.id.btnViewCurrentTicket );


        ViewBookingTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( ViewTimeLeft.this,MinutesAfterBooking.class));
            }
        });

        ViewCurrentTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( ViewTimeLeft.this,TimeLeft.class));
            }
        });


    }
}