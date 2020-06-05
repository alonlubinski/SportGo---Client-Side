package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alon.client.utils.Constants;
import com.alon.client.utils.Converter;
import com.alon.client.utils.elementUtils.FacilityStatus;
import com.alon.client.utils.elementUtils.FacilityType;
import com.alon.client.utils.elementUtils.MuscaleGroup;
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
import java.util.List;
import java.util.Locale;

public class FacilityDetailsActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button facility_BTN_status;
    private LinearLayout facility_LYT_manager;
    private TextView facility_LBL_name, facility_LBL_address, facility_LBL_location, facility_LBL_active,
                        facility_LBL_description, facility_LBL_type, facility_LBL_mus_group, facility_LBL_status;
    private String id, name, address = null, location, description, userStatus = "free", facilityStatusStr;
    private FacilityStatus facilityStatus;
    private FacilityType facilityType;
    private MuscaleGroup muscaleGroup;
    private Boolean active, sendStatus = false, getStatus = false;
    private Dialog statusDialog;
    private ProgressBar facility_PRB;
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_details);
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
            description = getIntent().getStringExtra("description");
            facilityType = FacilityType.valueOf(getIntent().getStringExtra("type"));
            muscaleGroup = MuscaleGroup.valueOf(getIntent().getStringExtra("mus_group"));
            facilityStatus = FacilityStatus.valueOf(getIntent().getStringExtra("status"));
            initDetails();
        }

        if(user.getUserRole().equals("PLAYER")){
            facility_LYT_manager.setVisibility(View.GONE);
        }

        statusDialog = new Dialog(this);
        createStatusPopupWindow(statusDialog);
        facility_BTN_status.setOnClickListener(this);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
    }

    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                facility_PRB.setVisibility(View.GONE);
                String errorMessage = volleyHelper.handleErrorMessage(error);
                Toast.makeText(FacilityDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                sendStatus = false;
                getStatus = false;
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                if(sendStatus){
                    facility_PRB.setVisibility(View.GONE);
                    Toast.makeText(FacilityDetailsActivity.this, "Thank you for sharing!", Toast.LENGTH_LONG).show();
                    sendStatus = false;
                    getUpdatedStatus();
                } else if(getStatus){
                    try {
                        facilityStatusStr = response.getJSONObject("info_facility").getString("status");
                        facility_LBL_status.setText(Converter.convertFacilityStatus(FacilityStatus.valueOf(facilityStatusStr)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getStatus = false;
                }
            }

            @Override
            public void notifySuccessArray(JSONArray response) {

            }
        };
    }

    // Method that get the updated status of the facility from the server.
    private void getUpdatedStatus() {
        String actionUrl = Constants.URL_PREFIX + "/acs/actions";
        JSONObject jsonBody = new JSONObject();
        jsonBody = createJsonForSend("get_info_facility");
        volleyHelper.postObjectDataVolley(requestQueue, actionUrl, jsonBody);
        getStatus = true;
    }

    // Method that init all the text views with details.
    private void initDetails() {
        facility_LBL_name.setText(name);
        if(address != null) {
            facility_LBL_address.setText(address);
        } else {
            facility_LBL_address.setText("Address not available");
        }
        facility_LBL_location.setText(location);
        facility_LBL_active.setText(active.toString());
        facility_LBL_description.setText(description);
        facility_LBL_type.setText(Converter.convertFacilityType(facilityType));
        facility_LBL_mus_group.setText(Converter.convertMuscaleGroup(muscaleGroup));
        facility_LBL_status.setText(Converter.convertFacilityStatus(facilityStatus));
    }

    // Method that find all the views by id.
    private void findAll() {
        facility_LYT_manager = findViewById(R.id.facility_LYT_manager);
        facility_BTN_status = findViewById(R.id.facility_BTN_status);
        facility_LBL_name = findViewById(R.id.facility_LBL_name);
        facility_LBL_address = findViewById(R.id.facility_LBL_address);
        facility_LBL_location = findViewById(R.id.facility_LBL_location);
        facility_LBL_active = findViewById(R.id.facility_LBL_active);
        facility_LBL_description = findViewById(R.id.facility_LBL_description);
        facility_LBL_type = findViewById(R.id.facility_LBL_type);
        facility_LBL_mus_group = findViewById(R.id.facility_LBL_mus_group);
        facility_LBL_status = findViewById(R.id.facility_LBL_status);
        facility_PRB = findViewById(R.id.facility_PRB);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.facility_BTN_status:
                statusDialog.show();
                break;
        }
    }


    // Method that create the status pop up window.
    private void createStatusPopupWindow(final Dialog dialog) {
        dialog.setContentView(R.layout.status_dialog);
        dialog.setCancelable(true);
        final RadioGroup status_RGP = dialog.findViewById(R.id.status_RGP);
        status_RGP.setOnCheckedChangeListener(this);

        Button status_BTN_confirm,  status_BTN_cancel;
        status_BTN_confirm = dialog.findViewById(R.id.status_BTN_confirm);
        status_BTN_cancel = dialog.findViewById(R.id.status_BTN_cancel);
        status_BTN_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facility_PRB.setVisibility(View.VISIBLE);
                sendStatusToTheServer();
            }
        });

        status_BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    // Method that send updated status of facility to the server.
    private void sendStatusToTheServer() {
        String actionUrl = Constants.URL_PREFIX + "/acs/actions";
        JSONObject jsonBody = new JSONObject();
        jsonBody = createJsonForSend("update_facility_status");
        volleyHelper.postObjectDataVolley(requestQueue, actionUrl, jsonBody);
        statusDialog.dismiss();
        sendStatus = true;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        View radioButton = radioGroup.findViewById(i);
        int index = radioGroup.indexOfChild(radioButton);
        switch(index){
            case 0:
                userStatus = "free";
                break;

            case 1:
                userStatus = "occupied";
                break;

            case 2:
                userStatus = "broken";
                break;
        }
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
            if(type.equals("update_facility_status")) {
                actionAttributes.put("status", userStatus);
            }
            jsonBody.put("actionAttributes", actionAttributes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonBody);
        return jsonBody;
    }
}
