package com.alon.client.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.alon.client.Constants;
import com.alon.client.R;
import com.alon.client.utils.Location;
import com.alon.client.utils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AddElementFragment extends Fragment implements View.OnClickListener {

    private Button element_BTN_add;
    private String type, name, url = Constants.URL_PREFIX + "/acs/elements/" + Constants.DOMAIN;
    private Boolean active;
    private Double lat, lng;
    private Location location;
    private AppCompatCheckBox element_CBX_garden, element_CBX_facility;
    private EditText element_EDT_name, element_EDT_latitude, element_EDT_longitude;
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;

    public AddElementFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_element, container, false);
        findAll(view);
        element_BTN_add.setOnClickListener(this);
        element_CBX_garden.setChecked(true);

        user = User.getInstance();
        url += "/" + user.getEmail();
        requestQueue = VolleySingleton.getInstance(this.getContext()).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
        return view;
    }

    // Method that init the volley interface.
    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                element_BTN_add.setClickable(true);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                Toast.makeText(getContext(), "Element add to db!", Toast.LENGTH_LONG).show();
                element_CBX_garden.setChecked(true);
                element_CBX_facility.setChecked(false);
                element_EDT_name.setText("");
                element_EDT_latitude.setText("");
                element_EDT_longitude.setText("");
                element_BTN_add.setClickable(true);
            }

            @Override
            public void notifySuccessArray(JSONArray response) {

            }
        };
    }

    // Method that find all the views by id.
    private void findAll(View view) {
        element_BTN_add = view.findViewById(R.id.element_BTN_add);
        element_CBX_garden = view.findViewById(R.id.element_CBX_garden);
        element_CBX_facility = view.findViewById(R.id.element_CBX_facility);
        element_EDT_name = view.findViewById(R.id.element_EDT_name);
        element_EDT_latitude = view.findViewById(R.id.element_EDT_latitude);
        element_EDT_longitude = view.findViewById(R.id.element_EDT_longitude);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.element_BTN_add:
                element_BTN_add.setClickable(false);
                if(checkInputValidation()){
                    if(element_CBX_garden.isChecked()){
                        type = element_CBX_garden.getText().toString();
                    } else {
                        type = element_CBX_facility.getText().toString();
                    }
                    name = element_EDT_name.getText().toString();
                    active = true;
                    lat = Double.parseDouble(element_EDT_latitude.getText().toString());
                    lng = Double.parseDouble(element_EDT_longitude.getText().toString());


                    JSONObject jsonBody = new JSONObject();
                    JSONObject locationJson = new JSONObject();
                    try {
                        locationJson.put("lat", lat);
                        locationJson.put("lng", lng);
                        jsonBody.put("type", type);
                        jsonBody.put("name", name);
                        jsonBody.put("active", active);
                        jsonBody.put("location", locationJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    volleyHelper.postObjectDataVolley(requestQueue, url, jsonBody);

                } else {
                    element_BTN_add.setClickable(true);
                }
                break;

                //TODO it should be possible to choose only one but it don't work
            case R.id.element_CBX_garden:
                if(element_CBX_facility.isChecked()) {
                    element_CBX_facility.setChecked(false);
                    element_CBX_garden.setChecked(true);
                }
                break;

            case R.id.element_CBX_facility:
                if(element_CBX_garden.isChecked()) {
                    element_CBX_garden.setChecked(false);
                    element_CBX_facility.setChecked(true);
                }
                break;
        }
    }


    // Method that check if the inputs are not empty.
    private boolean checkInputValidation(){
        if(element_EDT_name.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), "Please enter element name!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(element_EDT_latitude.getText().toString().trim().isEmpty() || element_EDT_longitude.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), "Please enter full location!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
