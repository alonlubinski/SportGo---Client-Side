package com.alon.client.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alon.client.R;
import com.alon.client.UpdateProfileActivity;
import com.alon.client.utils.User;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView profile_LBL_username, profile_LBL_email, profile_LBL_avatar, profile_LBL_role;
    private Button profile_BTN_update;
    private Boolean update = false;
    private User user;

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findAll(view);
        user = User.getInstance();
        updateUI();
        profile_BTN_update.setOnClickListener(this);
        return view;
    }

    // Method that update the ui with the user information.
    private void updateUI() {
        profile_LBL_username.setText(user.getUsername());
        profile_LBL_email.setText(user.getEmail());
        profile_LBL_avatar.setText(user.getAvatar());
        profile_LBL_role.setText(user.getUserRole());
    }

    // Method that find all the views by id.
    private void findAll(View view) {
        profile_LBL_username = view.findViewById(R.id.profile_LBL_username);
        profile_LBL_email = view.findViewById(R.id.profile_LBL_email);
        profile_LBL_avatar = view.findViewById(R.id.profile_LBL_avatar);
        profile_LBL_role = view.findViewById(R.id.profile_LBL_role);
        profile_BTN_update = view.findViewById(R.id.profile_BTN_update);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.profile_BTN_update:
                openUpdateProfileActivity();
                update = true;
                break;
        }
    }

    // Method that start the update profile activity.
    private void openUpdateProfileActivity() {
        Intent intent = new Intent(ProfileFragment.super.getActivity(), UpdateProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(update = true){
            updateUI();
            update = false;
        }
    }
}
