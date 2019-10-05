package com.example.roamer;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class busList extends SQLiteOpenHelper {
    private static final int version=1;
    private static final String databaseName="BusDatabase.sqlite";
    public static String APP_DATA_PATH="";
    public static final String DB_SUB_PATH="/databases/" + databaseName;
    private SQLiteDatabase dataBase;
    private static final String tableName1="Bus";
    private static final String tableName2="BusRoads";
    private static final String busID="Bus_ID";
    private static final String busName="Bus_Name";
    private static final String roadID="RoadID";
    private static final String createBusTable="create table "+tableName1+"( "+busID+" INTEGER primary key autoincrement, "+busName+" varchar(100), "+roadID+" INTEGER, foreign key(RoadID) REFERENCES BusRoads(RoadID));";
    private static final String createRoadTable="create table "+tableName2+"( "+roadID+" INTEGER primary key autoincrement, " +
            "Stoppage1 varchar(100)," +
            "Stoppage2 varchar(100)," +
            "Stoppage3 varchar(100)," +
            "Stoppage4 varchar(100)," +
            "Stoppage5 varchar(100)," +
            "Stoppage6 varchar(100)," +
            "Stoppage7 varchar(100)," +
            "Stoppage8 varchar(100)," +
            "Stoppage9 varchar(100)," +
            "Stoppage10 varchar(100)," +
            "Stoppage11 varchar(100)," +
            "Stoppage12 varchar(100)," +
            "Stoppage13 varchar(100)," +
            "Stoppage14 varchar(100)," +
            "Stoppage15 varchar(100)," +
            "Stoppage16 varchar(100)," +
            "Stoppage17 varchar(100)," +
            "Stoppage18 varchar(100)," +
            "Stoppage19 varchar(100)," +
            "Stoppage20 varchar(100)," +
            "Stoppage21 varchar(100)," +
            "Stoppage22 varchar(100)," +
            "Stoppage23 varchar(100)," +
            "Stoppage24 varchar(100)," +
            "Stoppage25 varchar(100)," +
            "Stoppage26 varchar(100)," +
            "Stoppage27 varchar(100)," +
            "Stoppage28 varchar(100)," +
            "Stoppage29 varchar(100)," +
            "Stoppage30 varchar(100));";

    private static final String dropTable1="drop table if exists "+tableName1;
    private static final String dropTable2="drop table if exists "+tableName2;
    Context context;
    public busList(@Nullable Context context) {
        super(context, databaseName, null, version);
        APP_DATA_PATH=context.getApplicationInfo().dataDir;
        this.context=context;
    }
    public boolean openDataBase() throws SQLException {
        String mPath = APP_DATA_PATH + DB_SUB_PATH;
        //Note that this method assumes that the db file is already copied in place
        dataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);
        return dataBase != null;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try{

           db.execSQL(createBusTable);
           db.execSQL(createRoadTable);
           Toast.makeText(context, "Tables are created", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public synchronized void close(){
        if(dataBase != null) {dataBase.close();}
        super.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(dropTable1);
            db.execSQL(dropTable2);
            Toast.makeText(context, "OnUpgrade method is called", Toast.LENGTH_SHORT).show();
            onCreate(db);
        } catch (Exception e) {
            Toast.makeText(context, "upFailed", Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor displayData(){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("select * from "+tableName1 ,null);
        return cursor;
    }
}
