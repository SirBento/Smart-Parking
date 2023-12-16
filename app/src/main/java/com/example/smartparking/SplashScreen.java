package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 *
 * @author  Bernard Towindo
 * @version 1.0
 * @since   2022-07-13
 */
public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String AccessLevel;
    TextView appName;
    LottieAnimationView lottieAnimationView;
    FirebaseUser LoggediNuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appName = findViewById(R.id.appname);
        lottieAnimationView = findViewById(R.id.lottie);
        // initializing the database authentication
        mAuth = FirebaseAuth.getInstance();



        /**
         * empty_slot_start_animation
         *
         * lottieAnimationView.animate().translationX(2000).setDuration(15000).setStartDelay(2900);//2900
         * appName.animate().translationY(-1400).setDuration(2700).setStartDelay(0);//-1400
         *
         * book_slots
         *
          handler = 10000 for above

             parking_find
         *
         * appName.animate().translationY(-1400).setDuration(2700).setStartDelay(0);//-1400
         * lottieAnimationView.animate().translationX(2000).setDuration(10000).setStartDelay(2900);//2900
         *
         * handler = 6000 for above
         *
         *
         * parking_concept     1
         *
         * comment out lottieAnimationView
         * handler = 4000 for above
         * */
        //appName.animate().translationY(-1100).setDuration(2700).setStartDelay(0);//-1400
        //lottieAnimationView.animate().translationX(2000).setDuration(10000).setStartDelay(2900);//2900
        //YoYo.with(Techniques.Landing).duration(1000).repeat(3).playOn(appName);
        YoYo.with(Techniques.Pulse).duration(2000).repeat(10).playOn(appName);



        disableAllSllCerts();
        //check if the device is connected to the a network and if it has internet access
        if(!haveNetworkConnection()){

            Toast.makeText(SplashScreen.this, "Please Check Your Internet Connection!!!", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, NoInternetActivity.class));
                    finish();
                }
            }, 4000);

        }else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    LoggediNuser = mAuth.getCurrentUser();

                    if(LoggediNuser!= null){

                        userType(LoggediNuser.getUid());
                    }else {

                        startActivity(new Intent(SplashScreen.this,Log_In.class));
                        finish();

                    }
                }
            }, 6000);// extend seconds if it is failing acquire the current user  Uid

        }


    }

    /**if checks if there is a user already logged in, if so it opens the homepage
    @Override
    protected void onStart() {
        super.onStart();
        LoggediNuser = mAuth.getCurrentUser();
        if(LoggediNuser!= null){

            userType(LoggediNuser.getUid());
        }
    }**/


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        return haveConnectedWifi || haveConnectedMobile;
    }



    private void disableAllSllCerts(){

        try {
            TrustManager[] victimizedManager = new TrustManager[]{

                    new X509TrustManager() {

                        public X509Certificate[] getAcceptedIssuers() {

                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];

                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, victimizedManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void userType(String uid){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Roles").child(uid);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //store the access level of the user trying to login
                AccessLevel = snapshot.getValue(String.class);

                if( AccessLevel.equals("Marshal")){


                    Toast.makeText(SplashScreen.this, "Welcome Marshal....", Toast.LENGTH_LONG).show();

                    //if login is successful go to the Marshal's homepage
                    startActivity(new Intent( SplashScreen.this,MarshalHomeActivity.class) );
                    finish();


                }else if(AccessLevel.equals("Driver")){


                    Toast.makeText(SplashScreen.this, "Welcome Driver....", Toast.LENGTH_LONG).show();
                    //if login is successful go to the Driver's homepage
                    startActivity(new Intent( SplashScreen.this,Home.class) );
                    finish();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


}