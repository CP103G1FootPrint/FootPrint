package com.example.molder.footprint;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class Home extends AppCompatActivity {

    private Toolbar homeToolbar;
    private TabLayout hometabLayout;
    private ViewPager homeViewPager;
    private PagerAdapter homePagerAdapter;
    private TabItem homeTabNews, homeTabStroke, homeTabMap;
    private BottomNavigationView homeBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        handleViews();
        homeTabSelectedListener();
        homeNavigationItemSelectedListener();
    }

    //initial 初始化
    private void handleViews() {

        homeToolbar = findViewById(R.id.homeToolbar);
        //toolbar title設定
        homeToolbar.setTitle("");
        setSupportActionBar(homeToolbar);

        hometabLayout = findViewById(R.id.homeTabLayout);
        homeViewPager = findViewById(R.id.homeViewPager);
        homeTabNews = findViewById(R.id.homeTabNews);
        homeTabStroke = findViewById(R.id.homeTabStroke);
        homeTabMap = findViewById(R.id.homeTabMap);

        //取得FragmentManager權限 並取得目前分頁所在的頁數
        homePagerAdapter = new HomePageAdapter(getSupportFragmentManager(), hometabLayout.getTabCount());
        //將剛剛取到的分頁所在的頁數 顯示在Fragment上
        homeViewPager.setAdapter(homePagerAdapter);

        homeBottomNavigation = findViewById(R.id.homeBottomNavigation);


    }

    //分頁選項監聽器 (分頁換頁)
    private void homeTabSelectedListener() {
        hometabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //取得使用者點擊的分頁頁數
                homeViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //刷新 Fragment 頁面
        homeViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(hometabLayout));
    }

    //主功能表換頁
    private void homeNavigationItemSelectedListener(){
        homeBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.homeBottomNavigation:
                        intent = new Intent(Home.this, Home.class);
                        startActivity(intent);
                        return true;

                    case R.id.homeStrokeBtnNavigation:
                        intent = new Intent(Home.this, Stroke.class);
                        startActivity(intent);
                        return true;

                    case R.id.homeTakePictureBtnNavigation:

                        return true;

                    case R.id.homeFriendsBtnNavigation:
                        intent = new Intent(Home.this, Friends.class);
                        startActivity(intent);
                        return true;

                    case R.id.homePersonalBtnNavigation:
                        intent = new Intent(Home.this, Personal.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
}
