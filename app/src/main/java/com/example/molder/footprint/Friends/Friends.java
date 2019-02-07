package com.example.molder.footprint.Friends;

import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.molder.footprint.R;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

public class Friends extends Fragment implements FragmentBackHandler {
    private Toolbar friendsToolbar;
    private TabLayout friendstabLayout;
    private ViewPager friendsViewPager;
    private PagerAdapter FriendsPageAdapter;
    private TabItem friendsTabFriend, friendsTabMessage;
    private View friends_fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        friends_fragment = inflater.inflate(R.layout.fragment_friends, container, false);
        handleViews();
        return friends_fragment;
    }
    private void handleViews() {
        friendsToolbar = friends_fragment.findViewById(R.id.friendsToolbar);
        friendstabLayout = friends_fragment.findViewById(R.id.friendsTabLayout);
        friendsViewPager = friends_fragment.findViewById(R.id.friendsViewPager);
        friendsTabFriend = friends_fragment.findViewById(R.id.friendsTabFriend);
        friendsTabMessage = friends_fragment.findViewById(R.id.homeTabStroke);

        friendstabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                friendsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //刷新 Fragment 頁面
        friendsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(friendstabLayout));
        //取得FragmentManager權限 並取得目前分頁所在的頁數
        FriendsPageAdapter = new FriendsPageAdapter(getActivity().getSupportFragmentManager(), friendstabLayout.getTabCount());
        //將剛剛取到的分頁所在的頁數 顯示在Fragment上
        friendsViewPager.setAdapter(FriendsPageAdapter);
    }


    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }
}
