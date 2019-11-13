package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

public class UberHandler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_handler);
        SessionConfiguration config = new SessionConfiguration.Builder()
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

        /// get the context by invoking ``getApplicationContext()``, ``getContext()``, ``getBaseContext()`` or ``this`` when in the activity class
        //RideRequestButton requestButton = new RideRequestButton(UberHandler.this);
        RideRequestButton requestButton=findViewById(R.id.uberButton);
        // get your layout, for instance:
        RelativeLayout layout = new RelativeLayout(UberHandler.this);
        layout.addView(requestButton);

      /* // get the context by invoking ``getApplicationContext()``, ``getContext()``, ``getBaseContext()`` or ``this`` when in the activity class
        RideRequestButton requestButton = new RideRequestButton(MainActivity.this);

        // get your layout, for instance:
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative_layout);
        layout.addView(requestButton);*/

        //need location parameter to implement this items


       /* RideParameters rideParams = new RideParameters.Builder()
                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                .setDropoffLocation(
                        37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")                .build();
                // set parameters for the RideRequestButton instance
        requestButton.setRideParameters(rideParams);


        ServerTokenSession session = new ServerTokenSession(config);
        requestButton.setSession(session);
        requestButton.loadRideInformation();*/

    }
}
