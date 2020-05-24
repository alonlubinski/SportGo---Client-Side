package com.alon.client.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alon.client.Constants;
import com.alon.client.R;
import com.alon.client.utils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;


public class SearchGardenFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup garden_RGP_group;
    private EditText garden_EDT_value;
    private Button garden_BTN_search;
    private String type = "byName", value, url = Constants.URL_PREFIX + "/acs/elements/" + Constants.DOMAIN;
    private Double distance;
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;

    public SearchGardenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_garden, container, false);
        findAll(view);
        garden_RGP_group.setOnCheckedChangeListener(this);
        garden_BTN_search.setOnClickListener(this);

        user = User.getInstance();
        requestQueue = VolleySingleton.getInstance(this.getContext()).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
        return view;
    }

    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                garden_BTN_search.setClickable(true);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {

            }

            @Override
            public void notifySuccessArray(JSONArray response) {
                garden_BTN_search.setClickable(true);
            }
        };
    }

    // Method that find all the views by id.
    private void findAll(View view) {
        garden_RGP_group = view.findViewById(R.id.search_RGP_garden);
        garden_EDT_value = view.findViewById(R.id.search_garden_EDT_value);
        garden_BTN_search = view.findViewById(R.id.search_garden_BTN_search);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.search_garden_BTN_search:
                garden_BTN_search.setClickable(false);
                String newUrl;
                value = garden_EDT_value.getText().toString();
                if(checkInputValidation()){
                    if(type.equals("byName")){
                        newUrl = url + "/" + user.getEmail() +"/search/byName/" + value;
                    } else {
                        distance = Double.parseDouble(value);
                        newUrl = url + "/" + user.getEmail() + "/search/near/32.071012/34.831162/" + distance;
                    }
                    volleyHelper.getArrayDataVolley(requestQueue, newUrl);
                } else {
                    garden_BTN_search.setClickable(true);
                }
                break;
        }
    }

    // Method that check if the inputs validation.
    private boolean checkInputValidation(){
        if(garden_EDT_value.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), "Please enter search value!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        View radioButton = radioGroup.findViewById(i);
        int index = radioGroup.indexOfChild(radioButton);
        garden_EDT_value.setText("");
        switch(index){
            case 0:
                garden_EDT_value.setInputType(InputType.TYPE_CLASS_TEXT);
                type = "byName";
                break;

            case 1:
                garden_EDT_value.setInputType(InputType.TYPE_CLASS_NUMBER);
                type = "near";
                break;
        }
    }
}
