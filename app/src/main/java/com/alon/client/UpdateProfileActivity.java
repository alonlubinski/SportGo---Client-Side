package com.alon.client;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alon.client.utils.Constants;
import com.alon.client.utils.userUtils.User;
import com.alon.client.volley.VolleyHelper;
import com.alon.client.volley.VolleyResultInterface;
import com.alon.client.volley.VolleySingleton;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText update_EDT_username, update_EDT_avatar;
    private ProgressBar update_profile_PRB;
    private Button update_BTN_confirm;
    private String username, avatar, url = Constants.URL_PREFIX + "/acs/users/" + Constants.DOMAIN;
    private User user;
    private RequestQueue requestQueue;
    private VolleyHelper volleyHelper;
    private VolleyResultInterface volleyResultInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        findAll();
        update_BTN_confirm.setOnClickListener(this);
        user = User.getInstance();
        updateUI();
        requestQueue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        initVolleyInterface();
        volleyHelper = new VolleyHelper(volleyResultInterface);
    }

    private void initVolleyInterface() {
        volleyResultInterface = new VolleyResultInterface() {
            @Override
            public void notifyError(VolleyError error) {
                update_profile_PRB.setVisibility(View.GONE);
                if(error instanceof ParseError){
                    Toast.makeText(UpdateProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    user.setUsername(username);
                    user.setAvatar(avatar);
                    finish();
                } else{
                    String errorMessage = volleyHelper.handleErrorMessage(error);
                    Toast.makeText(UpdateProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    update_BTN_confirm.setClickable(true);
                }
            }

            @Override
            public void notifySuccessObject(JSONObject response) {
                update_profile_PRB.setVisibility(View.GONE);
                Toast.makeText(UpdateProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void notifySuccessArray(JSONArray response) {

            }
        };
    }

    // Method that init the values of the fields in the db.
    private void updateUI() {
        update_EDT_username.setText(user.getUsername());
        update_EDT_avatar.setText(user.getAvatar());
    }

    // Method that find all the views by id.
    private void findAll() {
        update_EDT_username = findViewById(R.id.update_EDT_username);
        update_EDT_avatar = findViewById(R.id.update_EDT_avatar);
        update_profile_PRB = findViewById(R.id.update_profile_PRB);
        update_BTN_confirm = findViewById(R.id.update_BTN_confirm);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.update_BTN_confirm:
                update_BTN_confirm.setClickable(false);
                if(checkInputValidation()){
                    update_profile_PRB.setVisibility(View.VISIBLE);
                    username = update_EDT_username.getText().toString();
                    avatar = update_EDT_avatar.getText().toString();
                    url += "/" + user.getEmail();
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("username", username);
                        jsonBody.put("avatar", avatar);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    volleyHelper.putObjectDataVolley(requestQueue, url, jsonBody);
                } else {
                    update_BTN_confirm.setClickable(true);
                }
                break;
        }
    }

    // Method that check if the inputs are not empty.
    private boolean checkInputValidation(){
        if(update_EDT_username.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter username!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(update_EDT_avatar.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter avatar!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
