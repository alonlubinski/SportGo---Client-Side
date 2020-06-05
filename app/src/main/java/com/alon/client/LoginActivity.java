package com.alon.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alon.client.utils.Constants;
import com.alon.client.utils.LocationUtil;
import com.alon.client.utils.userUtils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText login_EDT_email;
    private Button login_BTN_login, login_BTN_signup;
    private String email, url = Constants.URL_PREFIX + "/acs/users/login";
    private ProgressBar login_PRB;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;
    private User user;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = User.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermissions();

        findAll();
        setClickListeners();
        requestQueue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);

    }

    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                login_PRB.setVisibility(View.GONE);
                String errorMessage = volleyHelper.handleErrorMessage(error);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                login_BTN_login.setClickable(true);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                login_BTN_login.setClickable(true);
                try {
                    user.login(response.getJSONObject("userId").getString("email"), response.getString("username"), response.getString("avatar"), response.getString("role"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                login_PRB.setVisibility(View.GONE);
                startMainActivity();
            }

            @Override
            public void notifySuccessArray(JSONArray response) {

            }
        };
    }

    // Set click listeners.
    private void setClickListeners() {
        login_BTN_login.setOnClickListener(this);
        login_BTN_signup.setOnClickListener(this);
    }

    // Find all views by id.
    private void findAll() {
        login_EDT_email = findViewById(R.id.login_EDT_email);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_signup = findViewById(R.id.login_BTN_signup);
        login_PRB = findViewById(R.id.login_PRB);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login_BTN_login:
                login_BTN_login.setClickable(false);
                if(checkInputValidation()) {
                    login_PRB.setVisibility(View.VISIBLE);
                    email = login_EDT_email.getText().toString();
                    String loginUrl = url + "/" + Constants.DOMAIN + "/" + email;
                    volleyHelper.getObjectDataVolley(requestQueue, loginUrl);
                } else {
                    login_BTN_login.setClickable(true);
                }
                break;

            case R.id.login_BTN_signup:
                startSignUpActivity();
                break;
        }
    }

    // Method that start the main activity.
    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // Method that start the sign up activity.
    public void startSignUpActivity(){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    // Method that check if the inputs are not empty.
    private boolean checkInputValidation(){
        if(login_EDT_email.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Method that check if users granted location permissions.
    private void checkLocationPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Permissions granted
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                } else {
                    checkLocationPermissions();
                }
                return;
        }
    }

    // Method that get the location of the user.
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location  = task.getResult();
                if(location != null){
                    user.setLocationUtil(new LocationUtil());
                    user.getLocationUtil().setLat(location.getLatitude());
                    user.getLocationUtil().setLng(location.getLongitude());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        login_EDT_email.setText("");
    }
}
