package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BookingDone extends AppCompatActivity {

    private Button DoneBooking;
    private String mins, bookedSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_done);

        DoneBooking= (Button)findViewById(R.id.BookingDoneButton);



        DoneBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( BookingDone.this, MinutesAfterBooking.class);
                startActivity(intent);
                finish();

            }
        });

    }
}