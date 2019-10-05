package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            stoppageArra=bundle.getStringArrayList("ara");
        }
        textView.setText("Rezaul Karim");
        ArrayAdapter<String> stoppageArrayList=new ArrayAdapter<>(this,R.layout.stoppage_list,R.id.stoppageTextId,stoppageArra);
        listView.setAdapter(stoppageArrayList);
    }

}
