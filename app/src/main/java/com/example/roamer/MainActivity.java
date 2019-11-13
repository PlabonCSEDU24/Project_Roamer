package com.example.roamer;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
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
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.chainsaw.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

    busList databaseHelper;
    Geocoder geocoder;
    List<Address> geocodedAddresses;
    String myArea;

    Button findVehicleButton;
    Button slideUpButton;
    SlidingUpPanelLayout slidingUpPanel;
    Toolbar toolbar;
    com.mancj.materialsearchbar.MaterialSearchBar searchBar0,searchBar;
    FindVehicle findVehicle;
    ListView queryResultListView;


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

        /*:::::::::::::::::::::::map api things::::::::::::::::::::::*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myLocationButton = (androidx.cardview.widget.CardView) findViewById(R.id.imgMyLocation);

        /*:::::::::::::::::::::::search::::::::::::::::::::::*/
        searchBar0=(com.mancj.materialsearchbar.MaterialSearchBar)findViewById(R.id.search_bar_1);
        searchBar=(com.mancj.materialsearchbar.MaterialSearchBar)findViewById(R.id.search_bar_2);
        addListenersTOSearchBars();

        //:::::::::::::::::::::::::::::::sliding up panel:::::::::::::::::::::::::::::::::::
        slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        findVehicleButton=(Button)findViewById(R.id.findTransportButton);
        queryResultListView=(ListView)findViewById(R.id.queryResultList);
        findVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                searchBar.requestFocus();
            }
        });



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
            searchBar0.setPlaceHolder(address.toString());
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
    private void addListenersTOSearchBars() {
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Vector<Integer>[][]roadId;
                ArrayList<String> resultList;
                startSearch(text.toString(), true, null, false);
                findVehicles(searchBar0.getText().toString(),text.toString());
                searchBar.clearFocus();
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    searchBar.disableSearch();
                }
            }
        });
    }
        //TextChangeListener
      /*  searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*Toast.makeText(MainActivity.this,charSequence.toString(),Toast.LENGTH_LONG).show();
                Cursor cursor=databaseHelper.autocompleteQuery(charSequence.toString());
                List<String> suggestionList = new ArrayList<>();
                while(cursor.moveToNext()){
                    suggestionList.add(cursor.getString(0));
                }
                Toast.makeText(MainActivity.this,suggestionList.get(0).toString(),Toast.LENGTH_LONG).show();
                searchBar.updateLastSuggestions(suggestionList);
                searchBar.showSuggestionsList();



            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    */
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
        databaseHelper=new busList(this);
    }
    void findVehicles(String source,String destination){

        findVehicle=new FindVehicle(this);
        Vector<Integer>[][]roadId;
        ArrayList<String> stoppageInRoad;
        ArrayList<String> resultList=new ArrayList<>();
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
                     }
                 }

            } catch (Exception e) {
               Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if(!resultList.isEmpty()) {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, resultList);
            queryResultListView.setAdapter(adapter);
        }
    }
    
}

