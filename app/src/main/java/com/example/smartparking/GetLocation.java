package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.smartparking.databinding.ActivityGetLocationBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GetLocation extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ActivityGetLocationBinding binding;
    // Reference to the parking slots booking status in the database
    private DatabaseReference SlotBookingDatabaseRef;

    //markers to be displayed on the map
    MarkerOptions  slot1, slot2, slot3;

    //get direction button reference
    private Button getDirection;

    private LatLng user;

    //An instance of slot data class to set and get values from the database
    SlotData slotsData = new SlotData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityGetLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Currents user's coordinates
        user = new LatLng(GlobalVariables.lat_, GlobalVariables.long_);

        // slot 1 coordinates
        slot1 = new MarkerOptions().position(new LatLng(-18.9779536303923, 32.67726898142822)).title("Slot 1");

        // slot 2 coordinates
        slot2 = new MarkerOptions().position(new LatLng(-18.9779536303923, 32.67726898142822)).title("Slot 2");

        // slot 3 coordinates
        slot3 = new MarkerOptions().position(new LatLng(-18.9779536303923, 32.67726898142822)).title("Slot 3");



    }

    @Override
    protected void onStart() {
        super.onStart();

        getDirection = (Button) findViewById(R.id.getDirection);

        // opens google navigation corner by corner direction when clicked

        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=-18.9779536303923,32.67726898142822&mode=d"));
                intent.setPackage("com.google.android.apps.maps");
                if(intent.resolveActivity(getPackageManager())!=null) {
                    startActivity(intent);
                }

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //OPEN ON CONDITION THAT THE SLOTS A FREE

        //mMap.addMarker(userCurrentLocation);
        mMap.addMarker(new MarkerOptions().position(user).title("You are here"));


        //Database references for all the 3 slots to check if they are booked or not
        SlotBookingDatabaseRef = FirebaseDatabase.getInstance().getReference("Reservations");//Reservations/Slots1_Occ

        //Listen to realtime  changes on the slots whether booked or occupied

        SlotBookingDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String slot1bookStatus = snapshot.child("Slot1").getValue(String.class);
                    String slot1OccStatus = snapshot.child("Slots1_Occ").getValue(String.class);

                    String slot2bookStatus = snapshot.child("Slot2").getValue(String.class);
                    String slot2OccStatus = snapshot.child("Slots2_Occ").getValue(String.class);

                    String slot3bookStatus = snapshot.child("Slot3").getValue(String.class);
                    String slot3OccStatus = snapshot.child("Slots3_Occ").getValue(String.class);

                    slotsData.setSlot1_book_Status(slot1bookStatus);
                    slotsData.setSlot1_Occ_Status(slot1OccStatus);
                    slotsData.setSlot2_book_Status(slot2bookStatus);
                    slotsData.setSlot2_Occ_Status(slot2OccStatus);
                    slotsData.setSlot3_book_Status(slot3bookStatus);
                    slotsData.setSlot3_Occ_Status(slot3OccStatus);

                }

                // Functions for realtime update to the slot status

                updatingSlot1();
                updatingSlot2();
                updatingSlot3();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });



        LatLng zoomloc = new LatLng(-18.9779536303923, 32.67726898142822);
        float zoomLevel = 17.0f; //16.0f; //
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomloc, zoomLevel));

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void updatingSlot1(){

        String slot1Book = slotsData.getSlot1_book_Status();
        String slot1_Occ = slotsData.getSlot1_Occ_Status();

        if (!slot1Book.equals("Booked") && !slot1_Occ.equals("0")) {

            //Display slot 1  marker on the map
            mMap.addMarker(slot1).setIcon((bitmapDescriptorFromVector(GetLocation.this, R.drawable.slotcar)));
            //
            // create a marker object to use the below functions

            // else
            //Marker.setVisible(boolean);
            //Marker.remove()

            /**
             *  currentLocationMarker = mMap.addMarker(new MarkerOptions().position(
             *                     new LatLng(getLatitude(), getLongitude()))
             *                     .title("You are now Here").visible(true)
             *                     .icon(Utils.getMarkerBitmapFromView(getActivity(), R.drawable.auto_front))
             *                     .snippet("Updated Location"));*
             * */
        }
    }


    private void updatingSlot2(){

        String slot2Book = slotsData.getSlot2_book_Status();
        String slot2_Occ = slotsData.getSlot2_Occ_Status();

        if (!slot2Book.equals("Booked") && !slot2_Occ.equals("0")) {

            //Display slot 1  marker on the map
            mMap.addMarker(slot2).setIcon((bitmapDescriptorFromVector(GetLocation.this, R.drawable.slotcar2)));

        }

    }



    private void updatingSlot3(){

        String slot3Book = slotsData.getSlot3_book_Status();
        String slot3_Occ = slotsData.getSlot3_Occ_Status();

        if (!slot3Book.equals("Booked") && !slot3_Occ.equals("0")) {

            //Display slot 1  marker on the map
            mMap.addMarker(slot3).setIcon((bitmapDescriptorFromVector(GetLocation.this, R.drawable.slotcar3)));
        }

    }


}