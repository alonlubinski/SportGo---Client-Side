package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alon.client.utils.Constants;
import com.alon.client.utils.elementUtils.Element;
import com.alon.client.utils.adapters.FacilityRecyclerViewAdapter;
import com.alon.client.utils.LocationUtil;
import com.alon.client.utils.userUtils.User;
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

public class GardenDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout garden_LYT_manager;
    private TextView garden_LBL_name, garden_LBL_address, garden_LBL_location, garden_LBL_active, garden_LBL_rating;
    private String id, name, address = null, location, ratedBy, url = Constants.URL_PREFIX + "/acs/elements/" + Constants.DOMAIN;
    private Boolean active, sendRating = false, getRating = false;
    private int usersVote;
    private Double rating;
    private Button garden_BTN_vote;
    private RecyclerView garden_RCV_facilities;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Element> facilityArrayList = new ArrayList<>();
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;
    private Dialog rankDialog;
    private ProgressBar garden_PRB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_details);
        user = User.getInstance();
        findAll();
        rankDialog = new Dialog(this);
        createRatingPopupWindow(rankDialog);
        garden_BTN_vote.setOnClickListener(this);
        if(getIntent().getExtras() != null){
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            active = getIntent().getBooleanExtra("active", false);
            String[] locationArr = location.split(", ");
            Double lat = Double.parseDouble(locationArr[0]);
            Double lng = Double.parseDouble(locationArr[1]);
            convertToAddress(lat, lng);
            rating = Double.parseDouble(getIntent().getStringExtra("rating"));
            ratedBy = getIntent().getStringExtra("numOfRatedBy");
            initDetails();
        }


        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
        if(user.getUserRole().equals("PLAYER")) {
            garden_LYT_manager.setVisibility(View.GONE);
            getUpdatedRating();
        }
        url += "/" + user.getEmail() + "/" + Constants.DOMAIN + "/" + id + "/children";
        volleyHelper.getArrayDataVolley(requestQueue, url);

    }

    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                garden_PRB.setVisibility(View.GONE);
                String errorMessage = volleyHelper.handleErrorMessage(error);
                Toast.makeText(GardenDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                if(sendRating) {
                    garden_PRB.setVisibility(View.GONE);
                    Toast.makeText(GardenDetailsActivity.this, "Thank you for voting!", Toast.LENGTH_LONG).show();
                    sendRating = false;
                    getUpdatedRating();
                } else if (getRating){
                    try {
                        rating = response.getJSONObject("info_garden").getDouble("rating");
                        ratedBy = String.valueOf(response.getJSONObject("info_garden").getInt("numOfRatedBy"));
                        garden_LBL_rating.setText(String.format("%.1f / 5.0 ", rating) + "(Rated by " + ratedBy + " people)");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getRating = false;
                }
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

    private void getUpdatedRating() {
        String actionUrl = Constants.URL_PREFIX + "/acs/actions";
        JSONObject jsonBody = new JSONObject();
        jsonBody = createJsonForSend("get_info_garden");
        volleyHelper.postObjectDataVolley(requestQueue, actionUrl, jsonBody);
        getRating = true;
    }

    private void initDetails() {
        garden_LBL_name.setText(name);
        if(address != null) {
            garden_LBL_address.setText(address);
        } else {
            garden_LBL_address.setText("Address not available");
        }
        if(!user.getUserRole().equals("PLAYER")){
            garden_LBL_rating.setText(String.format("%.1f / 5.0 ", rating) + "(Rated by " + ratedBy + " people)");
        }
        garden_LBL_location.setText(location);
        garden_LBL_active.setText(active.toString());
    }

    // Method that find all the views by id.
    private void findAll() {
        garden_LYT_manager = findViewById(R.id.garden_LYT_manager);
        garden_BTN_vote = findViewById(R.id.garden_BTN_vote);
        garden_LBL_name = findViewById(R.id.garden_LBL_name);
        garden_LBL_address = findViewById(R.id.garden_LBL_address);
        garden_LBL_location = findViewById(R.id.garden_LBL_location);
        garden_LBL_active = findViewById(R.id.garden_LBL_active);
        garden_RCV_facilities = findViewById(R.id.garden_RCV_facilities);
        garden_LBL_rating = findViewById(R.id.garden_LBL_rating);
        garden_PRB = findViewById(R.id.garden_PRB);
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
                    element.getElementAttributes().put("description", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").getString("description"));
                    element.getElementAttributes().put("type", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").get("type"));
                    element.getElementAttributes().put("status", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").get("status"));
                    element.getElementAttributes().put("mus_group", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").get("mus_group"));
                    facilityArrayList.add(element);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.garden_BTN_vote:
                rankDialog.show();
                break;
        }
    }

    // Method that create the rating pop up window.
    private void createRatingPopupWindow(final Dialog dialog) {
        dialog.setContentView(R.layout.rating_dialog);
        dialog.setCancelable(true);
        final RatingBar rating_RGB = dialog.findViewById(R.id.rating_RGB);

        Button rating_BTN_confirm,  rating_BTN_cancel;
        rating_BTN_confirm = dialog.findViewById(R.id.rating_BTN_confirm);
        rating_BTN_cancel = dialog.findViewById(R.id.rating_BTN_cancel);
        rating_BTN_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                garden_PRB.setVisibility(View.VISIBLE);
                usersVote = (int) rating_RGB.getRating();
                sendRatingToTheServer();
            }
        });

        rating_BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void sendRatingToTheServer(){
        String actionUrl = Constants.URL_PREFIX + "/acs/actions";
        JSONObject jsonBody = new JSONObject();
        jsonBody = createJsonForSend("update_rating");
        volleyHelper.postObjectDataVolley(requestQueue, actionUrl, jsonBody);
        rankDialog.dismiss();
        sendRating = true;

    }

    // Method that create json to send to the server.
    private JSONObject createJsonForSend(String type){
        JSONObject jsonBody = new JSONObject();
        JSONObject userId = new JSONObject();
        JSONObject invokedBy = new JSONObject();
        JSONObject elementId = new JSONObject();
        JSONObject element = new JSONObject();
        JSONObject actionAttributes = new JSONObject();
        try {
            userId.put("domain", Constants.DOMAIN);
            userId.put("email", user.getEmail());
            invokedBy.put("userId", userId);
            jsonBody.put("invokedBy", invokedBy);
            elementId.put("domain", Constants.DOMAIN);
            elementId.put("id", id);
            element.put("elementId", elementId);
            jsonBody.put("element", element);
            jsonBody.put("type", type);
            if(type.equals("update_rating")) {
                actionAttributes.put("rating", usersVote);
            }
            jsonBody.put("actionAttributes", actionAttributes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonBody);
        return jsonBody;
    }
}
