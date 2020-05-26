package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.alon.client.utils.Element;

public class GardenDetailsActivity extends AppCompatActivity {

    private TextView garden_LBL_name, garden_LBL_location, garden_LBL_active;
    private String name, location;
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
            initDetails();
        }
    }

    private void initDetails() {
        garden_LBL_name.setText(name);
        garden_LBL_location.setText(location);
        garden_LBL_active.setText(active.toString());
    }

    // Method that find all the views by id.
    private void findAll() {
        garden_LBL_name = findViewById(R.id.garden_LBL_name);
        garden_LBL_location = findViewById(R.id.garden_LBL_location);
        garden_LBL_active = findViewById(R.id.garden_LBL_active);
    }
}
