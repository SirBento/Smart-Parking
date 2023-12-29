package com.example.smartparking;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarChartStats extends AppCompatActivity {
    //time counters
    int beforeNineCount,
        afterNineCount,
        afterTwelveCount,
        afterThreeCount,
        afterSixCount;

    private int textColor = Color.parseColor("#581b98");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart_stats);

         populateValues();
         drawBarChart();

    }


    // Get values to populate to the bar chart

    private void populateValues(){

        beforeNineCount = getIntent().getIntExtra("keyBeforeNineValues",0);
        afterNineCount = getIntent().getIntExtra("keyAfterNineValues",0);
        afterTwelveCount = getIntent().getIntExtra("keyAfterTwelveValues",0);
        afterThreeCount = getIntent().getIntExtra("keyAfterThreeValues",0);
        afterSixCount = getIntent().getIntExtra("keyAfterSixValues",0);

    }

    // display the Bar Chart Function
    private void drawBarChart(){

        //Bar Chart instance form the xml
        BarChart barChart = findViewById(R.id.ParkingHoursBarChart);

        //arraylist to store pie chat data
        ArrayList<BarEntry> parkingStats = new ArrayList<>();

        parkingStats.add(new BarEntry(0f, beforeNineCount));
        parkingStats.add(new BarEntry(1f, afterNineCount));
        parkingStats.add(new BarEntry(2f, afterTwelveCount));
        parkingStats.add(new BarEntry(3f, afterThreeCount));
        parkingStats.add(new BarEntry(4f, afterSixCount ));


        BarDataSet bardataset = new BarDataSet(parkingStats, "Cars");

        //setting the bar chart colors
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        //text size
        bardataset.setValueTextSize(18f);
        // the color to the bar  chart values
        bardataset.setValueTextColor(Color.BLACK);


        final ArrayList<String> labels = new ArrayList<String>();
        labels.add("6:00-9:00");
        labels.add("9:00-12:00");
        labels.add("12:00-15:00");
        labels.add("15:00-21:00");
        labels.add("18:00-00:00");


        BarData data = new BarData(bardataset);
        barChart.getXAxis().setLabelCount(labels.size());
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