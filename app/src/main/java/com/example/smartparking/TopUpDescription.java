package com.example.smartparking;

import static com.example.smartparking.BroadcastService.countDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import zw.co.paynow.constants.MobileMoneyMethod;
import zw.co.paynow.core.Payment;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;

public class TopUpDescription extends AppCompatActivity {

    private double topUpamounttobepaid;
    String paynum, mins, username,CurrentDuration, Uid, ticketKey,payNowPaymentException,paymentErrorException, CurrentTime;
    private int userduration;
    private boolean changeTime;
    private long lastMins;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("CurrentTicket");
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("TopUpPayments");
    String currentUserUid =FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference slotpaymentRef = FirebaseDatabase.getInstance().getReference().child("SlotPaymentErrors");
    private DatabaseReference slotPayNowRef = FirebaseDatabase.getInstance().getReference().child("SlotPayNowErrors");
    LoadingDialog loadingDialog = new LoadingDialog(TopUpDescription.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_description);

        //getting values entered by the user
        paynum = getIntent().getStringExtra("keynum");
        mins = getIntent().getStringExtra("keymins");
        username = GlobalVariables.ticketname;

        //calculating the amount to be paid as per 400 rtgs per hour

        topUpamounttobepaid = (Integer.parseInt(mins)* 6.7);

        CurrentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        Button topUpPaymentbtn = findViewById(R.id.btnTopUpPayment);
        Button topUpCancelbtn = findViewById(R.id.btTopUpncancel);
        TextView name = findViewById(R.id.TopUpNameOfPayer);
        TextView number = findViewById(R.id.TopUpEcoNumber);
        TextView time = findViewById(R.id.TopUpMinutesOfParking);
        TextView amount = findViewById(R.id.TopUpAmountToBePaid);

        //get the previous minutes from the database
        getMinutesValue();

        name.setText("Name: " + username);
        number.setText("Ecocash Number: " + paynum);
        time.setText("Parking Time In Minutes: " + mins);
        amount.setText("Amount To Be Paid: " +  topUpamounttobepaid + "RTGS");



        topUpPaymentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(changeTime){

                    //process payment
                    loadingDialog.startLoadingDialog();
                    payment();

                }else{

                    Toast.makeText(TopUpDescription.this, "Sorry!, we are unable to process your TopUp, please use our payment feature...", Toast.LENGTH_LONG).show();

                }


            }
        });


        topUpCancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( TopUpDescription.this,TopUp.class));
                finish();
            }
        });

    }


    private void getMinutesValue(){

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                   // changeTime = true;

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                       CurrentDuration = dataSnapshot.child("Duration").getValue(String.class);
                       Uid = dataSnapshot.child("Uid").getValue(String.class);
                       ticketKey = dataSnapshot.getKey();
                       GlobalVariables.uniqueTicketKey = ticketKey;


                        // if the current user id matches the one on the ticket the update the time
                       if(Uid.equals(currentUserUid)){

                             //check why it is taking long to change
                           changeTime = true;
                        }
                    }


                    userduration = Integer.parseInt(String.valueOf(mins))+  Integer.parseInt(String.valueOf(CurrentDuration));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void payment(){

            try {

                // pay now instance

                Paynow paynow = new Paynow("14906", "7d3605e6-effe-46db-a952-1746f431a2ab");

                paynow.setResultUrl("http://example.com/gateways/paynow/update");
                paynow.setReturnUrl("http://example.com/return?gateway=paynow&merchantReference=1234");

                Payment payment = paynow.createPayment("Invoice 1","bernardtowindo38@gmail.com");
                payment.setCartDescription("Smart Parking Fee Payment");

                //payment.setCart("Parking Fee",amounttobepaid); hashmap, big decimal

                payment.add("Parking Fee", topUpamounttobepaid );

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
                    Toast.makeText(TopUpDescription.this, "Pay now", Toast.LENGTH_LONG).show();



                    //delay checking the payment status by 15 seconds
                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // Get the poll URL of the transaction
                            String pollUrl = response.pollUrl();
                            StatusResponse status = paynow.pollTransaction(pollUrl);

                            if (status.isPaid()) {

                                //update the user current ticket
                                HashMap CurrentTicket = new HashMap<>();
                                CurrentTicket.put("Duration", String.valueOf(userduration));
                                CurrentTicket.put("Time", String.valueOf(CurrentTime));
                                databaseRef.getRef().child(GlobalVariables.uniqueTicketKey).updateChildren(CurrentTicket);

                                // storing the current milliseconds before topup
                                lastMins = GlobalVariables.userMillisec;
                                countDownTimer.cancel();

                                // storing the  milliseconds after topup
                                GlobalVariables.topUpMillisec = lastMins +  (Integer.parseInt(mins)* 60000);
                                GlobalVariables.topup = true;

                                saveTopUpdetails();
                                loadingDialog.dismissDialog();
                                Toast.makeText(TopUpDescription.this, "Thank you for paying", Toast.LENGTH_LONG).show();

                                startActivity(new Intent( TopUpDescription.this, TimeLeft.class) );
                                finish();

                            } else {

                                // print "Why you no pay?";
                                loadingDialog.dismissDialog();
                                Toast.makeText(TopUpDescription.this, "Payment Failed!!! Try Again", Toast.LENGTH_LONG).show();
                            }

                        }
                    }, 20000); // dismiss the dialog after 15 seconds


                } else {

                    paymentErrorException = response.errors().toString();
                    paymentErrors();
                    loadingDialog.dismissDialog();
                    Toast.makeText(TopUpDescription.this,"Something Wrong Happened!!!, Please Check Your Connection" , Toast.LENGTH_LONG).show();
                }

            }catch(Exception e){

                payNowPaymentException =e.getMessage();
                PayNowErrors();

                Toast.makeText(this,"Something Wrong Happened!!!, Please Check Your Connection", Toast.LENGTH_LONG).show();
            }

        }



    // logging any errors that may occur during payment
    private void paymentErrors(){

        HashMap<String, String> ErrorMap = new HashMap<>();

        ErrorMap.put("Error", paymentErrorException);
        ErrorMap.put("Time", CurrentTime );
        slotpaymentRef.push().setValue(ErrorMap);

    }

    // logging any errors that may occur during payment that could be coming from the paynow
    private void PayNowErrors(){

        HashMap<String, String> PayNowErrorMap = new HashMap<>();
        PayNowErrorMap.put("Error", payNowPaymentException);
        PayNowErrorMap.put("Time", CurrentTime );
        slotPayNowRef.push().setValue(PayNowErrorMap);

    }

    private void saveTopUpdetails(){

        HashMap<String, String> paymentsDone = new HashMap<>();

        paymentsDone.put("Driver", username );
        paymentsDone.put("Phone_No", paynum);
        paymentsDone.put("Amount Paid",String.valueOf(topUpamounttobepaid));
        paymentsDone.put("AddedMinutes", mins);
        paymentsDone.put("Time", CurrentTime);

        mRef.push().setValue(paymentsDone);
    }

}
