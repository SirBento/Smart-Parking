package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import zw.co.paynow.constants.MobileMoneyMethod;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;


public class Payment extends AppCompatActivity {

    private Button btnPayment;
    private EditText nameOfPayer, ecoNumber, plateNumber, parkingTime;
    //  private String error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        btnPayment =  findViewById(R.id.btnPayNow);
        nameOfPayer = findViewById(R.id.payDriverName);
        ecoNumber =  findViewById(R.id.payEcoNumber);
        plateNumber =  findViewById(R.id.payCarLicence);
        parkingTime = findViewById(R.id.payTimePeriod);


        // }
        btnPayment.setOnClickListener(view -> {

            //new activity will not run until the user enters valid information

            if(!validateName()
                    |  !validatePhoneNumber()
                    | !validateMinutes()
                    | !validateLicencePlate() ){

                return;
            }

            // storing values as a string ,those entered by the user
            String driverName = nameOfPayer.getText().toString().trim().toUpperCase();
            String paynum = ecoNumber.getText().toString().trim();
            String mins = parkingTime.getText().toString().trim();
            String numberplate = plateNumber.getText().toString().trim().toUpperCase();


            //  opening payment description activity  and attaching values to it
            Intent intent = new Intent(Payment.this, PaymentDescription.class);
            intent.putExtra("keyname", driverName);
            intent.putExtra("keyEcoNumber", paynum);
            intent.putExtra("keymins", mins);
            intent.putExtra("keyplate", numberplate);
            startActivity(intent);
            finish();

        });


    }


    // validating the information entered by the user

    private boolean validateName() {

        String driverName = nameOfPayer.getText().toString().trim();

        if (driverName.isEmpty()) {
            nameOfPayer.setError("Your name is required");
            nameOfPayer.requestFocus();
            return false;
        }else if (driverName.length() <=3){
            nameOfPayer.setError("Your name is invalid");
            nameOfPayer.requestFocus();
            return false;

        }else {
            nameOfPayer.setError(null);
            return true;

        }

    }

    private boolean validateLicencePlate(){

        String licencePlate = plateNumber.getText().toString().trim();

        if (!licencePlate.isEmpty()) {

            char platechar1 = plateNumber.getText().charAt(0);
            char platechar2 = plateNumber.getText().charAt(1);
            char platechar3 = plateNumber.getText().charAt(2);

        if (!Character.isAlphabetic(platechar1) || !Character.isAlphabetic(platechar2) || !Character.isAlphabetic(platechar3)) {
                plateNumber.setError("A valid Plate number required");
                plateNumber.requestFocus();
                return false;
        } else if (!Character.isAlphabetic(platechar1) && !Character.isAlphabetic(platechar2) && !Character.isAlphabetic(platechar3)){
            //check if first 3 characters of the licence plate are characters
            plateNumber.setError("A valid Plate number required");
            plateNumber.requestFocus();
            return false;

        }else if ( licencePlate.length() < 7 || licencePlate.length() >7) {
            plateNumber.setError("Enter a valid licence plate");
            plateNumber.requestFocus();
            return false;

        } else {
            plateNumber.setError(null);
            return true;

        }
        } else if (licencePlate.isEmpty()) {
            plateNumber.setError("Licence plate number is required");
            plateNumber.requestFocus();
            return false;

        } else {
            plateNumber.setError(null);
            return true;

        }
    }

    private boolean validatePhoneNumber(){

        String paynum = ecoNumber.getText().toString().trim();

        if (paynum.isEmpty()) {
            ecoNumber.setError("Ecocash Number is required");
            ecoNumber.requestFocus();
            return false;
        }else if (paynum.length() > 10 || paynum.length() < 10) {
            ecoNumber.setError("Enter a valid number");
            ecoNumber.requestFocus();
            return false ;
        } else {
            ecoNumber.setError(null);
            return true;

        }
    }

    private boolean validateMinutes(){
        String mins = parkingTime.getText().toString().trim();
        int numMins = 0;
        if(!mins.equals("")){
            numMins = Integer.parseInt(mins);
        }else{
            numMins=0;
        }

        if (mins.isEmpty()) {
            parkingTime.setError("Parking Minutes required");
            parkingTime.requestFocus();
            return false;

        } else if (numMins<=0) {
            parkingTime.setError("Enter Valid Minutes ");
            parkingTime.requestFocus();
            return false;

        } else if (mins.length() > 4) {
            parkingTime.setError("Enter Valid Minutes ");
            parkingTime.requestFocus();
            return false;

        }else{
            parkingTime.setError(null);
             return  true;

        }

    }


}