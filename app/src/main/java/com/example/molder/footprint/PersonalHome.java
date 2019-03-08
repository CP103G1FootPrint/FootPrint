package com.example.molder.footprint;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalHome extends Fragment {


    public PersonalHome() {
        // Required empty public constructor
    }
    private ViewPager viewPager_home;
    private TabLayout personal_tabBar;
    private ImageButton b;
    private View personal_fragment;
    private ImageView imageView;
    private Button btTakePictureLarge, btPickPicture;
    private File file;
//    private RecyclerView recyclerView;
    private PagerAdapter personalhomePagerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        personal_fragment = inflater.inflate(R.layout.personal_activity_main, container, false);
        findViews();
        return personal_fragment;

    }


    private void findViews() {
        b = personal_fragment.findViewById(R.id.personal_pic);
        imageView = personal_fragment.findViewById(R.id.imageView);
        btTakePictureLarge = personal_fragment.findViewById(R.id.btTakePictureLarge);
        btPickPicture = personal_fragment.findViewById(R.id.btPickPicture);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalSelfieMainActivity.class);
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
