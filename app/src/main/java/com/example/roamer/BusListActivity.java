package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class BusListActivity extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    busList busList;
    TextView textView;
    RecyclerView recyclerView;
    Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        createDatabase();
        textView=(TextView)findViewById(R.id.textItem);
        cursor=busList.displayData();
        recyclerView=(RecyclerView)findViewById(R.id.busListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter(this,cursor);
        recyclerView.setAdapter(adapter);

    }


    public void createDatabase(){
        busList =new busList(this);
        sqLiteDatabase=busList.getWritableDatabase();
    }
}
