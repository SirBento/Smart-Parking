package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class BookingDone extends AppCompatActivity {
    private Button DoneBooking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_done);

        DoneBooking= findViewById(R.id.BookingDoneButton);
        DoneBooking.setOnClickListener(v -> {

            Intent intent = new Intent( BookingDone.this, MinutesAfterBooking.class);
            startActivity(intent);
            finish();

        });

    }
}