package com.example.smartparking;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import zw.co.paynow.constants.MobileMoneyMethod;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;
import zw.co.paynow.core.Payment;



public class PaymentDescription extends AppCompatActivity {


    private Button paymentbtn, cancelbtn;
    private TextView name,number,time, amount;
    private Double amounttobepaid;
    private String driverName,paynum,mins,numberPlate,payNowPaymentException,paymentErrorException, CurrentTime,currentDate;
    private String TimeDuringPayment = new SimpleDateFormat("hh:mm a",Locale.getDefault()).format(new Date());
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = mDatabase.getReference().child("ParkingPayments");
    private DatabaseReference tickect = mDatabase.getReference().child("CurrentTicket");
    private DatabaseReference slotpaymentRef = mDatabase.getReference().child("SlotPaymentErrors");
    private DatabaseReference slotPayNowRef = mDatabase.getReference().child("SlotPayNowErrors");
    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    LoadingDialog loadingDialog = new LoadingDialog(PaymentDescription.this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_description);

                paymentbtn =  findViewById(R.id.btnPayment);
                cancelbtn =  findViewById(R.id.btncancel);
                name = findViewById(R.id.nameofpayer);
                number = findViewById(R.id.econumber);
                time = findViewById(R.id.minutesOfParking);
                amount = findViewById(R.id.amountToBePaid);

        //getting values from the previous activity

         driverName = getIntent().getStringExtra("keyname");
         paynum= getIntent().getStringExtra("keyEcoNumber");
         mins = getIntent().getStringExtra("keymins");
         numberPlate = getIntent().getStringExtra("keyplate");



        //making the number of minutes public so that it can be used in the time left activity.


        //calculating the amount to be paid
        // calculate the amount to be paid 67(bond per 10 mins)actual fee (400 per hour)
        // 6.7 rtgs per minute
        amounttobepaid =  (double)(Integer.parseInt(mins)* 6.7);


        name.setText("Name: " + driverName);
        number.setText("Ecocash Number: " + paynum);
        time.setText("Parking Time In Minutes: " + mins);
        amount.setText("Amount To Be Paid: " + amounttobepaid + " RTGS");

        CurrentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        currentDate =  new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());

       //CreateCurrentTicket(); //to be removed

        paymentbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        loadingDialog.startLoadingDialog();
                        Paying();

                    }

                });

                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        startActivity(new Intent( PaymentDescription.this, com.example.smartparking.Payment.class) );
                        finish();
                    }
                });


    }

    private void Paying(){

        try {

            // pay now instance

            Paynow paynow = new Paynow("14906", "7d3605e6-effe-46db-a952-1746f431a2ab");

            paynow.setResultUrl("http://example.com/gateways/paynow/update");
            paynow.setReturnUrl("http://example.com/return?gateway=paynow&merchantReference=1234");

            Payment payment = paynow.createPayment("Invoice 1","bernardtowindo38@gmail.com");
            payment.setCartDescription("Smart Parking Fee Payment");

            //payment.setCart("Parking Fee",amounttobepaid); hashmap, big decimal

            payment.add("Parking Fee", amounttobepaid );

            //For Success testing
            //MobileInitResponse response = paynow.sendMobile(payment, "0771111111", MobileMoneyMethod.ECOCASH);

            //For Canceling testing 0773333333
            //MobileInitResponse response = paynow.sendMobile(payment, "0773333333", MobileMoneyMethod.ECOCASH);

            //For delayed success 0772222222
            //MobileInitResponse response = paynow.sendMobile(payment, "0772222222", MobileMoneyMethod.ECOCASH);

            MobileInitResponse response = paynow.sendMobile(payment, paynum, MobileMoneyMethod.ECOCASH);

            if (response.success()) {

                // Get the instructions to show to the user

                String instructions  = response.instructions();
                Toast.makeText(PaymentDescription.this, "Pay now", Toast.LENGTH_LONG).show();

                
            //delay checking the payment status by 15 seconds

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // Get the poll URL of the transaction
                        String pollUrl = response.pollUrl();
                        StatusResponse status = paynow.pollTransaction(pollUrl);

                        if (status.isPaid()) {

                             CreateCurrentTicket();
                             savePaymentdetails();
                             loadingDialog.dismissDialog();

                             Toast.makeText(PaymentDescription.this, "Thank you for paying", Toast.LENGTH_LONG).show();

                             startActivity(new Intent( PaymentDescription.this, TimeLeft.class) );
                             finish();

                        } else {

                            // print "Why you no pay?";
                            loadingDialog.dismissDialog();
                            Toast.makeText(PaymentDescription.this, "Payment Failed!!! Try Again", Toast.LENGTH_LONG).show();
                        }

                    }
                }, 15000); // dismiss the dialog after 15 seconds


            } else {

                paymentErrorException = response.errors().toString();
                paymentErrors();
                loadingDialog.dismissDialog();
                Toast.makeText(PaymentDescription.this,"Failed!!, Please Check Your Connection" , Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
          //  Log.i("", String.valueOf(e));
            payNowPaymentException =e.getMessage();
            PayNowErrors();
            Toast.makeText(this,"Something Wrong Happened!!!, Please Check Your Connection", Toast.LENGTH_LONG).show();
            loadingDialog.dismissDialog();
        }

    }

    // saving the details of the payment made
    private void savePaymentdetails(){

        HashMap<String, String> paymentsDone = new HashMap<>();

        paymentsDone.put("Driver", driverName );
        paymentsDone.put("Phone_No", paynum);
        paymentsDone.put("Reg_No", numberPlate);
        paymentsDone.put("Amount Paid",String.valueOf(amounttobepaid));
        paymentsDone.put("Duration", mins);
        paymentsDone.put("Time", TimeDuringPayment);
        paymentsDone.put("Uid",userUid );

        mRef.push().setValue(paymentsDone);

        Intent intent = new Intent( PaymentDescription.this,TaskDone.class);
        intent.putExtra("keymins",mins);
        startActivity(intent);
        finish();

    }

    // creating a ticket after the payment has been made
    private void CreateCurrentTicket(){

        HashMap CurrentTicket = new HashMap<>();

        CurrentTicket.put("Driver", driverName );
        CurrentTicket.put("Reg_No", numberPlate);
        CurrentTicket.put("Date", currentDate);
        CurrentTicket.put("Time", TimeDuringPayment);
        CurrentTicket.put("Duration", mins);
        CurrentTicket.put("Uid", userUid );

        tickect.push().setValue(CurrentTicket);
        //tickect.updateChildren(CurrentTicket);

    }

    // logging any errors that may occur during payment
    private void paymentErrors(){

        HashMap<String, String> ErrorMap = new HashMap<>();

        ErrorMap.put("Error", paymentErrorException);
        ErrorMap.put("Time", CurrentTime );
        ErrorMap.put("Date", currentDate );
        slotpaymentRef.push().setValue(ErrorMap);

    }

    // logging any errors that may occur during payment that could be coming from the paynow
    private void PayNowErrors(){

        HashMap<String, String> PayNowErrorMap = new HashMap<>();
        PayNowErrorMap.put("Error", payNowPaymentException);
        PayNowErrorMap.put("Time", CurrentTime );
        PayNowErrorMap.put("Date", currentDate );
        slotPayNowRef.push().setValue(PayNowErrorMap);

    }


}