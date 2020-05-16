package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.alon.client.utils.VolleySingleton;
import com.alon.client.utils.NewUserDetails;
import com.alon.client.utils.UserRole;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signup_EDT_email, signup_EDT_username, signup_EDT_avatar;
    private Button signup_BTN_signup;
    private String email, username, avatar, url = Constants.URL_PREFIX + "/acs/users";
    private RequestQueue requestQueue;
    private TextView signup_LBL_data;
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
                signup_LBL_data.setText(error.toString());
                signup_BTN_signup.setClickable(true);
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                signup_LBL_data.setText(response.toString());
                signup_BTN_signup.setClickable(true);
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
        signup_LBL_data = findViewById(R.id.signup_LBL_data);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.signup_BTN_signup:
                signup_BTN_signup.setClickable(false);
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
                } catch (JSONException e){
                    e.printStackTrace();
                }
                volleyHelper.postObjectDataVolley(requestQueue, url, jsonBody);
//                JsonObjectRequest jsonObjectRequestSignUp = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        signup_LBL_data.setText(response.toString());
//                        signup_BTN_signup.setClickable(true);
//                        finish();
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        signup_LBL_data.setText(error.toString());
//                        signup_BTN_signup.setClickable(true);
//                    }
//                });
//                jsonObjectRequestSignUp.setRetryPolicy(new DefaultRetryPolicy(
//                        5000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                requestQueue.add(jsonObjectRequestSignUp);
                break;
        }
    }

}
