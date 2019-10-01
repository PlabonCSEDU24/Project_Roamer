package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class BusListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        createDatabase();
    }

    public void createDatabase(){
        busList busList=new busList(this);
        SQLiteDatabase sqLiteDatabase=busList.getWritableDatabase();
    }
}
