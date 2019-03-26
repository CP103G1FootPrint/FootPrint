package com.example.molder.footprint.HomeNews;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNewsFragment extends Fragment implements FragmentBackHandler {
    private static final String TAG = "TAG_HomeNewsFragment";
    private FragmentActivity activity;
    private RecyclerView rvNews;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonTask newsCommonTask, userIdTask, landMarkIdTask, picturesIdTask;
    private HeadImageTask headImageTask;
    private ImageTask newsImageTask;
    private String userId, userNickName, landMarkId, landMarkName, finalLikesCount;
    private String personalUserId;
    private Context context;

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

        //獲得目前登入使用者id
        SharedPreferences preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        personalUserId = preferences.getString("userId", "");

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

        return view;
    }


    //連servlet發送請求 取所有動態照片
    private void getHomeNewsFragment_News() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/PicturesServlet";
            List<HomeNewsFragment_News> newsFragment_news = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAlls");
            jsonObject.addProperty("userId",personalUserId);
            String jsonOut = jsonObject.toString();
            newsCommonTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = newsCommonTask.execute().get();
                Type listType = new TypeToken<List<HomeNewsFragment_News>>() {
                }.getType();
                newsFragment_news = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
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
        private List<HomeNewsFragment_News_Likes> newsLikes;
        private List<HomeNewsFragment_News_Collection> newsCollection;
        private int imageSize;


        HomeNewsFragmentAdapter(Context context, List<HomeNewsFragment_News> HomeNewsFragment_News) {
            layoutInflater = LayoutInflater.from(context);
            this.homeNewsFragment_news = HomeNewsFragment_News;
            imageSize = getResources().getDisplayMetrics().widthPixels ;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profile＿picture;
            TextView userName, landMarkname, description, like_Count, tv_Like_Word;
            ImageView news_picture;
            CheckBox likes, collection, message;


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
                like_Count = itemView.findViewById(R.id.tv_Like_Count);
                tv_Like_Word = itemView.findViewById(R.id.tv_Like_Word);
            }
        }
        //載入itemView
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (context == null)
                context = parent.getContext();
            View itemView = layoutInflater.inflate(R.layout.fragment_home_news_listitem, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            final HomeNewsFragment_News homeNewsFragmentnews = homeNewsFragment_news.get(position);
            final int id = homeNewsFragmentnews.getImageID();
            String userId = homeNewsFragmentnews.getUserID();
            int newsLikes = homeNewsFragmentnews.getLikeId();
            int newsCollection = homeNewsFragmentnews.getCollectionId();
            String nickName = homeNewsFragmentnews.getNickName();
            String landMarkName = homeNewsFragmentnews.getLandMarkName();

            //景點圖片
            String url = Common.URL + "/PicturesServlet";
            newsImageTask = new ImageTask(url, id, imageSize, holder.news_picture);
            newsImageTask.execute();

            //使用者頭像
            url = Common.URL + "/PicturesServlet";
            headImageTask = new HeadImageTask(url, userId, imageSize, holder.profile＿picture);
            headImageTask.execute();

            //顯示使用者暱稱
            holder.userName.setText(nickName);

            //顯示landMark名稱
            holder.landMarkname.setText(landMarkName);

            if (newsLikes == 0) {
                    holder.likes.setChecked(false);
                } else {
                    holder.likes.setChecked(true);
                }

            if (newsCollection == 0) {
                    holder.collection.setChecked(false);
                } else {
                    holder.collection.setChecked(true);
                }

//            //先抓userId
//            if (Common.networkConnected(activity)) {
//                url = Common.URL + "/PicturesServlet";
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("action", "findUserId");
//                jsonObject.addProperty("id", id);
//                //將內容轉成json字串
//                userIdTask = new CommonTask(url, jsonObject.toString());
//                try {
//                    String jsonIn = userIdTask.execute().get();
//                    userId = String.valueOf(jsonIn);
//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//                if (homeNewsFragment_news == null || homeNewsFragment_news.isEmpty()) {
////                    Toast.makeText(HomeNewsFragment.this,R.string.NoFoundNewsPictures,Toast.LENGTH_SHORT).show();
//                } else {
//                    url = Common.URL + "/PicturesServlet";
//                    jsonObject.addProperty("action", "findUserNickName");
//                    jsonObject.addProperty("id", userId);
//                    userIdTask = new CommonTask(url, jsonObject.toString());
//                    try {
//
//                        //顯示使用者暱稱
//                        String jsonIn = userIdTask.execute().get();
//                        userNickName = String.valueOf(jsonIn);
//                        holder.userName.setText(userNickName);
//

//
//                    } catch (Exception e) {
//                        Log.e(TAG, e.toString());
//                    }
//                }
//            } else {
////                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
//            }
//
//            //抓landMarkId
//            if (Common.networkConnected(activity)) {
//                url = Common.URL + "/PicturesServlet";
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("action", "findLandMarkId");
//                jsonObject.addProperty("id", id);
//                landMarkIdTask = new CommonTask(url, jsonObject.toString());
//                try {
//                    String jsonIn = landMarkIdTask.execute().get();
//                    landMarkId = String.valueOf(jsonIn);
//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//                if (homeNewsFragment_news == null || homeNewsFragment_news.isEmpty()) {
////                    Toast.makeText(HomeNewsFragment.this,R.string.NoFoundNewsPictures,Toast.LENGTH_SHORT).show();
//                } else {
//                    url = Common.URL + "/PicturesServlet";
//                    jsonObject.addProperty("action", "findLandMark");
//                    jsonObject.addProperty("id", landMarkId);
//                    landMarkIdTask = new CommonTask(url, jsonObject.toString());
//                    try {
//                        //顯示landMark名稱
//                        String jsonIn = landMarkIdTask.execute().get();
//                        landMarkName = String.valueOf(jsonIn);
//                        holder.landMarkname.setText(landMarkName);
//                    } catch (Exception e) {
//                        Log.e(TAG, e.toString());
//                    }
//                }
//            } else {
////                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
//            }
//
//            //抓目前使用者對照片按讚的id及收藏的id
//            if (Common.networkConnected(activity)) {
//                url = Common.URL + "/LikesServlet";
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("action", "findLikesImageId");
//                jsonObject.addProperty("userId", personalUserId);
//                jsonObject.addProperty("imageId", id);
//                String jsonOut = jsonObject.toString();
//                picturesIdTask = new CommonTask(url, jsonOut);
//                try {
//                    String jsonIn = picturesIdTask.execute().get();
//                    Type listType = new TypeToken<List<HomeNewsFragment_News_Likes>>() {
//                    }.getType();
//                    newsLikes = new Gson().fromJson(jsonIn, listType);
//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//                if (newsLikes == null || newsLikes.isEmpty()) {
//                    holder.likes.setChecked(false);
//                } else {
//                    holder.likes.setChecked(true);
//                }
//            }
//            if (Common.networkConnected(activity)) {
//                url = Common.URL + "/CollectionServlet";
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("action", "findCollectionId");
//                jsonObject.addProperty("userId", personalUserId);
//                jsonObject.addProperty("imageId", id);
//                String jsonOut = jsonObject.toString();
//                picturesIdTask = new CommonTask(url, jsonOut);
//                try {
//                    String jsonIn = picturesIdTask.execute().get();
//                    Type listType = new TypeToken<List<HomeNewsFragment_News_Collection>>() {
//                    }.getType();
//                    newsCollection = new Gson().fromJson(jsonIn, listType);
//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//                if (newsCollection == null || newsCollection.isEmpty()) {
//                    holder.collection.setChecked(false);
//                } else {
//                    holder.collection.setChecked(true);
//                }
//            }

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
                    Intent intent = new Intent(getActivity(), LandMarkInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("landMarkName", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
            //按讚||不按讚
            holder.like_Count.setText(homeNewsFragmentnews.getLikesCount());
            holder.likes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //抓取照片的按讚數
                    int likesCount = Integer.valueOf(holder.like_Count.getText().toString());

                    if (isChecked) {
                        //使用者按讚時
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL + "/LikesServlet";
                            HomeNewsFragment_News_Likes homeNewsFragment_news_likes;
                            homeNewsFragment_news_likes = new HomeNewsFragment_News_Likes(0, personalUserId, id);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insert");
                            jsonObject.addProperty("share", new Gson().toJson(homeNewsFragment_news_likes));
                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);
                            } catch (Exception e) {
//                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, R.string.msg_InsertFail);
                            } else {
                                Common.showToast(activity, R.string.msg_Liked);
                            }
                        } else {
                            Common.showToast(activity, R.string.msg_NoNetwork);
                        }
                        likesCount++;
                        holder.like_Count.setText(String.valueOf(likesCount));
                    } else {
                        //使用者取消讚時
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL + "/LikesServlet";
                            HomeNewsFragment_News_Likes homeNewsFragment_news_likes;
                            homeNewsFragment_news_likes = new HomeNewsFragment_News_Likes(personalUserId, id);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "delete");
                            jsonObject.addProperty("delete", new Gson().toJson(homeNewsFragment_news_likes));
                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);
                            } catch (Exception e) {
//                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, R.string.msg_InsertFail);
                            } else {
//                                Common.showToast(activity, R.string.msg_InsertSuccess);
                            }
                        } else {
                            Common.showToast(activity, R.string.msg_NoNetwork);
                        }
                        likesCount--;
                        holder.like_Count.setText(String.valueOf(likesCount));

                    }
                    //更新最後照片的按讚數
                    finalLikesCount = String.valueOf(likesCount);
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL + "/PicturesServlet";
                        HomeNewsFragment_News homeNewsFragment_news;
                        homeNewsFragment_news = new HomeNewsFragment_News(finalLikesCount, id);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "update");
                        jsonObject.addProperty("update", new Gson().toJson(homeNewsFragment_news));
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
//                            Common.showToast(activity, R.string.msg_InsertFail);
                        } else {
//                            Common.showToast(activity, R.string.msg_InsertSuccess);
                        }
                    } else {
                        Common.showToast(activity, R.string.msg_NoNetwork);
                    }
                }
            });
