package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class BusListActivity extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    busList busList;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        createDatabase();
        textView=(TextView)findViewById(R.id.busList);
        cursor=busList.displayData();
        StringBuffer stringBuffer=new StringBuffer();
        while (cursor.moveToNext()){
            stringBuffer.append(cursor.getString(0)+"-> "+cursor.getString(1)+"\n");
        }
        textView.setText(stringBuffer.toString());

    }


    public void createDatabase(){
        busList =new busList(this);
        sqLiteDatabase=busList.getWritableDatabase();
    }
}
