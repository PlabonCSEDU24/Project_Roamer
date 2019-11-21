package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Vector;

public class SearchActivity extends AppCompatActivity {
    SlidingUpPanelLayout slidingUpPanel;
    AutoCompleteTextView searchBar0;
    AutoCompleteTextView searchBar;
    DatabaseHelper databaseHelper;
    FindVehicle findVehicle;
    ListView queryResultListView;
    Cursor busCursor,roadCursor,vehicleNames;
    Adapter adapter;
    ArrayList<String> placeNames;
    ArrayList<Integer> stoppageList=new ArrayList<>();
    ArrayList<String> stoppageInRoad=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_search);
        slidingUpPanel=findViewById(R.id.sliding_layout1);
        queryResultListView=findViewById(R.id.queryResultList);;
        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        databaseHelper=new DatabaseHelper(this);
        findVehicle=new FindVehicle(this);
        addListenersTOSearchBars();


    }
    public void setAnimation(){
        if(Build.VERSION.SDK_INT>20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(400);
            slide.setInterpolator(new DecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }
    }


        private void addListenersTOSearchBars() {
            searchBar0= findViewById(R.id.search_bar_1);
            searchBar= findViewById(R.id.search_bar_2);
            Cursor cursor=databaseHelper.getStoppage();
            placeNames=new ArrayList<>();
            while(cursor.moveToNext()){
                placeNames.add(cursor.getString(1));

            }
            ArrayAdapter<String> items=new ArrayAdapter<>(SearchActivity.this,android.R.layout.simple_list_item_1,placeNames);
            searchBar0.setAdapter(items);
            searchBar.setAdapter(items);
            searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                    findVehicles(searchBar0.getText().toString(),searchBar.getText().toString());
                    searchBar.clearFocus();

                }
            });

    }



    void findVehicles(String source,String destination){

        findVehicle=new FindVehicle(this);
        final Vector<Integer>[][]roadId;
        ArrayList<String> stoppageInRoad;
        final ArrayList<String> resultList=new ArrayList<>();
        final ArrayList<String>routeId=new ArrayList<>();
        int [] placeIds;
        findVehicle.findRoadAlgo(source,destination);
        roadId=findVehicle.getRoadID();
        stoppageInRoad=findVehicle.getStoppageInRoad();
        placeIds=findVehicle.getPlaceId();

        for(int i=stoppageInRoad.size()-1;i>0;i--) {
            try {
                // Toast.makeText(this, stoppageInRoad.get(i)+"->"+stoppageInRoad.get(i-1)+"\nRoad Id "+roadId[placeIds[i-1]][placeIds[i]].get(0), Toast.LENGTH_SHORT).show();
                Cursor cursor=databaseHelper.getVehicleNameByRoadID(roadId[placeIds[i-1]][placeIds[i]].get(0));
                while(cursor.moveToNext()) {
                    if(!resultList.contains(cursor.getString(0))){
                        resultList.add(cursor.getString(0));
                        routeId.add(cursor.getString(1));
                    }
                }

            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if(!resultList.isEmpty()) {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, resultList);
            queryResultListView.setAdapter(adapter);
            queryResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(SearchActivity.this,resultList.get(i)+routeId.get(i),Toast.LENGTH_SHORT).show();
                    getStoppageData(Integer.parseInt(routeId.get(i)));
                    createNewActivity(resultList.get(i));
                }
            });


        }
    }

    void getStoppageData(int roadId){
        roadCursor= databaseHelper.displayRoadData();
        stoppageList.clear();
        int firstStoppage=0;
        while(roadCursor.moveToNext()){
            if(Integer.parseInt(roadCursor.getString(1))==roadId){
                if(firstStoppage==0){
                    stoppageList.add(Integer.parseInt(roadCursor.getString(2)));
                    firstStoppage=1;
                }
                stoppageList.add(Integer.parseInt(roadCursor.getString(3)));
            }
            else if(Integer.parseInt(roadCursor.getString(1))>roadId)
                break;
        }
    }
    void makeStoppageList(){
        Cursor cursor= databaseHelper.getStoppage();
        int n=stoppageList.size();
        ArrayList<String> stoppage=new ArrayList<>();
        stoppageInRoad.clear();
        while(cursor.moveToNext()){
            stoppage.add(cursor.getString(1));
        }
        for(int i=0;i<n;i++){
            String str=stoppage.get(stoppageList.get(i));
            stoppageInRoad.add(str);
        }
    }

    void createNewActivity(String busName){
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        makeStoppageList();
        Intent intent=new Intent(this,RoadListByBus.class);
        intent.putExtra("ara",stoppageInRoad);
        intent.putExtra("busName",busName);
        startActivity(intent);
    }

}


