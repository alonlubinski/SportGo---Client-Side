package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alon.client.utils.Constants;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.alon.client.utils.userUtils.NewUserDetails;
import com.alon.client.utils.userUtils.UserRole;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signup_EDT_email, signup_EDT_username, signup_EDT_avatar;
    private Button signup_BTN_signup;
    private ProgressBar signup_PRB;
    private String email, username, avatar, url = Constants.URL_PREFIX + "/acs/users";
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
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
                signup_PRB.setVisibility(View.GONE);
                String errorMessage = volleyHelper.handleErrorMessage(error);
                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                signup_BTN_signup.setClickable(true);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                signup_BTN_signup.setClickable(true);
                signup_PRB.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void notifySuccessArray(JSONArray response) {

            }
        };
    }

    // Set click listeners.
    private void setClickListeners() {
        signup_BTN_signup.setOnClickListener(this);
    }

    // Find all views by id.
    private void findAll() {
        signup_EDT_email = findViewById(R.id.signup_EDT_email);
        signup_EDT_username = findViewById(R.id.signup_EDT_username);
        signup_EDT_avatar = findViewById(R.id.signup_EDT_avatar);
        signup_BTN_signup = findViewById(R.id.signup_BTN_signup);
        signup_PRB = findViewById(R.id.signup_PRB);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.signup_BTN_signup:
                signup_BTN_signup.setClickable(false);
                if(checkInputValidation()) {
                    signup_PRB.setVisibility(View.VISIBLE);
                    email = signup_EDT_email.getText().toString();
                    username = signup_EDT_username.getText().toString();
                    avatar = signup_EDT_avatar.getText().toString();
                    NewUserDetails newUserDetails = new NewUserDetails(UserRole.PLAYER, username, email, avatar);
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("role", newUserDetails.getRole());
                        jsonBody.put("username", newUserDetails.getUserName());
                        jsonBody.put("email", newUserDetails.getEmail());
                        jsonBody.put("avatar", newUserDetails.getAvatar());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    volleyHelper.postObjectDataVolley(requestQueue, url, jsonBody);
                } else {
                    signup_BTN_signup.setClickable(true);
                }
                break;
        }
    }


    // Method that check if the inputs are not empty.
    private boolean checkInputValidation(){
        if(signup_EDT_username.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter your username!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(signup_EDT_email.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_LONG).show();
            return false;
        } else if(signup_EDT_avatar.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter your avatar!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
