package com.alon.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.alon.client.fragments.AddElementFragment;
import com.alon.client.fragments.HomeFragment;
import com.alon.client.fragments.ProfileFragment;
import com.alon.client.fragments.SearchFacilityFragment;
import com.alon.client.fragments.SearchGardenFragment;
import com.alon.client.utils.userUtils.User;
import com.alon.client.utils.userUtils.UserRole;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private User user;
    private View navHeader;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findAll();

        user = User.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        if(user.getUserRole().equals(UserRole.PLAYER.toString())){
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            getSupportFragmentManager().beginTransaction().add(R.id.main_FL, new HomeFragment()).commit();
        } else if(user.getUserRole().equals(UserRole.MANAGER.toString())){
            navigationView.getMenu().getItem(5).setVisible(false);
            getSupportFragmentManager().beginTransaction().add(R.id.main_FL, new HomeFragment()).commit();
        } else if(user.getUserRole().equals(UserRole.ADMIN.toString())){
            navigationView.getMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
            getSupportFragmentManager().beginTransaction().add(R.id.main_FL, new ProfileFragment()).commit();
        }



    }

    // Find all views by id.
    private void findAll() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.main_DL);
        navigationView = findViewById(R.id.main_NV);
        navHeader = navigationView.getHeaderView(0);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                fragment = new HomeFragment();
                break;

            case R.id.menu_profile:
                fragment = new ProfileFragment();
                break;

            case R.id.menu_search_garden:
                fragment = new SearchGardenFragment();
                break;

            case R.id.menu_search_facility:
                fragment = new SearchFacilityFragment();
                break;

            case R.id.menu_add_element:
                fragment = new AddElementFragment();
                break;
        }
        if(fragment != null){
            changeFragment(fragment, item);
        }
        return false;
    }

    // Method that change the layout to the chosen fragment.
    private void changeFragment(Fragment fragment, MenuItem item){
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_FL, fragment).commit();
            setTitle(item.getTitle());
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
