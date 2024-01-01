package com.example.smartparking;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import zw.co.paynow.core.Payment;
import zw.co.paynow.core.Paynow;
import zw.co.paynow.responses.MobileInitResponse;
import zw.co.paynow.responses.StatusResponse;

public class BookingDescription extends AppCompatActivity {
    private double bookamounttobepaid;
    String driverName,paynum, mins,numberPlate,date,CurrenTime,slotVal,paymentException,payNowException;
    //Firebase instances
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = mDatabase.getReference().child("Bookings");
    private DatabaseReference slotRef = mDatabase.getReference().child("Reservations");
    private DatabaseReference paymentRef = mDatabase.getReference().child("BookingPaymentErrors");
    private DatabaseReference PayNowRef = mDatabase.getReference().child("BookingPayNowErrors");
    DatabaseReference databaseRef = mDatabase .getReference("BookingTicket");
    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    LoadingDialog loadingDialog = new LoadingDialog(BookingDescription.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_description);

        Button bookpaymentbtn = findViewById(R.id.btnPayment);
        Button bookcancelbtn = findViewById(R.id.btncancel);
        TextView name = findViewById(R.id.booknameofpayer);
        TextView number = findViewById(R.id.booknumber);
        TextView time = findViewById(R.id.bookminutesOfParking);
        TextView amount = findViewById(R.id.bookamountToBePaid);

        //getting values from the previous activity

          driverName = getIntent().getStringExtra("keyname");
          paynum = getIntent().getStringExtra("keyEcoNumber");
          mins = getIntent().getStringExtra("keymins");
          numberPlate = getIntent().getStringExtra("keyplate");
          slotVal = getIntent().getStringExtra("keyslotval");

          date =  new SimpleDateFormat("dd/LLL/yyyy", Locale.getDefault()).format(new Date());
          CurrenTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        //calculating the amount to be paid as per 400 rtgs per hour
        bookamounttobepaid = (Integer.parseInt(mins)* 6.7);

        //adding 10% of the amount to be paid as booking fee
        bookamounttobepaid = bookamounttobepaid + (bookamounttobepaid*0.1);

        name.setText("Name: " + driverName);
        number.setText("Ecocash Number: " + paynum);
        time.setText("Parking Time In Minutes: " + mins);
        amount.setText("Amount To Be Paid: " +(int) bookamounttobepaid + "RTGS");
        GlobalVariables.bookregNo = numberPlate;

   // createBookingTicket(); //
        bookpaymentbtn.setOnClickListener(view -> {
            loadingDialog.startLoadingDialog();
            BookingPayment();});

        bookcancelbtn.setOnClickListener(view -> {

            startActivity(new Intent( BookingDescription.this,SlotBooking.class) );
            finish();
        });

    }



    private void BookingPayment(){

        try {

            // pay now instance
            Paynow paynow = new Paynow("14906", "7d3605e6-effe-46db-a952-1746f431a2ab");

            paynow.setResultUrl("http://example.com/gateways/paynow/update");
            paynow.setReturnUrl("http://example.com/return?gateway=paynow&merchantReference=1234");

            Payment payment = paynow.createPayment("Invoice 1","bernardtowindo38@gmail.com");

            // payment description
            payment.setCartDescription("Smart Parking Booking Fee");

            //payment.setCartDescription("Smart Parking Fee Payment");
            //payment.setCart("Parking Fee",amounttobepaid); hashmap, big decimal
            payment.add("Booking Fee", bookamounttobepaid );

            MobileInitResponse response = paynow.sendMobile(payment, paynum, MobileMoneyMethod.ECOCASH);



            if (response.success()) {

                // Get the instructions to show to the user
                String instructions  = response.instructions();
                Toast.makeText(BookingDescription.this, "Pay now", Toast.LENGTH_LONG).show();

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        String pollUrl = response.pollUrl();
                        StatusResponse status = paynow.pollTransaction(pollUrl);

                        if (status.isPaid()) {

                            saveData();
                            createBookingTicket();
                            Toast.makeText(BookingDescription.this, "Thank you for paying", Toast.LENGTH_LONG).show();
                            loadingDialog.dismissDialog();
                            startActivity(new Intent( BookingDescription.this, BookingDone.class) );
                            finish();

                        } else {

                            loadingDialog.dismissDialog();
                            Toast.makeText(BookingDescription.this, "Something wrong happened!!! Payment Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                }, 20000); // dismiss the dialog after 7 seconds


            } else {
                payNowException = response.errors().toString();
                PayNowErrors();
                loadingDialog.dismissDialog();
                Toast.makeText(BookingDescription.this, "Something Wrong Happened!!! Check Your Connection.", Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){

            paymentException = e.getMessage();
            paymentErrors();
            loadingDialog.dismissDialog();
            Toast.makeText(this, "Something Wrong Happened!! Please Check your Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    private void saveData(){

        HashMap<String, String> bookingMap = new HashMap<>();

        bookingMap.put("Driver", driverName );
        bookingMap.put("Phone_No", paynum);
        bookingMap.put("Reg_No", numberPlate);
        bookingMap.put("Duration", mins);
        bookingMap.put("Fee", String.valueOf(bookamounttobepaid));
        bookingMap.put("Slot",slotVal);
        bookingMap.put("Date", date);
        bookingMap.put("Time", CurrenTime);
        bookingMap.put("Uid",userUid );

        mRef.push().setValue(bookingMap);



        if(slotVal.equals("1")){

            HashMap Slot1booking = new HashMap<>();
            Slot1booking.put("Slot1", "Booked" );
            slotRef.updateChildren(Slot1booking);


        }else if(slotVal.equals("2")){

            HashMap Slot2booking = new HashMap<>();
            Slot2booking.put("Slot2", "Booked" );
            slotRef.updateChildren(Slot2booking);


        }else if(slotVal.equals("3")){

            HashMap Slot3booking = new HashMap<>();
            Slot3booking.put("Slot3", "Booked" );
            slotRef.updateChildren(Slot3booking);

        }


        Intent intent = new Intent( BookingDescription.this,BookingDone.class);
       // intent.putExtra("keymins",mins);
        //intent.putExtra("keybookedslot",slotVal);
        startActivity(intent);
        finish();

    }


    private void paymentErrors(){

        HashMap<String, String> ErrorMap = new HashMap<>();


        ErrorMap.put("Error", paymentException);
        ErrorMap.put("Time", CurrenTime );
        paymentRef.push().setValue(ErrorMap);

    }


    private void PayNowErrors(){

        HashMap<String, String> PayNowErrorMap = new HashMap<>();


        PayNowErrorMap.put("Error", payNowException);
        PayNowErrorMap.put("Time", CurrenTime );
        PayNowRef.push().setValue(PayNowErrorMap);

    }


    private void createBookingTicket(){

        HashMap<String, String> bookingTicketMap = new HashMap<>();

        bookingTicketMap.put("Name", driverName);
        bookingTicketMap.put("Date", date);
        bookingTicketMap.put("Slot", slotVal);
        bookingTicketMap.put("Reg_No", numberPlate);
        bookingTicketMap.put("Time", CurrenTime);
        bookingTicketMap.put("Duration", mins);
        bookingTicketMap.put("Uid", userUid );
        databaseRef.push().setValue(bookingTicketMap);
    }


}