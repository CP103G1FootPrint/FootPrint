package com.example.molder.footprint.HomeNews;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.molder.footprint.main.task.HomeNewsActivity_personal;
//import com.example.molder.footprint.main.task.CommonTask;
//import com.example.molder.footprint.main.task.ImageTask;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.Map.LandMarkInfo;
import com.example.molder.footprint.R;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNewsFragment extends Fragment implements FragmentBackHandler {
    private static final String TAG = "TAG_HomeNewsFragment";
    private FragmentActivity activity;
    private RecyclerView rvNews;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonTask newsCommonTask, userIdTask, landMarkIdTask;
    private HeadImageTask headImageTask;
    private ImageTask newsImageTask;
    private String userId, userNickName, landMarkId, landMarkName;
    private Context context;
    private int choosePosition = 0;
//    private List<HomeNewsFragment_News> homeNewsFragment_news = null;
//    private CommonTask spotGetAllTask;
//    private CommonTask spotDeleteTask;
//    private ImageTask spotImageTask;


    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home_news, container, false);

        //刷新資料
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getHomeNewsFragment_News();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //REFERENCE
        rvNews = view.findViewById(R.id.home_news);
        //LAYOUT MANAGER
        rvNews.setLayoutManager(new LinearLayoutManager(activity));
        rvNews.setLayoutManager(new LinearLayoutManager(getActivity()));
//        getHomeNewsFragment_News();
        return view;
    }

    //連servlet發送請求 取所有動態照片
    private void getHomeNewsFragment_News() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/PicturesServlet";
            List<HomeNewsFragment_News> newsFragment_news = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            newsCommonTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = newsCommonTask.execute().get();
                Type listType = new TypeToken<List<HomeNewsFragment_News>>() {
                }.getType();
                newsFragment_news = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (newsFragment_news == null || newsFragment_news.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoNewsFound);
            } else {
                rvNews.setAdapter(new HomeNewsFragmentAdapter(activity, newsFragment_news));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getHomeNewsFragment_News();
    }

    private class HomeNewsFragmentAdapter extends
            RecyclerView.Adapter<HomeNewsFragmentAdapter.MyViewHolder> {

        private LayoutInflater layoutInflater;
        private List<HomeNewsFragment_News> homeNewsFragment_news;
        private int imageSize;


        HomeNewsFragmentAdapter(Context context, List<HomeNewsFragment_News> HomeNewsFragment_News) {
            layoutInflater = LayoutInflater.from(context);
            this.homeNewsFragment_news = HomeNewsFragment_News;
            imageSize = getResources().getDisplayMetrics().widthPixels;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profile＿picture;
            TextView userName, landMarkname, description;
            ImageView news_picture;
//            ImageButton message;
            CheckBox thumb,likes,collection,message;


            public MyViewHolder(View itemView) {
                super(itemView);
                description = itemView.findViewById(R.id.tv_News_Description);
                profile＿picture = itemView.findViewById(R.id.ci_profile＿picture);
                userName = itemView.findViewById(R.id.tv_news_userName);
                landMarkname = itemView.findViewById(R.id.tv_news_landmark);
                news_picture = itemView.findViewById(R.id.iv_news_picture);
                likes = itemView.findViewById(R.id.ib_news_like);
                message = itemView.findViewById(R.id.ib_news_message);
                collection = itemView.findViewById(R.id.ib_news_collection);
//                thumb = itemView.findViewById(R.id.thumb);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(context==null)
                context = parent.getContext();
            View itemView = layoutInflater.inflate(R.layout.fragment_home_news_listitem, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            final HomeNewsFragment_News homeNewsFragmentnews = homeNewsFragment_news.get(position);
            final Integer tag = position+1;

            String url = Common.URL + "/PicturesServlet";
            final int id = homeNewsFragmentnews.getImageID();
            newsImageTask = new ImageTask(url, id, imageSize, holder.news_picture);
            newsImageTask.execute();

            //先抓userId
            if (Common.networkConnected(activity)) {
                url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findUserId");
                jsonObject.addProperty("id", id);
                //將內容轉成json字串
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {
                    String jsonIn = userIdTask.execute().get();
                    userId = String.valueOf(jsonIn);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (homeNewsFragment_news == null || homeNewsFragment_news.isEmpty()) {
//                    Toast.makeText(HomeNewsFragment.this,R.string.NoFoundNewsPictures,Toast.LENGTH_SHORT).show();
                } else {
                    url = Common.URL + "/PicturesServlet";
                    jsonObject.addProperty("action", "findUserNickName");
                    jsonObject.addProperty("id", userId);
                    userIdTask = new CommonTask(url, jsonObject.toString());
                    try {

                        //顯示使用者暱稱
                        String jsonIn = userIdTask.execute().get();
                        userNickName = String.valueOf(jsonIn);
                        holder.userName.setText(userNickName);

                        //使用者頭像
                        url = Common.URL + "/PicturesServlet";
                        headImageTask = new HeadImageTask(url, userId, imageSize, holder.profile＿picture);
                        headImageTask.execute();

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            } else {
//                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
            }

            //抓landMarkId
            if (Common.networkConnected(activity)) {
                url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findLandMarkId");
                jsonObject.addProperty("id", id);
                landMarkIdTask = new CommonTask(url, jsonObject.toString());
                try {
                    String jsonIn = landMarkIdTask.execute().get();
                    landMarkId = String.valueOf(jsonIn);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (homeNewsFragment_news == null || homeNewsFragment_news.isEmpty()) {
//                    Toast.makeText(HomeNewsFragment.this,R.string.NoFoundNewsPictures,Toast.LENGTH_SHORT).show();
                } else {
                    url = Common.URL + "/PicturesServlet";
                    jsonObject.addProperty("action", "findLandMark");
                    jsonObject.addProperty("id", landMarkId);
                    landMarkIdTask = new CommonTask(url, jsonObject.toString());
                    try {
                        //顯示landMark名稱
                        String jsonIn = landMarkIdTask.execute().get();
                        landMarkName = String.valueOf(jsonIn);
                        holder.landMarkname.setText(landMarkName);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            } else {
//                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
            }

            holder.description.setText(homeNewsFragmentnews.getDescription());
            //點擊使用者頭像換頁至該使用者詳細資訊頁面
            holder.profile＿picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HomeNewsActivity_Personal.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
            //點擊使用者名字換頁至該使用者詳細資訊頁面
            holder.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HomeNewsActivity_Personal.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);

                }
            });
            //點擊地標名子換頁至該地標詳細資訊頁面
            holder.landMarkname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), LandMarkInfo.class);
//                    intent.putExtra("landMarkName",landMarkName);
//                    startActivity(intent);
                    Intent intent = new Intent(getActivity(), LandMarkInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("landMarkName", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
//
            //點擊留言圖像換頁至留言頁面
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HomeNewsActivity_Message.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return homeNewsFragment_news.size();
        }

        private void changeFragment(Fragment fragment) {
            if (getActivity().getSupportFragmentManager() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.content, fragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (newsCommonTask != null) {
            newsCommonTask.cancel(true);
            newsCommonTask = null;
        }
    }
}
