package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    Date tdate = calendar.getTime();
    String dayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(tdate.getTime());
    String CurrentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference slotdataRecords = mDatabase .getReference("Slots/Data");

    // Location request instance get request the user current location
    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    DatabaseReference databaseRef;
    DatabaseReference databaseBookingRef;
    //ticket variables
    String duration,date,reg_no,driver,Uid;
    // booking ticket variables
    String booktname, bookDate, bookDuration, bookSlot, bookUid, bookregNo;


    private CardView paymentCard, parkingTimeLeftcard, mapLocation, parkingSlotscard,
                    parkingStatistics, userLogout, bookForParking, parkingHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        if (dayOfTheWeek.equals("Sunday") && CurrentTime.equals("01:00 PM") || CurrentTime.equals("01:00 p.m.")){

            slotdataRecords.removeValue();
        }
        // turn off restriction mode for android versions higher or  equal to 9

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //check if the device is connected to the a network and if it has internet access
        if(!haveNetworkConnection()){

            Toast.makeText(Home.this, "Please Check Your INTERNET CONNECTION And RESTART The Application!!!!! Otherwise the application will not work as expected", Toast.LENGTH_LONG).show();

        }

        databaseRef = FirebaseDatabase.getInstance().getReference("CurrentTicket");
        databaseBookingRef=FirebaseDatabase.getInstance().getReference("BookingTicket");
        paymentCard = findViewById(R.id.payment);
        mapLocation = findViewById(R.id.parkingLocation);
        parkingStatistics =(CardView) findViewById(R.id.parkingStatistics);
        userLogout =(CardView) findViewById(R.id.logout);
        parkingTimeLeftcard =(CardView) findViewById(R.id.parkingTimeLeft);
        parkingSlotscard =(CardView) findViewById(R.id.parkingSlots);
        bookForParking=(CardView) findViewById(R.id.bookForParking);
        parkingHistory = (CardView) findViewById(R.id.parkingHistory);




        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        userticket();
        userBookingticket();


        paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( Home.this,Payment.class) );

            }
        });

        parkingTimeLeftcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( Home.this,ViewTimeLeft.class));

            }
        });


        mapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( Home.this,GetLocation.class) );

            }
        });
        parkingSlotscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( Home.this,ParkingSlots.class) );

            }
        });

        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                Toast.makeText(Home.this, "Log Out Successful", Toast.LENGTH_LONG).show();

                startActivity(new Intent( Home.this, Log_In.class) );
                finish();
            }
        });

        parkingStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( Home.this,Statistics.class));


            }
        });

        bookForParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( Home.this,ParkingSlots.class));

            }
        });

        parkingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( Home.this,ParkingHistory.class));
            }
        });


        //ASKING PERMISSIONS
        getCurrentLocation();


    }






    //Turn on GPS if the user grants access and the gps is off

    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(Home.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Home.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }


    // check if the phone GPS service is enabled

    private Boolean isGPSEnabled(){

        LocationManager locationManager = null;
        boolean isEnabled = false;

        if(locationManager == null){

            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return  isEnabled;
    }

    // override the activity result whenever the user grant access to GPS

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }


//Acquire the user's current location if the user grants access
    private void getCurrentLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(Home.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(Home.this)
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                       // Saving Latitude and Longitude to global variables
                                        GlobalVariables.lat_= latitude;
                                        GlobalVariables.long_ = longitude;
                                        //globalVariables.setUserLocation(new MarkerOptions().position(new LatLng(latitude, longitude )).title("You Are Here")) ;
                                    }
                                }
                            }, Looper.getMainLooper());
                } else {
                    //turn on GPS if it is off
                    turnOnGPS();
                }
            } else {
                // request permission to use gps if the user has not used the app before or has never granted the permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

                //Change to the actual code to be run after the permission is granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent( Home.this,Home.class));


            }else{

                //Change to the actual code to be run after the permission is denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

            }
        }


}


    private void userticket() {

      databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        date = dataSnapshot.child("Date").getValue(String.class);
                        driver = dataSnapshot.child("Driver").getValue(String.class);
                        duration = dataSnapshot.child("Duration").getValue(String.class);
                        reg_no = dataSnapshot.child("Reg_No").getValue(String.class);
                        Uid = dataSnapshot.child("Uid").getValue(String.class);


                        GlobalVariables.ticketname= driver;
                        GlobalVariables.ticketDate = date;
                        GlobalVariables.ticketDuration = duration;
                        GlobalVariables.ticketregNo = reg_no;
                        GlobalVariables.Uid = Uid;

                        GlobalVariables.userMillisec = (Integer.valueOf(GlobalVariables.ticketDuration) * 60000);
                    }

                    GlobalVariables.tucketDatExist = true;
                }else{

                    GlobalVariables.tucketDatExist = false;


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Home.this, "Please Check Your Internet Connection And Restart The Application!!!!!", Toast.LENGTH_LONG).show();

            }
        });

    }



    private void userBookingticket() {


        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){


                        booktname = dataSnapshot.child("Name").getValue(String.class);
                        bookDate= dataSnapshot.child("Date").getValue(String.class);
                        bookDuration = dataSnapshot.child("Duration").getValue(String.class);
                        bookregNo = dataSnapshot.child("Reg_No").getValue(String.class);
                        bookSlot = dataSnapshot.child("Slot").getValue(String.class);
                        bookUid = dataSnapshot.child("Uid").getValue(String.class);


                        GlobalVariables.bookTicketname = booktname;
                        GlobalVariables.bookTicketDate = bookDate;
                        GlobalVariables.bookTicketDuration = bookDuration;
                        GlobalVariables.bookregNo = bookregNo;
                        GlobalVariables.bookSlot =bookSlot;
                        GlobalVariables.bookUid = bookUid;
                        GlobalVariables.bookuserMillisec =(30* 60000);

                    }

                    GlobalVariables.bookingTicketExist = true;

                }else {

                    GlobalVariables.bookingTicketExist = false;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Home.this, "Please Check Your Internet Connection And Restart The Application!!!!!", Toast.LENGTH_LONG).show();

            }
        });

    }


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

   /** private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/
}