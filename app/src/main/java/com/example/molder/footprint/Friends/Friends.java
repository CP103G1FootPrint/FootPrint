package com.example.molder.footprint.Friends;

import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Personal;
import com.example.molder.footprint.R;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Friends extends Fragment implements FragmentBackHandler {

    private static final String TAG = "Friends";
    private Toolbar friendsToolbar;
    private TabLayout friendstabLayout;
    private ViewPager friendsViewPager;
    private PagerAdapter FriendsPageAdapter;
    private TabItem friendsTabFriend, friendsTabMessage;
    private View friends_fragment;
    private AutoCompleteTextView friendsAutoCompleteTextView;
    private String[] users;
    private ArrayAdapter<String> arrayAdapter;

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
        friendsAutoCompleteTextView = friends_fragment.findViewById(R.id.friendAutoCompleteTextView);

        //find LandMark last id
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/AccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findAllUserId");
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                List<String> friends = new Gson().fromJson(result, listType);
                if(friends != null){
                    int count = friends.size();
                    users = null;
                    users = new String[count];
                    for(int k = 0; k < count ; k++){
                        users[k] = friends.get(k);
                    }
                    arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.friend_autocomplete_item, users);
                    friendsAutoCompleteTextView.setAdapter(arrayAdapter);
                }

            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
        }

        //搜尋
        friendsAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userId = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(getActivity(), HomeNewsActivity_Personal.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        //分頁
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
