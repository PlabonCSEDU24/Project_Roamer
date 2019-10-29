package com.example.roamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DataUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameEditTextId,vehicleNameEditTextId,vehicleTypeEditTextId,routeListEditTextId;
    private Button sendDataButtonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update);

        nameEditTextId = findViewById(R.id.nameEditTextId);
        vehicleNameEditTextId = findViewById(R.id.vehicleNameEditTextId);
        vehicleTypeEditTextId = findViewById(R.id.vehicleTypeEditTextId);
        routeListEditTextId = findViewById(R.id.routeListEditTextId);
        sendDataButtonId = findViewById(R.id.sendDataButtonId);

        sendDataButtonId.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(R.id.sendDataButtonId == view.getId()){
            String name = nameEditTextId.getText().toString();
            String vehicleName = vehicleNameEditTextId.getText().toString();
            String vehicleType = vehicleTypeEditTextId.getText().toString();
            String routeList = routeListEditTextId.getText().toString();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"abusufian18121@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "ROMER's data needs to update");
            i.putExtra(Intent.EXTRA_TEXT   , "User Name: \n\n"+ name + "\n\n Vehicle Name: \n\n"+ vehicleName +"\n\n Vehicle Type: \n\n"+ vehicleType + "\n\n Route Lists: \n\n" + routeList);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(DataUpdateActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
            nameEditTextId.setText("");
            vehicleNameEditTextId.setText("");
            vehicleTypeEditTextId.setText("");
            routeListEditTextId.setText("");
        }
    }
}
