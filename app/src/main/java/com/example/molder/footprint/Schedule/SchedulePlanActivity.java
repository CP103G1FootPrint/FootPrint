package com.example.molder.footprint.Schedule;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.Map.LandMark;
import com.example.molder.footprint.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static com.example.molder.footprint.Schedule.ScheduleDayServer.connectServer;
import static com.example.molder.footprint.Schedule.ScheduleDayServer.disconnectServer;
import static com.example.molder.footprint.Schedule.ScheduleDayServer.getUserName;
import static com.example.molder.footprint.Schedule.ScheduleDayServer.scheduleDayWebSocketClient;


public class SchedulePlanActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = "SchedulePlanActivity";
    private ViewPager mViewPager;
    private static final int REQ_PERMISSIONS = 0;
    private PagerAdapter schedulePlanPagerAdapter;
    private TabLayout mTabLayout;
    private int page = 0;
    private int tripId ;
    private int yFrom,yStop,mapFrom = 0,mapStop = -450;
    private Boolean orientation = false;

    private Button scheduleAddDays, scheduleMoveDays,scheduleMinusDays,scheduleEditSave,scheduleEditCancel;
    private LinearLayout linearLayout,linearImage;
    private Animator animator,animator2;
    private GoogleMap scheduleGoogleMap;
    private CommonTask retrieveLocationTask2,retrieveLocationTask3,friendsCommonTask;
    private List<LandMark> mDatas = new ArrayList<>();
    public static int judgmentDay;
    private SupportMapFragment mapFragment;
    private LocalBroadcastManager broadcastManager;
    private String userId;
    private List<String> friends = null;
    private HashSet<Marker> hset = new HashSet<>();
    private List<Marker> markerList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_plantest);

        connectServer(this, getUserName(this));
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        userId = preferences.getString("userId", "");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.scheduleGoogleMap);

        initView();
        initData();
        //地圖初始往上移動
        animator2 = getTranslateAnimImage();
        animator2.start();
        // 廣播
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerScheduleDayReceiver();

    }

    private void initView() {
        //打開上一頁的打包的資料
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Trip trip = (Trip) bundle.getSerializable("trip");
            if (trip != null) {
                page = trip.getDays();
                tripId = trip.getTripID();
                getFriends();
            }
        }

//        //儲存編輯
//        scheduleEditSave = findViewById(R.id.scheduleEditSave);
//        scheduleEditSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        //取消編輯
        scheduleEditCancel = findViewById(R.id.scheduleEditCancel);
        scheduleEditCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //減少行程天數
        scheduleMinusDays = findViewById(R.id.scheduleMinusDays);
        scheduleMinusDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTabLayout.getTabCount() > 1){
//                    mTabLayout.removeTab(mTabLayout.getTabAt(mTabLayout.getTabCount()-1));
                    page = page - 1;

                    //刪除day的地標
                    if (Common.networkConnected(SchedulePlanActivity.this)) {
                        String url = Common.URL + "/LocationServlet";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "dayLandMarkDelete");
                        jsonObject.addProperty("tripId", tripId);
                        jsonObject.addProperty("day", page);
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    //更新資料庫
                    if (Common.networkConnected(SchedulePlanActivity.this)) {
                        String url = Common.URL + "/TripServlet";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "dayUpdate");
                        jsonObject.addProperty("tripId", tripId);
                        jsonObject.addProperty("day", page);
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
                            Common.showToast(SchedulePlanActivity.this, R.string.msg_UpdateFail);
                        } else {
                            Common.showToast(SchedulePlanActivity.this, R.string.msg_UpdateSuccess);
                            //廣播
                            ScheduleDay scheduleDay = new ScheduleDay("ScheduleDay","dayChangeLess",userId,new Gson().toJson(friends),1, mTabLayout.getTabCount());
                            String scheduleMessageJson = new Gson().toJson(scheduleDay);
                            scheduleDayWebSocketClient.send(scheduleMessageJson);
                        }
                    } else {
                        Common.showToast(SchedulePlanActivity.this, R.string.msg_NoNetwork);
                    }
                    //刷新頁面
                    schedulePlanPagerAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(SchedulePlanActivity.this, R.string.msg_LastDay, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //增加行程天數
        scheduleAddDays = findViewById(R.id.scheduleAddDays);
        scheduleAddDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //page =行程天數
                page = page + 1;
//                String textDay = "第 "+ String.valueOf(page) + " 天";
//                mTabLayout.addTab(mTabLayout.newTab().setText(textDay));
                //更新資料庫
                if (Common.networkConnected(SchedulePlanActivity.this)) {
                    String url = Common.URL + "/TripServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "dayUpdate");
                    jsonObject.addProperty("tripId", tripId);
                    jsonObject.addProperty("day", page);
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(SchedulePlanActivity.this, R.string.msg_UpdateFail);
                    } else {
                        Common.showToast(SchedulePlanActivity.this, R.string.msg_UpdateSuccess);
                        //廣播
                        ScheduleDay scheduleDay = new ScheduleDay("ScheduleDay","dayChangeAdd",userId,new Gson().toJson(friends),1, mTabLayout.getTabCount());
                        String scheduleMessageJson = new Gson().toJson(scheduleDay);
                        scheduleDayWebSocketClient.send(scheduleMessageJson);
                    }
                } else {
                    Common.showToast(SchedulePlanActivity.this, R.string.msg_NoNetwork);
                }
                //刷新頁面
                schedulePlanPagerAdapter.notifyDataSetChanged();
            }
        });
        linearLayout = findViewById(R.id.linearMove);
        linearImage = findViewById(R.id.scheduleMoveMap);
