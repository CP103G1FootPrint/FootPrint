package com.example.molder.footprint;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.Login.Account;
import com.example.molder.footprint.Schedule.PersonalSetting;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalHome extends Fragment {


    public PersonalHome() {
        // Required empty public constructor
    }

    private ViewPager viewPager_home;
    private TabLayout personal_tabBar;
    private ImageButton btSetting;
    private View personal_fragment;
    private PagerAdapter personalhomePagerAdapter;
    private HeadImageTask headImageTask;
    private TextView tv_id, tv_point;
    private ImageView piggy, selfePic;
    private int imageSize;
    private String url;
    private CommonTask retrieveLocationTask;
    private List<Account> accountList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        personal_fragment = inflater.inflate(R.layout.personal_activity_main, container, false);
        findViews();
        return personal_fragment;

    }


    private void findViews() {

        btSetting = personal_fragment.findViewById(R.id.btSetting);
        tv_id = personal_fragment.findViewById(R.id.tv_id);
        tv_point = personal_fragment.findViewById(R.id.tv_point);
        piggy = personal_fragment.findViewById(R.id.piggy);
        selfePic = personal_fragment.findViewById(R.id.selfePic);
        imageSize = getResources().getDisplayMetrics().widthPixels;


        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String userId = preferences.getString("userId", "");//抓使用者帳號


        url = Common.URL + "/PicturesServlet";
        headImageTask = new HeadImageTask(url, userId, imageSize, selfePic);
        headImageTask.execute();//抓頭像

        if (Common.networkConnected(getActivity())) { //抓使用者名稱和點數
            String url = Common.URL + "/AccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findIntegral");
            jsonObject.addProperty("id", userId);
            //將內容轉成json字串

            retrieveLocationTask = new CommonTask(url, jsonObject.toString());
            try {   //接收資料
                String jsonIn = retrieveLocationTask.execute().get();
                Type listType = new TypeToken<List<Account>>() {
                }.getType();
                //解析 json to gson
                accountList = new Gson().fromJson(jsonIn, listType); //暫存資料
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }

            if (accountList == null || accountList.isEmpty()) {
//                Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
            } //顯示資料
            else {
                Account accountText = accountList.get(0);
                String nickName = accountText.getNickname();
                tv_id.setText(nickName);
//                Account integralText = accountList.get(1);
                int integral = accountText.getIntegral();
                tv_point.setText(String.valueOf(integral));

            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }


        btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalSetting.class);
                startActivity(intent);

            }

        });
        personal_tabBar = personal_fragment.findViewById(R.id.personal_tabBar);
        personal_tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_home.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager_home = personal_fragment.findViewById(R.id.fragment_home);
        //刷新 Fragment 頁面
        viewPager_home.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(personal_tabBar));
        //取得FragmentManager權限 並取得目前分頁所在的頁數
        personalhomePagerAdapter = new MainPageAdapter(getActivity().getSupportFragmentManager(), personal_tabBar.getTabCount());
        //將剛剛取到的分頁所在的頁數 顯示在Fragment上
        viewPager_home.setAdapter(personalhomePagerAdapter);
    }

    //繼承FragmentPagerAdapter
    public class MainPageAdapter extends FragmentStatePagerAdapter {

        //建立屬性 分頁頁數計數器
        private int numOfTabs;

        //建構式
        public MainPageAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        //取得分頁畫面與內容
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PersonalRecordMain();
                case 1:
                    return new PersonalCollectMain();
                case 2:
                    return new PersonalNotifyMain();
                case 3:
                    return new PersonalExchangeMain();

                default:
                    return null;
            }

        }

        //取得分頁頁數
        @Override
        public int getCount() {
            return numOfTabs;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }


}
