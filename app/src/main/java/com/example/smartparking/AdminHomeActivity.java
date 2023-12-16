package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AdminHomeActivity extends AppCompatActivity {

    private int SundayCount,MondayCount,TuesdayCount,WednesdayCount,ThursdayCount,FridayCount,SaturdayCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);


        populateValues();

        drawBarChart();
    }



    // Get values to populate to the bar chart

    private void populateValues(){

        SundayCount = getIntent().getIntExtra("keySundayValues",0);
        MondayCount = getIntent().getIntExtra("keyMondayValues",0);
        TuesdayCount = getIntent().getIntExtra("keyTuesdayValues",0);
        WednesdayCount = getIntent().getIntExtra("keyWednesdayValues",0);
        ThursdayCount = getIntent().getIntExtra("keyThursdayValues",0);
        FridayCount = getIntent().getIntExtra("keyFridayValues",0);
        SaturdayCount = getIntent().getIntExtra("keySaturdayValues",0);

    }


    // display the Bar Chart Function
    private void drawBarChart(){

        //Pie Chart instance form the xml
        BarChart barChart = findViewById(R.id.barchartTest);

        //arraylist to store pie chat data
        ArrayList<BarEntry> parkingStats = new ArrayList<>();

        parkingStats.add(new BarEntry(0f, SundayCount));
        parkingStats.add(new BarEntry(1f, MondayCount));
        parkingStats.add(new BarEntry(2f, TuesdayCount));
        parkingStats.add(new BarEntry(3f, WednesdayCount));
        parkingStats.add(new BarEntry(4f, ThursdayCount));
        parkingStats.add(new BarEntry(5f, FridayCount));
        parkingStats.add(new BarEntry(6f, SaturdayCount));

        BarDataSet bardataset = new BarDataSet(parkingStats, "Cars");

        //setting the bar chart colors
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        //text size
        bardataset.setValueTextSize(16f);
        // the color to the bar  chart values
        bardataset.setValueTextColor(Color.BLACK);


        final ArrayList<String> labels = new ArrayList<String>();
        labels.add("Sun");
        labels.add("Mon");
        labels.add("Tue");
        labels.add("Wed");
        labels.add("Thr");
        labels.add("Fri");
        labels.add("Sat");

        BarData data = new BarData(bardataset);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        //setting the bar  chart data
        barChart.setData(data);


        //Chart Animation
        barChart.animateY(5000);
        barChart.getDescription().setText("Weekly Parking Statistics");
        barChart.animate();


    }
}