package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RoadListByBus extends AppCompatActivity {
    TextView textView;
    ListView listView;

    ArrayList<String> stoppageArra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_list_by_bus);
        textView=findViewById(R.id.busNameByRoadId);
        listView=findViewById(R.id.stoppageList);
        textView.setText("Rezaul Karim");
        String[] ara={"Chittagong Road","Chankhar Pul","Bokshi Bazar","Azimpur","New Market","City College","Dhaka College","Chittagong Road","Chankhar Pul","Bokshi Bazar","Azimpur","New Market","City College","Dhaka College"};
        ArrayAdapter<String> stoppageArrayList=new ArrayAdapter<>(this,R.layout.stoppage_list,R.id.stoppageTextId,ara);
        listView.setAdapter(stoppageArrayList);
    }

}
