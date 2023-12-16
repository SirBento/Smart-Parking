package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TopUp extends AppCompatActivity {

    private Button TopUpPay,TopUpCancel;
    private EditText topUpmins,topUpeconum;
     private String mins,econum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        TopUpPay = findViewById(R.id.btnTopUpPay);
        TopUpCancel = findViewById(R.id.btnTopUpCancel);
        topUpmins = findViewById(R.id.TopUPMins);
        topUpeconum = findViewById(R.id.TopUPNum);



        TopUpPay.setOnClickListener(v -> {

            if(!validateMinutes() |  !validatePhoneNumber()){

                return;
            }
            mins = topUpmins.getText().toString();
            econum = topUpeconum.getText().toString();

            //  opening payment description activity  and attaching values to it
            Intent intent = new Intent(TopUp.this, TopUpDescription.class);
            intent.putExtra("keymins", mins);
            intent.putExtra("keynum", econum);
            startActivity(intent);
            finish();


        });


        TopUpCancel.setOnClickListener(v -> {

            startActivity(new Intent( TopUp.this,TimeLeft.class));
            finish();
        });



    }


    private boolean validatePhoneNumber(){

        String paynum = topUpeconum.getText().toString().trim();

        if (paynum.isEmpty()) {
            topUpeconum.setError("Ecocash Number is required");
            topUpeconum.requestFocus();
            return false;
        }else if (paynum.length() > 10 || paynum.length() < 10) {
            topUpeconum.setError("Enter a valid number");
            topUpeconum.requestFocus();
            return false ;
        } else {
            topUpeconum.setError(null);
            return true;

        }
    }

    private boolean validateMinutes(){

        String mins = topUpmins.getText().toString().trim();

        if (mins.isEmpty()) {
            topUpmins.setError("Parking Minutes required");
            topUpmins.requestFocus();
            return false;

        } else if (mins.equals("0")) {
            topUpmins.setError("Enter Valid Minutes ");
            topUpmins.requestFocus();
            return false;

        } else if (mins.length() > 4) {
            topUpmins.setError("Enter Valid Minutes ");
            topUpmins.requestFocus();
            return false;

        }else{
            topUpmins.setError(null);
            return  true;

        }

    }

}