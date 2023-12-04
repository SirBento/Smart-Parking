package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SlotBooking extends AppCompatActivity {

    private EditText BnameOfPayer, BecoNumber, BplateNumber, BparkingTime, Bcartype;
    private Button book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking);


        BnameOfPayer = findViewById(R.id.BookingDriverName);
        BecoNumber =findViewById(R.id.BookingEcoNumber);
        BplateNumber = findViewById(R.id.BookingCarLicence);
        BparkingTime = findViewById(R.id.BookingTimePeriod);
        Bcartype = findViewById(R.id.BookingCarType);
        book =findViewById(R.id.btnBookNow);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validateName() |  !validatePhoneNumber() | !validateMinutes() | !validateLicencePlate() | !validateCarType() ){

                    return;
                }
                openNewIntent();

            }
        });


    }



    private void openNewIntent(){

        // storing values as a string ,those entered by the user
        String driverName = BnameOfPayer.getText().toString().trim().toUpperCase();
        String paynum = BecoNumber.getText().toString().trim();
        String mins =  BparkingTime.getText().toString().trim();
        String numberplate = BplateNumber.getText().toString().trim().toUpperCase();
        String slotvalue = getIntent().getStringExtra("keyname");

        // passing user entered values along with the intent of opening the booking description
        Intent intent = new Intent( SlotBooking.this,BookingDescription.class);
        intent.putExtra("keyname",driverName);
        intent.putExtra("keyEcoNumber",paynum);
        intent.putExtra("keymins",mins);
        intent.putExtra("keyplate",numberplate);
        intent.putExtra("keyslotval", slotvalue);
        startActivity(intent);
        finish();
    }

    private boolean validateName(){
        // Get entered name, trim to remove extra white space
        String driverName = BnameOfPayer.getText().toString().trim();

        //checking if the name text box is filled or not
        if(driverName.isEmpty()){
            BnameOfPayer.setError("Your name is required");
            BnameOfPayer.requestFocus();
            return false ;

        //checking if the name entered has a valid length
        } else if (driverName.length() <= 3) {
            BnameOfPayer.setError("Your name is invalid");
            BnameOfPayer.requestFocus();
            return false;

        } else {
            BnameOfPayer.setError(null);
            return true;

        }
    }

    private boolean validatePhoneNumber(){

        // Get entered phone number, trim to remove extra white space
        String paynum = BecoNumber.getText().toString().trim();

        // checking if the phone number field is empty or not
        if (paynum.isEmpty()) {

            BecoNumber.setError("Ecocash Number is required");
            BecoNumber.requestFocus();
            return false;
            //checking the length and validity of the phone number
        }else if (paynum.length() > 10 || paynum.length() < 10) {

            BecoNumber.setError("Enter a valid number");
            BecoNumber.requestFocus();
            return false ;

        } else {
            BecoNumber.setError(null);
            return true;

        }

    }
    private boolean validateLicencePlate(){

        // Get entered plate number, trim to remove extra white space
        String licencePlate = BplateNumber.getText().toString().trim();

        if (!licencePlate.isEmpty()) {

            char platechar1 = BplateNumber.getText().charAt(0);
            char platechar2 = BplateNumber.getText().charAt(1);
            char platechar3 = BplateNumber.getText().charAt(2);

            //checking if the first characters  of the entered text are letters
            if (!Character.isAlphabetic(platechar1)
                    || !Character.isAlphabetic(platechar2) || !Character.isAlphabetic(platechar3)) {
                BplateNumber.setError("A valid Plate number required");
                BplateNumber.requestFocus();
                return false;
            } else if (!Character.isAlphabetic(platechar1)
                    && !Character.isAlphabetic(platechar2) && !Character.isAlphabetic(platechar3)){
                //check if first 3 characters of the licence plate are characters
                BplateNumber.setError("A valid Plate number required");
                BplateNumber.requestFocus();
                return false;

                //checking if the entered plate number has the correct length
            }else if ( licencePlate.length() < 7 || licencePlate.length() >7) {
                BplateNumber.setError("Enter a valid licence plate");
                BplateNumber.requestFocus();
                return false;

            } else {
                BplateNumber.setError(null);
                return true;

            }
            // checking if the plate text field is empty or not
        } else if (licencePlate.isEmpty()) {
            BplateNumber.setError("Licence plate number is required");
            BplateNumber.requestFocus();
            return false;

        } else {
            BplateNumber.setError(null);
            return true;

        }

    }

    private boolean validateMinutes(){

        // Get entered number of minutes, trim to remove extra white space
        String mins = BparkingTime.getText().toString().trim();
        int numMins = 0;
        if(!mins.equals("")){
            numMins = Integer.parseInt(mins);
        }else{
            numMins=0;
        }

        // checking if the minutes field is empty or not
        if (mins.isEmpty()) {
            BparkingTime.setError("Parking Minutes required");
            BparkingTime.requestFocus();
            return false;

        // checking if the minutes entered are not zero or below is empty or not
        }else if(numMins<=0){
            BparkingTime.setError("Enter Valid Minutes ");
            BparkingTime.requestFocus();
            return false;

        // checking if the minutes entered are within the allowed range
        } else if (mins.length() > 4) {
            BparkingTime.setError("Enter Valid Minutes ");
            BparkingTime.requestFocus();
            return false;

        }else{
            BparkingTime.setError(null);
            return  true;

        }
    }



    private boolean validateCarType(){

        // Get entered car type, trim to remove extra white space
        String carType = Bcartype.getText().toString().trim();

        // checking if the car type field is empty or not
        if(carType.isEmpty()){

            Bcartype.setError("Your car type is required");
            Bcartype.requestFocus();
            return false;
        // checking if the entered type is valid
        }else if (carType.length() < 3) {

            Bcartype.setError("Your name is invalid");
            Bcartype.requestFocus();
            return false;

        } else {
            Bcartype.setError(null);
            return true;

        }

    }
}