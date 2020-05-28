package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import com.alon.client.utils.Element;
import com.alon.client.utils.FacilityRecyclerViewAdapter;
import com.alon.client.utils.LocationUtil;
import com.alon.client.utils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GardenDetailsActivity extends AppCompatActivity {

    private TextView garden_LBL_name, garden_LBL_address, garden_LBL_location, garden_LBL_active;
    private String id, name, address = null, location, url = Constants.URL_PREFIX + "/acs/elements/" + Constants.DOMAIN;
    private Boolean active;
    private RecyclerView garden_RCV_facilities;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Element> facilityArrayList = new ArrayList<>();
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_details);
        user = User.getInstance();
        findAll();
        if(getIntent().getExtras() != null){
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            active = getIntent().getBooleanExtra("active", false);
            String[] locationArr = location.split(", ");
            Double lat = Double.parseDouble(locationArr[0]);
            Double lng = Double.parseDouble(locationArr[1]);
            convertToAddress(lat, lng);
            initDetails();
        }

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
        url += "/" + user.getEmail() + "/" + Constants.DOMAIN + "/" + id + "/children";
        volleyHelper.getArrayDataVolley(requestQueue, url);
    }

    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {

            }

            @Override
            public void notifySuccessObject(JSONObject response) {

            }

            @Override
            public void notifySuccessArray(JSONArray response) {
                convertJSONArrayToArrayList(response);
                if(facilityArrayList.size() != 0){
                    initRecyclerView();
                }
            }
        };
    }

    private void initDetails() {
        garden_LBL_name.setText(name);
        if(address != null) {
            garden_LBL_address.setText(address);
        } else {
            garden_LBL_address.setText("Address not available");
        }
        garden_LBL_location.setText(location);
        garden_LBL_active.setText(active.toString());
    }

    // Method that find all the views by id.
    private void findAll() {
        garden_LBL_name = findViewById(R.id.garden_LBL_name);
        garden_LBL_address = findViewById(R.id.garden_LBL_address);
        garden_LBL_location = findViewById(R.id.garden_LBL_location);
        garden_LBL_active = findViewById(R.id.garden_LBL_active);
        garden_RCV_facilities = findViewById(R.id.garden_RCV_facilities);
    }

    // Method that convert location to address.
    private void convertToAddress(double lat, double lng){
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(!addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Method that init the recycler view.
    private void initRecyclerView(){
        garden_RCV_facilities.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        garden_RCV_facilities.setLayoutManager(layoutManager);
        mAdapter = new FacilityRecyclerViewAdapter(facilityArrayList);
        garden_RCV_facilities.setAdapter(mAdapter);
    }

    // Method that convert json array to array list.
    private void convertJSONArrayToArrayList(JSONArray jsonArray){
        facilityArrayList.clear();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                if(jsonArray.getJSONObject(i).getString("type").equals("Facility")){
                    Element element = new Element();
                    element.setId(jsonArray.getJSONObject(i).getJSONObject("elementId").getString("id"));
                    element.setName(jsonArray.getJSONObject(i).getString("name"));
                    element.setType(jsonArray.getJSONObject(i).getString("type"));
                    element.setActive(jsonArray.getJSONObject(i).getBoolean("active"));
                    element.setLocationUtil(new LocationUtil(
                            jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                            jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lng")));
                    facilityArrayList.add(element);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
