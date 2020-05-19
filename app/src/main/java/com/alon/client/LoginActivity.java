package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alon.client.utils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText login_EDT_email;
    private Button login_BTN_login, login_BTN_signup;
    private String email, url = Constants.URL_PREFIX + "/acs/users/login";
    private RequestQueue requestQueue;
    private TextView login_LBL_data;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findAll();
        setClickListeners();
        requestQueue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
        user = User.getInstance();
    }

    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                login_LBL_data.setText(error.toString());
                login_BTN_login.setClickable(true);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                login_LBL_data.setText(response.toString());
                login_BTN_login.setClickable(true);
                try {
                    user.login(response.getJSONObject("userId").getString("email"), response.getString("username"), response.getString("avatar"), response.getString("role"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        login_LBL_data = findViewById(R.id.login_LBL_data);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login_BTN_login:
                login_BTN_login.setClickable(false);
                email = login_EDT_email.getText().toString();
                String loginUrl = url + "/" + Constants.DOMAIN + "/" + email;
                volleyHelper.getObjectDataVolley(requestQueue, loginUrl);
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


}
