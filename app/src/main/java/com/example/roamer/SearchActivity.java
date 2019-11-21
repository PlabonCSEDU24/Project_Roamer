package com.example.roamer;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {
    Geocoder geocoder;
    List<Address> geocodedAddresses;
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
    boolean flag=false;
    private GoogleMap map;
    Location lastLocation;
    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private androidx.cardview.widget.CardView myLocationButton;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    CardView setOnMapButton;
    ImageView dropDown;
    TextView dropDownText;
    RideRequestButton requestButton;
    LinearLayout layout;
    SessionConfiguration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_search);
        Window window=this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        slidingUpPanel=findViewById(R.id.sliding_layout1);
        queryResultListView=findViewById(R.id.queryResultList);
        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        databaseHelper=new DatabaseHelper(this);
        findVehicle=new FindVehicle(this);
        addListenersTOSearchBars();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment1);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myLocationButton = (androidx.cardview.widget.CardView) findViewById(R.id.imgMyLocation1);
        configureCameraIdle();
        configureDropDown();
        config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("gTCc4H_8dGHZ_mvOX4O2V-48mkY4CqAz")
                // required for enhanced button features
                .setServerToken("1VnrfjZENfFgaE9U2_I6YO85ss68_1vx973oq2c5")
                // required for implicit grant authentication
                .setRedirectUri("")
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(config);
        requestButton = new RideRequestButton(this);
        layout = (LinearLayout) findViewById(R.id.slider);



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
            searchBar.setDropDownBackgroundResource(R.color.colorPrimaryDark);
            searchBar0.setDropDownBackgroundResource(R.color.colorPrimaryDark);
            showSoftKeyboard(searchBar);
            ArrayAdapter<String> items=new ArrayAdapter<>(SearchActivity.this,android.R.layout.simple_list_item_1,placeNames);

            searchBar0.setAdapter(items);
            searchBar.setAdapter(items);
            searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                    try{
                        findVehicles(searchBar0.getText().toString(), searchBar.getText().toString());
                        layout.addView(requestButton);
                        setRideParams();
                    }catch(Exception e){
                        Toast.makeText(SearchActivity.this,"Not Found!",Toast.LENGTH_SHORT).show();
                        searchBar.clearFocus();
                    }

                }
            });
            searchBar.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View arg0, int arg1, KeyEvent event) {
                    if (arg1==66) {
                        try {
                            findVehicles(searchBar0.getText().toString(), searchBar.getText().toString());
                            layout.addView(requestButton);
                            setRideParams();
                            searchBar.clearFocus();
                        } catch (Exception e) {
                            Toast.makeText(SearchActivity.this, "Not Found!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    if(arg1==KeyEvent.KEYCODE_BACK){
                        finish();
                    }

                    return true;
                }
            });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraIdleListener(onCameraIdleListener);
        try {
            boolean success = googleMap.setMapStyle
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
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            LatLng doyelChattar = new LatLng(23.728014, 90.400323);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(doyelChattar, 11));
            map.getUiSettings().setMyLocationButtonEnabled(false);
            myLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                    map.animateCamera(cameraUpdate);
                }
            });
        }
    }
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (getApplicationContext() != null) {
                        lastLocation = location;
                        if (!flag) {
                            //  setSourceTextMyAddress();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            map.animateCamera(CameraUpdateFactory.zoomTo(15));
                            setSourceTextMyAddress();
                            flag = true;
                        }
                    }
                }
            }
        };




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
    private void setSourceTextMyAddress(){
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            geocodedAddresses=geocoder.getFromLocation(lastLocation.getLatitude(),lastLocation.getLongitude(),1);
            String area=geocodedAddresses.get(0).getSubLocality();
            searchBar0.setText(area);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = map.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(SearchActivity.this);

                try {
                    geocodedAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String area = geocodedAddresses.get(0).getSubLocality();
                    if (slidingUpPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                        searchBar.setText(area);
                }

                 catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private void configureDropDown() {
        setOnMapButton=findViewById(R.id.setOnMapButton);
        dropDown=findViewById(R.id.dropDown);
        dropDownText=findViewById(R.id.dropDownText);
        setOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidingUpPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    dropDown.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    dropDownText.setText("done");
                    hideSoftKeyboard(searchBar);
                    hideSoftKeyboard(searchBar0);
                }
                else {
                    slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    dropDown.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    dropDownText.setText("set on map");
                    findVehicles(searchBar0.getText().toString(), searchBar.getText().toString());
                    hideSoftKeyboard(searchBar);
                    setRideParams();
                }


            }
        });
    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
    private void setRideParams() {
        LatLng pickup=getLocationFromAddress(this,searchBar0.getText().toString()+", dhaka");
        LatLng drop=getLocationFromAddress(this,searchBar.getText().toString()+", dhaka");

        RideParameters rideParams = new RideParameters.Builder()
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                .setDropoffLocation(
                        drop.latitude, drop.longitude, "", "")
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(pickup.latitude, pickup.longitude, "", "").build();
        requestButton.setRideParameters(rideParams);
        ServerTokenSession session = new ServerTokenSession(config);
        requestButton.setSession(session);
        requestButton.loadRideInformation();
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




}


