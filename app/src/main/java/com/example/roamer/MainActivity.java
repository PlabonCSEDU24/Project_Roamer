package com.example.roamer;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    boolean flag=false;
    private GoogleMap map;
    Location lastLocation;
    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private androidx.cardview.widget.CardView myLocationButton;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    DatabaseHelper databaseHelper;
    Geocoder geocoder;
    List<Address> geocodedAddresses;
    String myArea;
    Button findVehicleButton;
    Toolbar toolbar;
    AutoCompleteTextView searchBar0,searchBar;
    FindVehicle findVehicle;
    TextView textView;
    Cursor busCursor,roadCursor;
    Adapter adapter;
    ArrayList<Integer> stoppageList=new ArrayList<>();
    ArrayList<String> stoppageInRoad=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*:::::::::::::::::::::::navigation things::::::::::::::::::::::*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        /*:::::::::::::::::::::::status bar transparent::::::::::::::::::::::*/
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);

        /*:::::::::::::::::::::::ship previously saved database with application::::::::::::::::::::::*/
        shipDatabase();
        databaseHelper=new DatabaseHelper(this);

        /*:::::::::::::::::::::::map api things::::::::::::::::::::::*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myLocationButton = (androidx.cardview.widget.CardView) findViewById(R.id.imgMyLocation);

        /*:::::::::::::::::::::::search::::::::::::::::::::::*/

        findVehicleButton=(Button)findViewById(R.id.findTransportButton);
        findVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                //searchBar.requestFocus();
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                if(Build.VERSION.SDK_INT>20){
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(intent,options.toBundle());
                }else {
                    startActivity(intent);
                }
            }
        });

        showRecentSearches();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_bus: {
                Intent intent = new Intent(this, BusListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_coach: {
                Intent intent = new Intent(this, CoachListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_train: {
                Intent intent = new Intent(this, TrainListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.uber: {
                Intent intent = new Intent(this, UberHandler.class);
                startActivity(intent);
                break;
            }
            case R.id.update:{
                Intent intent = new Intent(this, DataUpdateActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_share:{
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "\nhttps://github.com/PlabonCSEDU24/Project_Roamer\n";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Let me share an useful app:\n");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googlemap) {
        map = googlemap;
        try {
            boolean success = googlemap.setMapStyle
                    (MapStyleOptions.loadRawResourceStyle
                            (this, R.raw.mapstyle));
            if (!success) {
                Log.e("MainActivity", "style parsing failed");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MainActivity", "Can't find style. Error: ", e);
        }
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);

            } else {
                checkLocationPermission();
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        LatLng doyelChattar = new LatLng(23.728014, 90.400323);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(doyelChattar, 11));
        map.getUiSettings().setMyLocationButtonEnabled(false);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                map.animateCamera(cameraUpdate);
                setSourceTextMyAddress();
            }
        });
        //initSearch();
    }
    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    lastLocation = location;
                    if(!flag) {
                      //  setSourceTextMyAddress();
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(15));
                        setSourceTextMyAddress();
                        flag=true;
                    }
                }
            }
        }
    };
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                                map.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void setSourceTextMyAddress(){
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            geocodedAddresses=geocoder.getFromLocation(lastLocation.getLatitude(),lastLocation.getLongitude(),1);
            String address = geocodedAddresses.get(0).getAddressLine(0);
            String area=geocodedAddresses.get(0).getSubLocality();
            myArea=area;
            searchBar0.setText(area);
            searchBar0.setHint(address.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private LatLng getLocationFromAddress(Context context, String strAddress) {
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
    private void moveCamera(LatLng latLng){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            MarkerOptions options = new MarkerOptions()
                    .position(latLng);
            map.addMarker(options);
    }

    private void showRecentSearches(){
        textView=(TextView)findViewById(R.id.textItem);
        RecyclerView recentSearchesListView=(RecyclerView)findViewById(R.id.recentSearchList);
        busCursor = databaseHelper.getRecentSearches();
        recentSearchesListView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter(this, busCursor);
        recentSearchesListView.setAdapter(adapter);
        adapter.setOnItemClickListener(new Adapter.clickListener(){

            @Override
            public void onItemClick(int position, View view,int roadId,String busName) {
                getStoppageData(roadId);
                createNewActivity(busName);
            }
        });


    }

    private void shipDatabase(){
        String appDataPath = this.getApplicationInfo().dataDir;
        File dbFolder = new File(appDataPath + "/databases");//Make sure the /databases folder exists
        dbFolder.mkdir();//This can be called multiple times.

        File dbFilePath = new File(appDataPath + "/databases/BusDatabase.sqlite");

        try {
            InputStream inputStream = this.getAssets().open("BusDatabase.sqlite");
            OutputStream outputStream = new FileOutputStream(dbFilePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Toast.makeText(this, "copy hoy nai", Toast.LENGTH_SHORT).show();
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

