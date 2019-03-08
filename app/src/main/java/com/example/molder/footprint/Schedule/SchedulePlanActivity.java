package com.example.molder.footprint.Schedule;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class SchedulePlanActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = "SchedulePlanActivity";
    private ViewPager mViewPager;

    private PagerAdapter schedulePlanPagerAdapter;
    private TabLayout mTabLayout;
    private int page = 0;
    private int tripId ;
    private int yFrom,yStop,mapFrom = 0,mapStop = -600;
    private Boolean orientation = false;

    private Button scheduleAddDays, scheduleMoveDays,scheduleMinusDays,scheduleEditSave,scheduleEditCancel;
    private LinearLayout linearLayout,linearImage;
    private Animator animator,animator2;
    private GoogleMap scheduleGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_plantest);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.scheduleGoogleMap);
        mapFragment.getMapAsync(this);

        initView();
        //地圖初始往上移動
        animator2 = getTranslateAnimImage();
        animator2.start();
    }

    private void initView() {
        //打開上一頁的打包的資料
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Trip trip = (Trip) bundle.getSerializable("trip");
            if (trip != null) {
                page = trip.getDays();
                tripId = trip.getTripID();
            }
        }

        //儲存編輯
        scheduleEditSave = findViewById(R.id.scheduleEditSave);
        scheduleEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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
                    mTabLayout.removeTab(mTabLayout.getTabAt(mTabLayout.getTabCount()-1));
                    page = page - 1;
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
                page = page +1;
                String textDay = "第 "+ String.valueOf(page) + " 天";
                mTabLayout.addTab(mTabLayout.newTab().setText(textDay));
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
                    yStop = 1300;
                    mapFrom = 0;
                    mapStop = 0;
                    orientation = true;
//                    setViewSize(imageView,orientation);
                }else {
                    yFrom = 1300;
                    yStop = 0;
                    mapFrom = 0;
                    mapStop = -600;
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

            return SchedulePlanDayFragment.newInstances(position,tripId);
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.scheduleGoogleMap = googleMap;

        LatLng s = new LatLng(-34,151);
        scheduleGoogleMap.addMarker(new MarkerOptions()
        .position(s)
        .title("S"));
        scheduleGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(s));

    }


}
