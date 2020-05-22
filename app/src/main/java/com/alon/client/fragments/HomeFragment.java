package com.alon.client.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alon.client.Constants;
import com.alon.client.R;
import com.alon.client.utils.Element;
import com.alon.client.utils.Location;
import com.alon.client.utils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;
    private String url = Constants.URL_PREFIX + "/acs/elements/" + Constants.DOMAIN;
    private ArrayList<Element> elementArrayList = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();
        requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
        url += "/" + user.getEmail() + "/search/byType/Garden";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_map);
        supportMapFragment.getMapAsync(this);

        return view;
    }

    // Method that init the volley interface.
    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void notifySuccessObject(JSONObject response) {

            }

            @Override
            public void notifySuccessArray(JSONArray response) {
                convertJSONArrayToArrayList(response);
                initElementToMap(map);
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.4117257, 35.0818155), 7));
       // map.setMyLocationEnabled(true);
        volleyHelper.getArrayDataVolley(requestQueue, url);
    }

    // Method that init all the garden elements to the map.
    private void initElementToMap(GoogleMap map){
        if(!elementArrayList.isEmpty()){
            for(int i = 0; i < elementArrayList.size(); i++){
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(elementArrayList.get(i).getLocation().getLat(),
                                             elementArrayList.get(i).getLocation().getLng())))
                        .setTitle(elementArrayList.get(i).getName());
            }
        }
    }

    private void convertJSONArrayToArrayList(JSONArray jsonArray){
        for(int i = 0; i < jsonArray.length(); i++){
            Element element = new Element();
            try {
                element.setId(jsonArray.getJSONObject(i).getString("elementId"));
                element.setName(jsonArray.getJSONObject(i).getString("name"));
                element.setType(jsonArray.getJSONObject(i).getString("type"));
                element.setActive(jsonArray.getJSONObject(i).getBoolean("active"));
                element.setLocation(new Location(
                        jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                        jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lng")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            elementArrayList.add(element);
        }
    }
}
