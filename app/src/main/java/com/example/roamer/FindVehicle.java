package com.example.roamer;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Vector;

public class FindVehicle  {
    Context context;
    busList busList;
    int routeFound=0;
    int leangth=150;
    Vector<Integer>[] adjList=new Vector[leangth];
    int[] parent=new int[leangth];
    int privious=-1;
    Vector<Integer> [][]roadId=new Vector[leangth][leangth];
    ArrayList<String> stoppageList=new ArrayList<>();
    String[] nodeColor=new String[leangth];
    ArrayList<String> stoppageInRoad=new ArrayList<>();
    int[] placeIds=new int[leangth];
   // ArrayList<Integer>busIDs=new ArrayList<>();
    FindVehicle(Context context){
        this.context=context;
    }
    void ini(){
        for(int i=0;i<leangth;i++){
            adjList[i]=new Vector<>();
            parent[i]=-1;
        }
        for(int i=0;i<leangth;i++){

            roadId[i]=new Vector[leangth];
        }
        for(int i=0;i<leangth;i++){
            for(int j=0;j<leangth;j++){
                roadId[i][j]=new Vector<>();
            }

        }
    }
    public void findRoadAlgo() {
        ini();
        int origin = -1, destination = -1;
        Cursor roadCursor, stoppageCursor;
        busList = new busList(context);
        try {
            origin = busList.getStoppageId("Motijheel");
            destination = busList.getStoppageId("Sadarghat");
        } catch (Exception e) {
            Toast.makeText(context, "Failed" + e, Toast.LENGTH_SHORT).show();
        }
        /*try {
            origin = busList.getStoppageId(source);
            destination = busList.getStoppageId(Destination);
        } catch (Exception e) {

        }

         */
        stoppageCursor = busList.getStoppage();
        roadCursor = busList.displayRoadData();
        while (stoppageCursor.moveToNext()) {
            String str = stoppageCursor.getString(1);
            //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            stoppageList.add(str);
        }
        try {

            while (roadCursor.moveToNext()) {

                int stoppage1 = Integer.parseInt(roadCursor.getString(2));
                int stoppage2 = Integer.parseInt(roadCursor.getString(3));
                adjList[stoppage1].add(stoppage2);

                adjList[stoppage2].add(stoppage1);
                //Toast.makeText(this, stoppage1 +"\n"+stoppage2, Toast.LENGTH_SHORT).show();
                roadId[stoppage1][stoppage2].add(Integer.parseInt(roadCursor.getString(1)));
                roadId[stoppage2][stoppage1].add(Integer.parseInt(roadCursor.getString(1)));
            }
        } catch (Exception e) {

        }
        setValue();
        routeFound=0;
        dfs(origin,destination);
        prnt(destination);

    }
    void setValue(){
        for(int i=0;i<leangth;i++){
            nodeColor[i]="White";
        }
    }
    void dfs(int root,int destination){
        try {
            if (root == destination) {
                routeFound = 1;

                return;
            }
            if (routeFound == 0) {
                if (nodeColor[root].equals("Gray") || nodeColor[root].equals("Black")) {
                    nodeColor[root] = "Black";
                    return;
                }
                nodeColor[root]="Gray";
                for (int i = 0; i < adjList[root].size(); i++) {
                    if(routeFound==1)
                        return;
                    int x = adjList[root].get(i);
                    if(parent[x]==-1 && nodeColor[x].equals("White")) {
                        parent[x] = root;
                        dfs(x, destination);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void prnt(int des){
        int i=0;
        while(i<leangth){
            //Toast.makeText(this,stoppageList.get(des), Toast.LENGTH_SHORT).show();
            stoppageInRoad.add(stoppageList.get(des)); ///This contains all stoppage of user's desired route in reverse order;
            placeIds[i]=des;
            des=parent[des];
            i++;
            if(des==-1)
                return;
        }
    }
    ArrayList<String> getStoppageInRoad(){
        return stoppageInRoad;
    }
    Vector<Integer>[][] getRoadID(){
        return roadId;
    }
    int[] getPlaceId(){
        return placeIds;
    }
}