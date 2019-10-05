package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BusListActivity extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabase;
    Cursor busCursor,roadCursor;
    busList busList;
    TextView textView;
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<String> stoppageList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        createDatabase();
        textView=(TextView)findViewById(R.id.textItem);
        busCursor =busList.displayBusData();
        recyclerView=(RecyclerView)findViewById(R.id.busListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter(this, busCursor);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new Adapter.clickListener(){

            @Override
            public void onItemClick(int position, View view,int roadId,String busName) {
                getStoppageData(roadId);
                createNewActivity(busName);
            }
        });

    }
    void getStoppageData(int roadId){
        roadCursor=busList.displayRoadData();
        stoppageList.clear();
        int firstStoppage=0;
        while(roadCursor.moveToNext()){
            if(Integer.parseInt(roadCursor.getString(0))==roadId){
                if(firstStoppage==0){
                    stoppageList.add(roadCursor.getString(1));
                    firstStoppage=1;
                }
                stoppageList.add(roadCursor.getString(2));
            }
            else if(Integer.parseInt(roadCursor.getString(0))>roadId)
                break;
        }
    }
    void createNewActivity(String busName){
        Intent intent=new Intent(this,RoadListByBus.class);
        intent.putExtra("ara",stoppageList);
        intent.putExtra("busName",busName);
        startActivity(intent);
    }
    public void createDatabase(){
        busList =new busList(this);
        sqLiteDatabase=busList.getWritableDatabase();
    }
}
