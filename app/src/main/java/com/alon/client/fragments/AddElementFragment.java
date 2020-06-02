package com.alon.client.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alon.client.Constants;
import com.alon.client.R;
import com.alon.client.utils.Element;
import com.alon.client.utils.LocationUtil;
import com.alon.client.utils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AddElementFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button element_BTN_add;
    private String type = "Garden", name, url = Constants.URL_PREFIX + "/acs/elements/" + Constants.DOMAIN;
    private Boolean active, put = false;
    private Double lat, lng;
    private RadioGroup element_RGP;
    private EditText element_EDT_name, element_EDT_latitude, element_EDT_longitude;
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;
    private ArrayList<Element> elementArrayList = new ArrayList<>();
    private Element parent;

    public AddElementFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_element, container, false);
        findAll(view);
        element_RGP.setOnCheckedChangeListener(this);
        element_BTN_add.setOnClickListener(this);

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
                if(error instanceof ParseError){
                    Toast.makeText(getContext(), "Element add to db!", Toast.LENGTH_LONG).show();
                    element_EDT_name.setText("");
                    element_EDT_latitude.setText("");
                    element_EDT_longitude.setText("");
                    put = false;
                    element_BTN_add.setClickable(true);
                } else {
                    String errorMessage = volleyHelper.handleErrorMessage(error);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
                element_BTN_add.setClickable(true);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                if(type.equals("Facility") && !put){
                    put = true;
                    try{
                    String childId = response.getJSONObject("elementId").getString("id");
                    String newUrl = url + "/" + Constants.DOMAIN + "/" + parent.getId() + "/children";
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("domain", Constants.DOMAIN);
                    jsonBody.put("id", childId);
                    volleyHelper.putObjectDataVolley(requestQueue, newUrl, jsonBody);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Element add to db!", Toast.LENGTH_LONG).show();
                    element_EDT_name.setText("");
                    element_EDT_latitude.setText("");
                    element_EDT_longitude.setText("");
                    element_BTN_add.setClickable(true);
                }
            }

            @Override
            public void notifySuccessArray(JSONArray response) {
                convertJSONArrayToArrayList(response);
                if(elementArrayList.isEmpty()){
                    Toast.makeText(getContext(), "No garden in this location!", Toast.LENGTH_LONG).show();
                    element_BTN_add.setClickable(true);
                } else {
                    for(int i = 0; i < elementArrayList.size(); i++){
                        if(elementArrayList.get(i).getType().equals("Garden")){
                            parent = elementArrayList.get(i);
                            JSONObject jsonBody = createJsonForSend();
                            volleyHelper.postObjectDataVolley(requestQueue, url, jsonBody);
                            break;
                        } else if(i == elementArrayList.size() - 1){
                            Toast.makeText(getContext(), "No garden in this location!", Toast.LENGTH_LONG).show();
                            element_BTN_add.setClickable(true);
                        }
                    }
                }

            }
        };
    }

    // Method that find all the views by id.
    private void findAll(View view) {
        element_BTN_add = view.findViewById(R.id.element_BTN_add);
        element_RGP = view.findViewById(R.id.add_element_RGP);
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
                    JSONObject jsonBody = createJsonForSend();
                    if(type.equals("Facility")){
                        String newUrl = url + "/search/near/" + lat + "/" + lng + "/" + 0;
                        volleyHelper.getArrayDataVolley(requestQueue, newUrl);
                    } else {
                        volleyHelper.postObjectDataVolley(requestQueue, url, jsonBody);
                    }
                } else {
                    element_BTN_add.setClickable(true);
                }
                break;
        }
    }

    // Method that create json to send to the server.
    private JSONObject createJsonForSend(){
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
        return jsonBody;
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        View radioButton = radioGroup.findViewById(i);
        int index = radioGroup.indexOfChild(radioButton);
        switch(index){
            case 0:
                type = "Garden";
                break;

            case 1:
                type = "Facility";
                break;
        }
    }

    // Method that convert json array to array list.
    private void convertJSONArrayToArrayList(JSONArray jsonArray){
        elementArrayList.clear();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                if(jsonArray.getJSONObject(i).getString("type").equals("Garden")){
                    Element element = new Element();
                    element.setId(jsonArray.getJSONObject(i).getJSONObject("elementId").getString("id"));
                    element.setName(jsonArray.getJSONObject(i).getString("name"));
                    element.setType(jsonArray.getJSONObject(i).getString("type"));
                    element.setActive(jsonArray.getJSONObject(i).getBoolean("active"));
                    element.setLocationUtil(new LocationUtil(
                            jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                            jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lng")));
                    elementArrayList.add(element);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
