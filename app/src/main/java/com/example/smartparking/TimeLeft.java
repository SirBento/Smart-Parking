package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.concurrent.TimeUnit;


public class TimeLeft extends AppCompatActivity {


    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("CurrentTicket");
    private Button TopUp;
    private static final String FORMAT = "%02d:%02d:%02d";
    private TextView driverName, regNo , ticketDate , Duration , countDownTextview,ticketMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_left);

        Intent intent = new Intent(this, BroadcastService.class);
       //startService(intent);


        countDownTextview = findViewById(R.id.PayCountdown);
        driverName = findViewById(R.id.userPayName);
        regNo = findViewById(R.id.userPayReg);
        ticketDate = findViewById(R.id.userPayDate);
        Duration= findViewById(R.id.userPayMinutes);
        ticketMessage = findViewById(R.id.ticketMessage);
        TopUp = findViewById(R.id.btnTopUp);
        ticketMessage.setVisibility(View.INVISIBLE);


        // if the ticket matches the current user
        if(userUid.equals(GlobalVariables.Uid)) {

            driverName.setText("Name: "+ GlobalVariables.ticketname);
            regNo.setText("RegNo: "+ GlobalVariables.ticketregNo);
            ticketDate.setText("Date: "+GlobalVariables.ticketDate);
            Duration.setText("Mins: " + GlobalVariables.ticketDuration);
            startService(intent);

        }else{

            driverName.setText("Name: Null");
            regNo.setText("RegNo: Null");
            ticketDate.setText("Date: Null");
            Duration.setText("Mins: Null");
            GlobalVariables.userMillisec = 0;
            ticketMessage.setVisibility(View.VISIBLE);
            countDownTextview.setText("00:00");
        }

   // If there is no current ticket  then set the data to default
        if(!GlobalVariables.tucketDatExist){

            driverName.setText("Name: Null");
            regNo.setText("RegNo: Null");
            ticketDate.setText("Date: Null");
            Duration.setText("Mins: Null");
            GlobalVariables.userMillisec = 0;
            ticketMessage.setVisibility(View.VISIBLE);
            countDownTextview.setText("00:00");

        }




        TopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!GlobalVariables.tucketDatExist){

                    Toast.makeText(TimeLeft.this, "No ticket running, hence you can't make a TopUp", Toast.LENGTH_LONG).show();

                }else{

                    startActivity(new Intent( TimeLeft.this,TopUp.class));
                    finish(); //remove
                }

            }
        });




    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Update GUI
            updateGUI(intent);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,BroadcastService.class));
        super.onDestroy();
    }

  private void updateGUI(Intent intent){

        if(intent.getExtras() != null){

            long millisUntilFinished = intent.getLongExtra("countdown",0);

            countDownTextview.setText(""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            String timeValue = countDownTextview.getText().toString();

            if(timeValue.equals("00:00:01")){

                terminateTicket();
            }

        }else{

            //Terminate ticket as soon as the count down reaches zero
            terminateTicket();
        }

    }



    @Override
    protected void onStop() {

        try {

            unregisterReceiver(broadcastReceiver);

        }catch (Exception e){

            // broadcast receiver was probably already unregistered
        }

        //change the user milliseconds to the current one left
       // GlobalVariables.userMillisec = currentTimeLeft;

        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }



    // A function to notifying the user when the time elapsed.

    private void sendNotification(){

        //creating an intent that is called when the notification is clicked
        Intent resultIntent = new Intent(this,TimeLeft.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder  = new NotificationCompat.Builder(TimeLeft.this,"My Notification");
        builder.setContentTitle("Smart Parking");
        builder.setContentText("Hello! Your Time is Up. Please move your car or TopUp");
        builder.setSmallIcon(R.drawable.announcement);
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(TimeLeft.this);
        managerCompat.notify(1,builder.build());


    }

    private void NotifyChannelOreo(){
        //for oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new  NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);


        }
    }

//working not reset the text view after this has been called.
    private void terminateTicket(){

     //if the current user id matches the uid on the ticket, it gets deleted

      if(userUid.equals(GlobalVariables.Uid)){

          //Deleting the booking ticket soon as the time is up
         databaseRef.removeValue();

            driverName.setText("Name: Null");
            regNo.setText("RegNo: Null");
            ticketDate.setText("Date: Null");
            Duration.setText("Mins: Null");
            ticketMessage.setVisibility(View.VISIBLE);
            countDownTextview.setText("00:00");
       }

    }






    }
