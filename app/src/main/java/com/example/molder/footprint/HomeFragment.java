package com.example.molder.footprint;


import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements FragmentBackHandler {

    private Toolbar homeToolbar;
    private TabLayout hometabLayout;
    private ViewPager homeViewPager;
    private PagerAdapter homePagerAdapter;
    private TabItem homeTabNews, homeTabStroke, homeTabMap;
    private View home_fragment;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        home_fragment = inflater.inflate(R.layout.fragment_home, container, false);
        handleViews();
        return home_fragment;
    }

    //initial 初始化
    private void handleViews() {

        homeToolbar = home_fragment.findViewById(R.id.homeToolbar);
        hometabLayout = home_fragment.findViewById(R.id.homeTabLayout);
        homeViewPager = home_fragment.findViewById(R.id.homeViewPager);
        homeTabNews = home_fragment.findViewById(R.id.homeTabNews);
        homeTabStroke = home_fragment.findViewById(R.id.homeTabStroke);
        homeTabMap = home_fragment.findViewById(R.id.homeTabMap);

        //tabLayout監聽
        hometabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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
        //取得FragmentManager權限 並取得目前分頁所在的頁數
        homePagerAdapter = new HomePageAdapter(getActivity().getSupportFragmentManager(), hometabLayout.getTabCount());
        //將剛剛取到的分頁所在的頁數 顯示在Fragment上
        homeViewPager.setAdapter(homePagerAdapter);
    }


    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }
}
