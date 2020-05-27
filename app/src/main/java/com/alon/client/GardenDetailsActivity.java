package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import com.alon.client.utils.Element;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GardenDetailsActivity extends AppCompatActivity {

    private TextView garden_LBL_name, garden_LBL_address, garden_LBL_location, garden_LBL_active;
    private String name, address, location;
    private Boolean active;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_details);
        findAll();
        if(getIntent().getExtras() != null){
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            active = getIntent().getBooleanExtra("active", false);
            String[] locationArr = location.split(", ");
            Double lat = Double.parseDouble(locationArr[0]);
            Double lng = Double.parseDouble(locationArr[1]);
            convertToAddress(lat, lng);
            initDetails();
        }
    }

    private void initDetails() {
        garden_LBL_name.setText(name);
        garden_LBL_address.setText(address);
        garden_LBL_location.setText(location);
        garden_LBL_active.setText(active.toString());
    }

    // Method that find all the views by id.
    private void findAll() {
        garden_LBL_name = findViewById(R.id.garden_LBL_name);
        garden_LBL_address = findViewById(R.id.garden_LBL_address);
        garden_LBL_location = findViewById(R.id.garden_LBL_location);
        garden_LBL_active = findViewById(R.id.garden_LBL_active);
    }

    // Method that convert location to address.
    private void convertToAddress(double lat, double lng){
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