//        linearImage.setY(-300);

        //移動框
        scheduleMoveDays = findViewById(R.id.scheduleMoveDays);
        scheduleMoveDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orientation == false){
                    yFrom = 0;
                    yStop = 1130;
                    mapFrom = 0;
                    mapStop = 0;
                    orientation = true;
//                    setViewSize(imageView,orientation);
                }else {
                    yFrom = 1130;
                    yStop = 0;
                    mapFrom = 0;
                    mapStop = -450;
                    orientation = false;
//                    setViewSize(imageView,orientation);
                }
                animator = getTranslateAnim();
                animator2 = getTranslateAnimImage();
                animator.start();
                animator2.start();
            }
        });
        mViewPager = findViewById(R.id.scheduleEditViewPager);
        mTabLayout = findViewById(R.id.scheduleEditTabLayout);
        initFragment();
    }


    private void initFragment() {

        for (int i = 0;i<page;i++){
            String textDay = "第 "+ String.valueOf(i+1) + " 天";
            mTabLayout.addTab(mTabLayout.newTab().setText(textDay));
        }

        //tabLayout監聽
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                int day = tab.getPosition();
                judgmentDay = tab.getPosition();
                ScheduleDay scheduleDay = new ScheduleDay("ScheduleDay", day,"updateMap",userId,userId,0,tripId);
                String scheduleMessageJson = new Gson().toJson(scheduleDay);
                scheduleDayWebSocketClient.send(scheduleMessageJson);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //刷新 Fragment 頁面
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //取得FragmentManager權限 並取得目前分頁所在的頁數
        schedulePlanPagerAdapter = new SchedulePlanPageAdapter(this.getSupportFragmentManager(), mTabLayout.getTabCount());
        //將剛剛取到的分頁所在的頁數 顯示在Fragment上
        mViewPager.setAdapter(schedulePlanPagerAdapter);

    }

    //繼承FragmentPagerAdapter
    public class SchedulePlanPageAdapter extends FragmentStatePagerAdapter {

        //建立屬性 分頁頁數計數器
        private int numOfTabs;

        //建構式
        public SchedulePlanPageAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        //取得分頁畫面與內容
        @Override
        public Fragment getItem(int position) {
            return SchedulePlanDayFragment.newInstances(position,tripId,friends);
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


    /**
     * 建立位移動畫並套用當前特效
     */
    private ObjectAnimator getTranslateAnim() {
        /* 第1個參數為套用動畫的對象 - ivSoccer物件；
           第2個參數為要套用的屬性 - TRANSLATION_X代表水平位移；
           第3個參數為屬性設定值 - 從原點向右移動800像素 */
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(linearLayout, View.TRANSLATION_Y, yFrom, yStop);
        /* 設定播放時間(預設300毫秒)為500毫秒，就是0.5秒 */
        objectAnimator.setDuration(500);
        /* 1代表重複播放1次；預設為0代表不重播；INFINITE代表無限重播 */
        objectAnimator.setRepeatCount(0);
        /* REVERSE代表播放完畢後會反向播放；重複播放至少要設定1次才會反向播放 */
//        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        /* 套用特效 */
//        interpolator = new LinearInterpolator();
        objectAnimator.setInterpolator(new LinearInterpolator());
        return objectAnimator;
    }

    /**
     * 建立位移動畫並套用當前特效
     */
    private ObjectAnimator getTranslateAnimImage() {
        /* 第1個參數為套用動畫的對象 - ivSoccer物件；
           第2個參數為要套用的屬性 - TRANSLATION_X代表水平位移；
           第3個參數為屬性設定值 - 從原點向右移動800像素 */
        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(linearImage, View.TRANSLATION_Y, mapFrom, mapStop);
        /* 設定播放時間(預設300毫秒)為500毫秒，就是0.5秒 */
        objectAnimator.setDuration(500);
        /* 1代表重複播放1次；預設為0代表不重播；INFINITE代表無限重播 */
        objectAnimator.setRepeatCount(0);
        /* REVERSE代表播放完畢後會反向播放；重複播放至少要設定1次才會反向播放 */
//        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        /* 套用特效 */
//        interpolator = new LinearInterpolator();
        objectAnimator.setInterpolator(new LinearInterpolator());
        return objectAnimator;
    }


    private void getFriends() {
        //抓取所有與使用者關係為好友的資料
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/TripServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getTripFriends");
            jsonObject.addProperty("tripId", tripId);
            friendsCommonTask = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = friendsCommonTask.execute().get();
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                friends = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.scheduleGoogleMap = googleMap;
        if(mDatas != null){
            googleMap.clear();
            showMap(mDatas);
            draw2D(mDatas);
        }
//        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }

    //一開始地圖初始值
    private void initData() {
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findLandMarkInSchedulePlanDay");
            jsonObject.addProperty("SchedulePlanDayTripId", tripId);
            jsonObject.addProperty("SchedulePlanDay", 0);
            //將內容轉成json字串
            retrieveLocationTask2 = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveLocationTask2.execute().get();
                Type listType = new TypeToken<List<LandMark>>() {
                }.getType();
                //解析 json to gson
                mDatas = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }if (mDatas == null || mDatas.isEmpty()) {
//                Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
                mapFragment.getMapAsync(this);
            } else {
                mapFragment.getMapAsync(this);
            }
        }
    }

    //更新地圖
    private void changeData(int day, int tripId) {
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findLandMarkInSchedulePlanDay");
            jsonObject.addProperty("SchedulePlanDayTripId", tripId);
            jsonObject.addProperty("SchedulePlanDay", day);
            //將內容轉成json字串
            retrieveLocationTask3 = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveLocationTask3.execute().get();
                Type listType = new TypeToken<List<LandMark>>() {
                }.getType();
                //解析 json to gson
                mDatas = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }if (mDatas == null || mDatas.isEmpty()) {
//                Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
                mapFragment.getMapAsync(this);
                scheduleGoogleMap.clear();
            } else {
                mapFragment.getMapAsync(this);
            }
        }
    }

    private void showMap(List<LandMark> locations) {
        for (int index = 0; index < locations.size(); index++) {
            LandMark location = locations.get(index);
            LandMark location1 = locations.get(0);
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng position1 = new LatLng(location1.getLatitude(), location1.getLongitude());
            cameraPosition(position1);
            String snippet = getString(R.string.col_Name) + ": " + location.getName() + "\n" +
                    getString(R.string.col_Type) + ": " + location.getType() + "\n" +
                    getString(R.string.col_Address) + ": " + location.getAddress();

            // ic_add spot on the map
            scheduleGoogleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(location.getName())
                    .snippet(snippet));

            scheduleGoogleMap.setInfoWindowAdapter(new ScheduleMapInfoWindowAdapter(this, location));
        }

    }

    private void draw2D(List<LandMark> locations) {
        List<LatLng> position = new ArrayList<LatLng>();
        for (int index = 0; index < locations.size(); index++) {
            LandMark location = locations.get(index);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            position.add(latLng);
        }
            Polyline polyline = scheduleGoogleMap.addPolyline(
                    new PolylineOptions()
                        //線的粗細
                        .width(6)
                        .addAll(position)
                        .color(Color.MAGENTA)
                        .zIndex(10));
//        polyline.setPoints(position);
    }

    //移動到圖標中心
    private void cameraPosition(LatLng location){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        scheduleGoogleMap.animateCamera(cameraUpdate);
    }

    // New Permission see Appendix A
    private void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        askPermissions();
    }

    //攔截廣播     Tag: ScheduleDay
    private void registerScheduleDayReceiver() {
        IntentFilter scheduleDayFilter = new IntentFilter("ScheduleDay");
        ScheduleDayReceiver scheduleDayReceiver = new ScheduleDayReceiver();
        broadcastManager.registerReceiver(scheduleDayReceiver, scheduleDayFilter);
    }

    // 接收到訊息會在TextView呈現
    private class ScheduleDayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 取得訊息
            String message = intent.getStringExtra("message");
            if(message!=null){
                ScheduleDay dayMessage = new Gson().fromJson(message, ScheduleDay.class);
                String dayType = dayMessage.getMessageType();
                int scheduleOfDay = dayMessage.getNumberOfDay();
                int tabCount = dayMessage.getTabCount();
                int tripId = dayMessage.getTripId();

                switch (dayType){
                    case "updateMap":
                        if(judgmentDay == scheduleOfDay) {
                            changeData(scheduleOfDay, tripId);
                        }
                        break;
                    case "dayChangeAdd":
                        String textDay = "第 "+ String.valueOf(tabCount+1) + " 天";
                        mTabLayout.addTab(mTabLayout.newTab().setText(textDay));
                        //刷新 Fragment 頁面
                        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                        //取得FragmentManager權限 並取得目前分頁所在的頁數
                        schedulePlanPagerAdapter = new SchedulePlanPageAdapter(getSupportFragmentManager(), tabCount);
                        //將剛剛取到的分頁所在的頁數 顯示在Fragment上
                        mViewPager.setAdapter(schedulePlanPagerAdapter);
                        schedulePlanPagerAdapter.notifyDataSetChanged();
                        break;
                    case "dayChangeLess":
                        if (tabCount > 1) {
                            mTabLayout.removeTab(mTabLayout.getTabAt(tabCount - 1));
                            //刷新 Fragment 頁面
                            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                            //取得FragmentManager權限 並取得目前分頁所在的頁數
                            schedulePlanPagerAdapter = new SchedulePlanPageAdapter(getSupportFragmentManager(), tabCount);
                            //將剛剛取到的分頁所在的頁數 顯示在Fragment上
                            mViewPager.setAdapter(schedulePlanPagerAdapter);
                            schedulePlanPagerAdapter.notifyDataSetChanged();
                        }
                        break;
                    default: break;
                }

            }

            // 顯示點擊地標秀在地圖
            LandMark landMark = (LandMark) intent.getSerializableExtra("lankMark");
            if(landMark != null){
                LatLng latLng = new LatLng(landMark.getLatitude(),landMark.getLongitude());
                if(markerList != null){
                    for(int k = 0; k < markerList.size(); k++){
                        Marker marker = markerList.get(k);
                        if(marker.getTitle().equals(landMark.getName())){
                            marker.showInfoWindow();
                            cameraPosition(latLng);
                        }
                    }
                }else {
                    Marker marker = scheduleGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(landMark.getName()));
                    marker.showInfoWindow();
                    cameraPosition(latLng);
                    hset.add(marker);
                    markerList = new ArrayList<>(hset);
                }
            }

