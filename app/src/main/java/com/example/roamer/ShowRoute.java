package com.example.roamer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowRoute extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {
    private GoogleMap mMap;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;
    LatLng doyelChattar;
    Geocoder geocoder;
    ArrayList<String> stoppageList=new ArrayList<>();
    ArrayList<LatLng> stoppageLatLngList=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            stoppageList=bundle.getStringArrayList("stoppageList");
        }
        polylines=new ArrayList<>();
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        makeStoppegLatLngList();
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stoppageLatLngList.get(0), 11));
        place1 = new MarkerOptions().position((stoppageLatLngList.get(0))).title(stoppageList.get(0));
        place2 = new MarkerOptions().position((stoppageLatLngList.get(stoppageLatLngList.size()-1))).title(stoppageList.get(stoppageList.size()-1));
        mMap.addMarker(place1);
        mMap.addMarker(place2);
        LatLng origin=new LatLng(23.728014,90.400323);
        LatLng destination=new LatLng(23.755613,90.368591);
        getRoute();


    }
    void makeStoppegLatLngList(){
        stoppageLatLngList.clear();
        for(int i=(stoppageList.size()-1);i>=0;i--){
            try {
                //Toast.makeText(this,i+ " "+stoppageList.get(i), Toast.LENGTH_SHORT).show();
                LatLng latLng = getLocationFromAddress(stoppageList.get(i)+" Main Road bus stoppage, Dhaka");
                stoppageLatLngList.add(latLng);
                //Toast.makeText(this, latLng.latitude + "\n" + latLng.longitude, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, stoppageList.get(i)+"\n"+e, Toast.LENGTH_SHORT).show();
            }
        }
    }





    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error eta: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void getRoute() {
        Routing routing=new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(stoppageLatLngList)
                .optimize(true)
                .alternativeRoutes(true)
                .key("AIzaSyBMqiUuZn23MmLndhWUX56SKgwV_GA0A8U")
                .build();
        routing.execute();
    }
    private void erasePolylines(){
        for(Polyline lines:polylines){
            lines.remove();
        }
        polylines.clear();
    }

    private LatLng getLocationFromAddress(String strAddress) {
        geocoder=new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = geocoder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
            Toast.makeText(this, "not found!", Toast.LENGTH_SHORT).show();
        }
        return p1;
    }
}
