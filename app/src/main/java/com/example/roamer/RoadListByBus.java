package com.example.roamer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoadListByBus extends AppCompatActivity {
    TextView textView;
    ListView listView;

    ArrayList<String> stoppageArray;
    String busName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_list_by_bus);
        textView=findViewById(R.id.busNameByRoadId);
        listView=findViewById(R.id.stoppageList);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            stoppageArray=bundle.getStringArrayList("ara");
            busName=bundle.getString("busName");
        }
        textView.setText(busName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newActivityToDrawRoute();
            }
        });
        // ArrayAdapter<String> stoppageArrayList=new ArrayAdapter<>(this,R.layout.stoppage_list,R.id.stoppageTextId,stoppageArray);
        //listView.setAdapter(stoppageArrayList);
        ListviewAdapter adapter=new ListviewAdapter(this,stoppageArray);
        listView.setAdapter(adapter);

    }
    void newActivityToDrawRoute(){
        Intent intent=new Intent(this,ShowRoute.class);
        intent.putExtra("stoppageList",stoppageArray);
        startActivity(intent);
    }

    class ListviewAdapter implements ListAdapter {
        Context context;
        ArrayList<String> stoppageList;
        ListviewAdapter(Context context,ArrayList<String>stoppageList){
            this.context=context;
            this.stoppageList=stoppageList;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return stoppageList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView=layoutInflater.inflate(R.layout.listview_row_item,null);
            }
            TextView stoppageName=convertView.findViewById(R.id.stoppageid);
            stoppageName.setText(stoppageList.get(position));
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return stoppageList.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }

}
