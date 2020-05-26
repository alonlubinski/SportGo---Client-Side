package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class FacilityDetailsActivity extends AppCompatActivity {

    private TextView facility_LBL_name, facility_LBL_garden, facility_LBL_location, facility_LBL_active;
    private String name, garden, location;
    private Boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_details);
        findAll();
        if(getIntent().getExtras() != null){
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            active = getIntent().getBooleanExtra("active", false);
            initDetails();
        }
    }

    private void initDetails() {
        facility_LBL_name.setText(name);
        facility_LBL_location.setText(location);
        facility_LBL_active.setText(active.toString());
    }

    // Method that find all the views by id.
    private void findAll() {
        facility_LBL_name = findViewById(R.id.facility_LBL_name);
        facility_LBL_garden = findViewById(R.id.facility_LBL_garden);
        facility_LBL_location = findViewById(R.id.facility_LBL_location);
        facility_LBL_active = findViewById(R.id.facility_LBL_active);
    }
}
