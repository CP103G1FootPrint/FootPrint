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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.molder.footprint.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SchedulePlanActivity extends AppCompatActivity implements OnMapReadyCallback {

    ViewPager mViewPager;

    private PagerAdapter homePagerAdapter;
    private TabLayout mTabLayout;
    private int page = 3;
    private int yFrom,yStop,mapFrom =0,mapStop=-600;
    private Boolean orientation = false;

    private Button button,move;
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
        animator2 = getTranslateAnimImage();
        animator2.start();
    }

    private void initView() {
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //行程天數
                page = page + 1;

                String string = String.valueOf(page);
                mTabLayout.addTab(mTabLayout.newTab().setText(string));

                homePagerAdapter.notifyDataSetChanged();
            }
        });
        linearLayout = findViewById(R.id.linearMove);
        linearImage = findViewById(R.id.imageMove);
//        linearImage.setY(-300);

        move = findViewById(R.id.move);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orientation == false){
                    yFrom = 0;
                    yStop = 1150;
                    mapFrom = 0;
                    mapStop = 0;
                    orientation = true;
//                    setViewSize(imageView,orientation);
                }else {
                    yFrom = 1150;
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
        mViewPager = findViewById(R.id.homeViewPager);
        mTabLayout = findViewById(R.id.homeTabLayout);
        initFragment();
    }


    private void initFragment() {

        for (int i = 0;i<page;i++){
            String string = String.valueOf(i);
            mTabLayout.addTab(mTabLayout.newTab().setText(string));
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
        homePagerAdapter = new HomePageAdapter(this.getSupportFragmentManager(), mTabLayout.getTabCount());
        //將剛剛取到的分頁所在的頁數 顯示在Fragment上
        mViewPager.setAdapter(homePagerAdapter);

    }



    //繼承FragmentPagerAdapter
    public class HomePageAdapter extends FragmentStatePagerAdapter {

        //建立屬性 分頁頁數計數器
        private int numOfTabs;

        //建構式
        public HomePageAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        //取得分頁畫面與內容
        @Override
        public Fragment getItem(int position) {

            return SchedulePlanDayFragment.newInstances(position);
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
