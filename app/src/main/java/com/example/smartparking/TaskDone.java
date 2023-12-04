package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TaskDone extends AppCompatActivity {

    private Button  DonePayment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_done);

        DonePayment= (Button)findViewById(R.id.BookingDoneButton);

        DonePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( TaskDone.this, TimeLeft.class);
                startActivity(intent);
                finish();
            }
        });

    }
}