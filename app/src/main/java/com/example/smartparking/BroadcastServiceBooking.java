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
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BroadcastServiceBooking extends Service{

    public  static  final String COUNTDOWN_BR ="com.example.smartparking";
    Intent intent = new Intent(COUNTDOWN_BR);
    public  static CountDownTimer countDownTimer = null;
    public long userTimeInSeconds;
    private boolean isRunning;
    @Override
    public void onCreate() {
        super.onCreate();
        //ticket countdown timer
        userTimeInSeconds = GlobalVariables.bookuserMillisec;
        if (userTimeInSeconds >1000) {
            if (!isRunning && !GlobalVariables.bookingTicketrunning) {
                //start counting
                countDownTimer = new CountDownTimer(userTimeInSeconds, 1000) {
                    // adjust the milli seconds here
                    public void onTick(long millisUntilFinished) {
                        Log.i("BroadcastServiceBooking", "Seconds: " + millisUntilFinished / 1000);
                        intent.putExtra("countdown", millisUntilFinished);
                        sendBroadcast(intent);
                        // constantly update the global variable with the correct left time
                        GlobalVariables.bookuserMillisec = millisUntilFinished;

                        Log.i("BroadcastServiceBooking", "Global Variable "
                                + GlobalVariables.bookuserMillisec);
                        isRunning = true;
                        GlobalVariables.bookingTicketrunning = true;
                    }
                    public void onFinish() {
                        sendNotification();
                        NotifyChannelOreo();
                        isRunning = false;
                        GlobalVariables.bookingTicketExist = false;
                        GlobalVariables.bookingTicketrunning = true;
                    }
                };
                countDownTimer.start();

            } else {

                // cancel the timer if the user has parked
                if(GlobalVariables.bookingTicketrunning){
                    countDownTimer.cancel();

                }else{

                // continue running if the user has not yet parked

                countDownTimer.cancel();
                countDownTimer = new CountDownTimer(userTimeInSeconds, 1000) { // adjust the milli seconds here

                    public void onTick(long millisUntilFinished) {

                        Log.i("BroadcastServiceBooking", "Seconds: " + millisUntilFinished / 1000);
                        intent.putExtra("countdown", millisUntilFinished);
                        sendBroadcast(intent);

                        // constantly update the global variable with the correct left time
                        GlobalVariables.bookuserMillisec = millisUntilFinished;

                        isRunning = true;  }

                    public void onFinish() {

                        NotifyChannelOreo();
                        sendNotification();
                        isRunning = false;
                        GlobalVariables.bookingTicketExist = false; }

                };
                countDownTimer.start();
            }

            }

        }
    }

    @Override
    public void onDestroy() {

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

        NotificationCompat.Builder builder  = new NotificationCompat.Builder(BroadcastServiceBooking.this,"My Notification");
        builder.setContentTitle("Smart Parking");
        builder.setContentText("Hello! Your Reservation has been terminated because your failed to park in time");
        builder.setSmallIcon(R.drawable.announcement);
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(BroadcastServiceBooking.this);
        managerCompat.notify(1,builder.build());

    }

    private void NotifyChannelOreo(){
        //for oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new  NotificationChannel("BookTimeUpNotification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            //creating an intent that is called when the notification is clicked
            Intent resultIntent = new Intent(this,TimeLeft.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder  = new NotificationCompat.Builder(BroadcastServiceBooking.this,"My Notification");
            builder.setContentTitle("Smart Parking");
            builder.setContentText("Hello! Your Reservation has been terminated because your failed to park in time");
            builder.setSmallIcon(R.drawable.announcement);
            builder.setAutoCancel(true);
            builder.setContentIntent(resultPendingIntent);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(BroadcastServiceBooking.this);
            managerCompat.notify(1,builder.build());

        }
    }
}
