package com.example.molder.footprint;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.molder.footprint.CheckInShare.CheckInShareFragment;

import com.example.molder.footprint.Friends.Friends;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;


public class Home extends AppCompatActivity {


    private BottomNavigationView homeBottomNavigation;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeBottomNavigation = findViewById(R.id.homeBottomNavigation);
        homeNavigationItemSelectedListener();
        fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content,fragment);
        ft.commit();
        initContent();
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
            this.finish();
        }
    }


    //主功能表換頁
    private void homeNavigationItemSelectedListener() {
        homeBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.homeStrokeBtnNavigation:
                        fragment = new ScheduleMainFragment();
                        break;
                    case R.id.homeTakePictureBtnNavigation:
                        fragment = new CheckInShareFragment();
                        break;
                    case R.id.homeFriendsBtnNavigation:
                        fragment = new Friends();
                        break;
                    case R.id.homePersonalBtnNavigation:
                        fragment = new HomeFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                item.setChecked(true);
                changeFragment(fragment);
                return false;
            }
        });
    }

    private void initContent() {
        //設定預選的選項
        homeBottomNavigation.setSelectedItemId(R.id.homeBtnNavigation);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}

