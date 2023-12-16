package com.example.smartparking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BroadcastService extends Service {
    public  static  final String COUNTDOWN_BR ="com.example.smartparking";
    Intent intent = new Intent(COUNTDOWN_BR);
    public  static  CountDownTimer countDownTimer = null;
    public static long userTimeInSeconds;
    private boolean isRunning;
    DatabaseReference databaseRef;

    @Override
    public void onCreate() {
        super.onCreate();

        databaseRef = FirebaseDatabase.getInstance().getReference("CurrentTicket");

        //check if the timer is being started after topUp or just from  slot payment
        if(GlobalVariables.topup){

            userTimeInSeconds = GlobalVariables.topUpMillisec;

        }else{

            userTimeInSeconds = GlobalVariables.userMillisec;
        }


    if (userTimeInSeconds >1000) {
        if (!isRunning) {

            //start
            countDownTimer = new CountDownTimer(userTimeInSeconds, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {

                    Log.i("BroadcastService", "Seconds: " + millisUntilFinished / 1000);
                    intent.putExtra("countdown", millisUntilFinished);
                    sendBroadcast(intent);

                    GlobalVariables.userMillisec = millisUntilFinished;
                    // reduce the seconds in the global variable
                    Log.i("BroadcastService", "Global Variable " + GlobalVariables.userMillisec);

                    isRunning = true;
                }

                public void onFinish() {

                    sendNotification();
                    NotifyChannelOreo();

                    isRunning = false;

                }
            };
            countDownTimer.start();


        } else {

            // continue running
            countDownTimer.cancel();

            countDownTimer = new CountDownTimer(userTimeInSeconds, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {

                    Log.i("BroadcastService", "Seconds: " + millisUntilFinished / 1000);
                    intent.putExtra("countdown", millisUntilFinished);
                    sendBroadcast(intent);

                    GlobalVariables.userMillisec = millisUntilFinished;

                    isRunning = true;
                }

                public void onFinish() {

                    sendNotification();
                    NotifyChannelOreo();

                    isRunning = false;
                    GlobalVariables.tucketDatExist = false;

                    //delete ticket
                    databaseRef.removeValue();
                }
            };
            countDownTimer.start();
        }

    }
    }


    @Override
    public void onDestroy() {

      //  countDownTimer.cancel();
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





    private void sendNotification(){

        //creating an intent that is called when the notification is clicked
        Intent resultIntent = new Intent(this,TimeLeft.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder  = new NotificationCompat.Builder(BroadcastService.this,"My Notification");
        builder.setContentTitle("Smart Parking");
        builder.setContentText("Hello! Your Time is Up. Please move your car or TopUp");
        builder.setSmallIcon(R.drawable.announcement);
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(BroadcastService.this);
        managerCompat.notify(1,builder.build());


    }

    private void NotifyChannelOreo(){
        //for oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new  NotificationChannel("TimeUpNotification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            //creating an intent that is called when the notification is clicked
            Intent resultIntent = new Intent(this,TimeLeft.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder  = new NotificationCompat.Builder(BroadcastService.this,"My Notification");
            builder.setContentTitle("Smart Parking");
            builder.setContentText("Hello! Your Time is Up. Please move your car or TopUp");
            builder.setSmallIcon(R.drawable.announcement);
            builder.setAutoCancel(true);
            builder.setContentIntent(resultPendingIntent);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(BroadcastService.this);
            managerCompat.notify(1,builder.build());


        }
    }





}
