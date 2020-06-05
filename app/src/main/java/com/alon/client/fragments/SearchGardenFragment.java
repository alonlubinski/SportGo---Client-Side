package com.alon.client.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alon.client.utils.Constants;
import com.alon.client.R;
import com.alon.client.utils.elementUtils.Element;
import com.alon.client.utils.LocationUtil;
import com.alon.client.utils.adapters.GardenRecyclerViewAdapter;
import com.alon.client.utils.userUtils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchGardenFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup garden_RGP_group;
    private EditText garden_EDT_value;
    private TextView garden_LBL_results;
    private Button garden_BTN_search;
    private String type = "byName", value, url = Constants.URL_PREFIX + "/acs/elements/" + Constants.DOMAIN;
    private Double distance;
    private RecyclerView garden_RCV_result;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;
    private ArrayList<Element> gardenArrayList = new ArrayList<>();

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
                if(garden_RCV_result != null){
                    garden_RCV_result.removeAllViewsInLayout();
                }
                garden_LBL_results.setVisibility(View.VISIBLE);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {

            }

            @Override
            public void notifySuccessArray(JSONArray response) {
                garden_BTN_search.setClickable(true);
                convertJSONArrayToArrayList(response);
                if(garden_RCV_result != null){
                    garden_RCV_result.removeAllViewsInLayout();
                }
                if(gardenArrayList.size() != 0){
                    if(garden_LBL_results.getVisibility() == View.VISIBLE){
                        garden_LBL_results.setVisibility(View.GONE);
                    }
                    initRecyclerView();
                } else {
                    garden_LBL_results.setVisibility(View.VISIBLE);
                }

            }
        };
    }

    // Method that find all the views by id.
    private void findAll(View view) {
        garden_RGP_group = view.findViewById(R.id.search_RGP_garden);
        garden_EDT_value = view.findViewById(R.id.search_garden_EDT_value);
        garden_BTN_search = view.findViewById(R.id.search_garden_BTN_search);
        garden_RCV_result = view.findViewById(R.id.search_RCV_gardens);
        garden_LBL_results = view.findViewById(R.id.search_garden_LBL_results);
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
                        newUrl = url + "/" + user.getEmail() + "/search/near/" +
                                 user.getLocationUtil().getLat() + "/" + user.getLocationUtil().getLng() + "/" + distance;
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

    // Method that init the recycler view.
    private void initRecyclerView(){
        garden_RCV_result.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        garden_RCV_result.setLayoutManager(layoutManager);
        mAdapter = new GardenRecyclerViewAdapter(gardenArrayList);
        garden_RCV_result.setAdapter(mAdapter);
    }

    // Method that convert json array to array list.
    private void convertJSONArrayToArrayList(JSONArray jsonArray){
        gardenArrayList.clear();
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
                    element.getElementAttributes().put("rating", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").get("rating"));
                    element.getElementAttributes().put("numOfRatedBy", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").get("numOfRatedBy"));
                    System.out.println(jsonArray.getJSONObject(i));
                    gardenArrayList.add(element);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