//            Log.d(TAG, message);
        }
    }

    public class ScheduleMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private static final String TAG = "TAG_ScheduleMapInfo";
        private View infoWindow;
        private LandMark location;
        private int imageSize;
        private ImageTask landMarkImageTask;
        private CommonTask imageIdTask;
        private int imageId;

        ScheduleMapInfoWindowAdapter(Context context, LandMark location) {
            infoWindow = View.inflate(context, R.layout.home_map_info_window, null);
            this.location = location;
            imageSize = context.getResources().getDisplayMetrics().widthPixels / 3;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            //用title＝landMark name 比對找出landMarkID 就可以知道imageID
            if (Common.networkConnected(SchedulePlanActivity.this)) {
                String url = Common.URL + "/LocationServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findLocationId");
                jsonObject.addProperty("name", marker.getTitle());
                String jsonOut = jsonObject.toString();
                imageIdTask = new CommonTask(url, jsonOut);
                try {
                    String result = imageIdTask.execute().get();
                    imageId = Integer.valueOf(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                Common.showToast(SchedulePlanActivity.this, R.string.msg_NoNetwork);
            }

            //秀出圖片,店名,地址
            ImageView imageView = infoWindow.findViewById(R.id.ivWindowPicture);
            String url = Common.URL + "/LocationServlet";
            int id = imageId;

            Bitmap bitmap = null;
            try {
                landMarkImageTask = new ImageTask(url, id, imageSize);
                // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
                bitmap = landMarkImageTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.default_image);
            }
            TextView tvTitle = infoWindow.findViewById(R.id.tvHomeMapTitle);
            tvTitle.setText(marker.getTitle());

            TextView tvSnippet = infoWindow.findViewById(R.id.tvHomeMapSnippet);
            tvSnippet.setText(marker.getSnippet());

            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (retrieveLocationTask2 != null) {
            retrieveLocationTask2.cancel(true);
            retrieveLocationTask2 = null;
        }
        if (retrieveLocationTask3 != null) {
            retrieveLocationTask3.cancel(true);
            retrieveLocationTask3 = null;
        }
        if (friendsCommonTask != null) {
            friendsCommonTask.cancel(true);
            friendsCommonTask = null;
        }
    }

//     Activity結束即中斷WebSocket連線
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectServer();
    }
}
