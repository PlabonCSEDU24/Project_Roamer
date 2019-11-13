package com.example.roamer;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class busList extends SQLiteOpenHelper {
    private static final int version=4;
    private static final String databaseName="BusDatabase.sqlite";
    public static String APP_DATA_PATH="";
    public static final String DB_SUB_PATH="/databases/" + databaseName;
    private SQLiteDatabase dataBase;
    private static final String tableName1="VehicleList";
    private static final String tableName2="RouteList";
    private static final String tableName3="Places";
    private static final String busID="Vahicle_ID";
    private static final String busName="Vahicle_Name";
    private static final String roadID="RoadID";
    private static final String type="Vahicle_Type";
    private static final String createBusTable="create table "+tableName1+"( "+busID+" INTEGER primary key autoincrement, "+busName+" varchar(100), "+type+" varchar(10), "+roadID+" INTEGER, foreign key(RoadID) REFERENCES "+tableName2+"(RoadID));";
    private static final String createRoadTable="create table "+tableName2+" ( "+roadID+" INTEGER, " +
            " Stoppage1 int, " +
            " Stoppage2 int, primary key(roadId,Stoppage1,Stoppage2), " +
            "foreign key(Stoppage1) references "+tableName3+"(stoppageId),foreign key(Stoppage2) references "+tableName3+"(stoppageId)); ";
    private static final String createStoppageIndexingTable="create table "+tableName3+"( stoppageId INTEGER primary key autoincrement, stoppage varchar(20) unique);";

    private static final String dropTable1="drop table if exists "+tableName1;
    private static final String dropTable2="drop table if exists "+tableName2;
    private static final String dropTable3="drop table if exists "+tableName3;
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
        try {
            db.execSQL(createStoppageIndexingTable);
            db.execSQL(createRoadTable);
            db.execSQL(createBusTable);
            Toast.makeText(context, "Table", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
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
            db.execSQL(dropTable3);
           // Toast.makeText(context, "OnUpgrade method is called", Toast.LENGTH_SHORT).show();
            onCreate(db);
        } catch (Exception e) {
            Toast.makeText(context, "upFailed", Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor displayBusData(){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("select * from "+tableName1 ,null);
        return cursor;
    }
    public Cursor displayRoadData(){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("select * from "+tableName2 ,null);
        return cursor;
    }
    public Cursor getStoppage(){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("select * from "+tableName3 ,null);
        return cursor;
    }
    public Cursor autocompleteQuery(String str){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("select Bus_Name from "+tableName1+" where Bus_Name like '%"+str+"%'" ,null);
        return cursor;
    }
    public int getStoppageId(String str){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from Places where place_name ='"+str+"'", null);
            cursor.moveToNext();
            Toast.makeText(context, cursor.getString(0), Toast.LENGTH_SHORT).show();
            return cursor.getInt(0);
        } catch (Resources.NotFoundException e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }
        return 0;
    }
    public Cursor getVehicleNameByRoadID(int id){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("select Vehicle_name from "+tableName1+" where route_id= "+id,null);
        return cursor;
    }

}
