package com.alon.client.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alon.client.utils.Constants;
import com.alon.client.GardenDetailsActivity;
import com.alon.client.R;
import com.alon.client.utils.elementUtils.Element;
import com.alon.client.utils.LocationUtil;
import com.alon.client.utils.userUtils.User;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

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
        url += "/" + user.getEmail() + "/search/byType/Garden?size=20";
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
                String errorMessage = volleyHelper.handleErrorMessage(error);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
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
        if(user.getLocationUtil().getLng() != 0 && user.getLocationUtil().getLat() != 0) {
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(user.getLocationUtil().getLat(), user.getLocationUtil().getLng()), 14));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(user.getLocationUtil().getLat(), user.getLocationUtil().getLng()), 0));
        }
        volleyHelper.getArrayDataVolley(requestQueue, url);
        map.setOnInfoWindowClickListener(this);
    }

    // Method that init all the garden elements to the map.
    private void initElementToMap(GoogleMap map){
        if(!elementArrayList.isEmpty()){
            for(int i = 0; i < elementArrayList.size(); i++){
                String address;
                LatLng latLng = new LatLng(elementArrayList.get(i).getLocationUtil().getLat(),
                        elementArrayList.get(i).getLocationUtil().getLng());
                Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(!addresses.isEmpty()){
                        address = addresses.get(0).getAddressLine(0);
                    } else {
                        address = "Address Not Available";
                    }
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .snippet(address)
                            .title(elementArrayList.get(i).getName()))
                            .setTag(elementArrayList.get(i));
                } catch (IOException e){
                    e.printStackTrace();
                }

            }
        }
    }

    private void convertJSONArrayToArrayList(JSONArray jsonArray){
        for(int i = 0; i < jsonArray.length(); i++){
            Element element = new Element();
            try {
                element.setId(jsonArray.getJSONObject(i).getJSONObject("elementId").getString("id"));
                element.setName(jsonArray.getJSONObject(i).getString("name"));
                element.setType(jsonArray.getJSONObject(i).getString("type"));
                element.setActive(jsonArray.getJSONObject(i).getBoolean("active"));
                element.setLocationUtil(new LocationUtil(
                        jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                        jsonArray.getJSONObject(i).getJSONObject("location").getDouble("lng")));
                element.getElementAttributes().put("rating", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").get("rating"));
                element.getElementAttributes().put("numOfRatedBy", jsonArray.getJSONObject(i).getJSONObject("elementAttributes").getJSONObject("Info").get("numOfRatedBy"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            elementArrayList.add(element);
        }
    }

    // Method that start the garden details activity.
    private void startGardenDetailsActivity(Element garden) {
        Intent intent = new Intent(this.getContext(), GardenDetailsActivity.class);
        intent.putExtra("id", garden.getId());
        intent.putExtra("name", garden.getName());
        intent.putExtra("location", garden.getLocationUtil().getLat() + ", " + garden.getLocationUtil().getLng());
        intent.putExtra("active", garden.getActive());
        intent.putExtra("rating", String.valueOf(garden.getElementAttributes().get("rating")));
        intent.putExtra("numOfRatedBy", String.valueOf(garden.getElementAttributes().get("numOfRatedBy")));
        startActivity(intent);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        startGardenDetailsActivity((Element) marker.getTag());
    }
}
