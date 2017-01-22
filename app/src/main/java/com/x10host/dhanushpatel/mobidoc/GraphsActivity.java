package com.x10host.dhanushpatel.mobidoc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GraphsActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    LineChart symptomsChart;

    ArrayList<Double> cataractData = new ArrayList<>(),
            redveinData = new ArrayList<>(),
            vitiligoData = new ArrayList<>(),
            cornData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        symptomsChart = (LineChart) findViewById(R.id.symptomsChart);

        YAxis yAxis = symptomsChart.getAxisLeft();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);

        XAxis xAxis = symptomsChart.getXAxis();
        xAxis.setGranularity(1f);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        fbListenerStart();
    }

    public LineDataSet generateLDS(ArrayList<Double> datas, String dataSetName) {
        List<Entry> entry = new ArrayList<Entry>();
        for (int i = 0; i < datas.size(); i++) {
            entry.add(new Entry(i + 1, Float.parseFloat(String.valueOf(datas.get(i)))));
        }

        LineDataSet LDS = new LineDataSet(entry, dataSetName);
        LDS.setAxisDependency(YAxis.AxisDependency.LEFT);
        LDS.setLineWidth(2f);
        LDS.setCircleRadius(4f);
        return LDS;
    }

    public void populateChart() {
        LineDataSet cataractLDS = generateLDS(cataractData, "Cataract");
        cataractLDS.setColor(Color.YELLOW);
        LineDataSet redveinLDS = generateLDS(redveinData, "Red Eye");
        redveinLDS.setColor(Color.RED);
        LineDataSet vitiligoLDS = generateLDS(vitiligoData, "Vitiligo");
        vitiligoLDS.setColor(Color.CYAN);
        LineDataSet cornLDS = generateLDS(cornData, "Corn");
        cornLDS.setColor(Color.GREEN);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(cataractLDS);
        dataSets.add(redveinLDS);
        dataSets.add(vitiligoLDS);
        dataSets.add(cornLDS);

        LineData lineData = new LineData(dataSets);
        symptomsChart.setData(lineData);
        symptomsChart.invalidate();
    }

    public ArrayList<Double> getDataFromChild(DataSnapshot dataSnap) {
        Map<String, Object> dataMap = (Map<String, Object>) dataSnap.getValue(true);
        return new ArrayList<Double>(Arrays.asList(dataMap.values().toArray(new Double[0])));
    }

    private void fbListenerStart() {
        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cataractData = getDataFromChild(dataSnapshot.child("cataractData"));
                redveinData = getDataFromChild(dataSnapshot.child("redveinData"));
                vitiligoData = getDataFromChild(dataSnapshot.child("vitiligoData"));
                cornData = getDataFromChild(dataSnapshot.child("cornData"));

                populateChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mPostReference.addValueEventListener(postListener);
    }
}