//            HomeNewsFragmentAdapter.this.notifyDataSetChanged();

            //收藏||取消收藏
            holder.collection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //使用者收藏時
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL + "/CollectionServlet";
                            HomeNewsFragment_News_Collection homeNewsFragment_news_collection;
                            homeNewsFragment_news_collection = new HomeNewsFragment_News_Collection(0, personalUserId, id);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insert");
                            jsonObject.addProperty("share", new Gson().toJson(homeNewsFragment_news_collection));
                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);
                            } catch (Exception e) {
//                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
//                                Common.showToast(activity, R.string.msg_InsertFail);
                            } else {
//                                Common.showToast(activity, R.string.msg_CollectionSuccess);
                            }
                        } else {
                            Common.showToast(activity, R.string.msg_NoNetwork);
                        }
                    } else {
                        //使用者取消收藏時
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL + "/CollectionServlet";
                            HomeNewsFragment_News_Collection homeNewsFragment_news_collection;
                            homeNewsFragment_news_collection = new HomeNewsFragment_News_Collection(personalUserId, id);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "delete");
                            jsonObject.addProperty("delete", new Gson().toJson(homeNewsFragment_news_collection));
                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
//                                Common.showToast(activity, R.string.msg_InsertFail);
                            } else {
//                                Common.showToast(activity, R.string.msg_InsertSuccess);
                            }
                        } else {
                            Common.showToast(activity, R.string.msg_NoNetwork);
                        }
                    }
                }
            });

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

//        private void changeFragment(Fragment fragment) {
//            if (getActivity().getSupportFragmentManager() != null) {
//                getActivity().getSupportFragmentManager().beginTransaction().
//                        replace(R.id.content, fragment).addToBackStack(null).commit();
//            }
//        }
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
