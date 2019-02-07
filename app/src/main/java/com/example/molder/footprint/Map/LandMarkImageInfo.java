package com.example.molder.footprint.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.CheckInShare.Picture;
import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Login.HeadImageTask;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandMarkImageInfo extends Activity {

    private static final String TAG = "TAG_LandMarkImageInfo";
    private ImageButton imageInfoBack;
    private TextView imageInfoTitle;
    private RecyclerView imageInfoRecyclerView;
    private String landMarkName;
    private int imagePosition;
    private InfoImageTask infoImageTask;
    private HeadImageTask headImageTask;
    private int imageSize;
    private CommonTask imageIdTask, retrieveLocationTask,userIdTask;
    private List<LandMark> locations = null;
    private List<Picture> pictures = null;
    private String userId,userNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_mark_image_info);
        handleView();
    }

    public void handleView() {
        imageInfoBack = findViewById(R.id.mapInfoImageBack);
        imageInfoTitle = findViewById(R.id.mapInfoImageTitle);
        imageInfoRecyclerView =findViewById(R.id.landMarkImageInfoRecyclerView);

        //取得地標的名稱與圖片id
        Intent intent = getIntent();
        landMarkName = intent.getStringExtra("landMarkName");
        imagePosition = intent.getIntExtra("position",0);

        imageInfoTitle.setText(landMarkName);

        //返回上一頁
        imageInfoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //取得地標資訊
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findLocationDetail");
            jsonObject.addProperty("name", landMarkName);
            //將內容轉成json字串
            retrieveLocationTask = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveLocationTask.execute().get();
                Type listType = new TypeToken<List<LandMark>>() {
                }.getType();
                //解析 json to gson
                locations = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (locations == null || locations.isEmpty()) {
                Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
            } else {
                LandMark location = locations.get(0);
                //找出地標裡所有照片id
                if (Common.networkConnected(this)) {
                    url = Common.URL + "/LocationServlet";
                    jsonObject.addProperty("action", "findImageId");
                    jsonObject.addProperty("id", location.getId());
                    //將內容轉成json字串
                    retrieveLocationTask = new CommonTask(url, jsonObject.toString());
                    try {
                        String jsonIn = retrieveLocationTask.execute().get();
                        Type listType = new TypeToken<List<Picture>>() {
                        }.getType();
                        //解析 json to gson
                        pictures = new Gson().fromJson(jsonIn, listType);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (pictures == null || pictures.isEmpty()) {
                        Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
                    } else {
                        //recycleView
                        imageInfoRecyclerView.setLayoutManager(
                                new StaggeredGridLayoutManager(1,
                                        StaggeredGridLayoutManager.VERTICAL));
                        imageInfoRecyclerView.setAdapter(new PictureInfoAdapter(this, pictures));
//                        imageInfoRecyclerView.smoothScrollToPosition(imagePosition);
                        imageInfoRecyclerView.scrollToPosition(imagePosition);
                    }
                } else {
                    Toast.makeText(this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }

        /* 不處理捲動事件所以監聽器設為null */
        imageInfoRecyclerView.setOnFlingListener(null);
        /* 如果希望一次滑動一頁資料，要加上PagerSnapHelper物件 */
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(imageInfoRecyclerView);

    }

    private class PictureInfoAdapter extends
            RecyclerView.Adapter<PictureInfoAdapter.MyViewHolder> {
        private Context context;
        private List<Picture> pictureList;

        PictureInfoAdapter(Context context, List<Picture> pictureList) {
            this.context = context;
            this.pictureList = pictureList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            CircleImageView profilePicture;
            TextView userName;
            ImageButton like,message,collection;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.landMarkImageInfoItem);
                profilePicture = itemView.findViewById(R.id.landMarkImageInfoProfilePicture);
                userName = itemView.findViewById(R.id.landMarkImageInfoItemUserName);
                like = itemView.findViewById(R.id.landMarkImageInfoItemLike);
                message = itemView.findViewById(R.id.landMarkImageInfoMessage);
                collection = itemView.findViewById(R.id.landMarkImageInfoItemCollection);
            }
        }

        @Override
        public int getItemCount() {
            return pictureList.size();
        }


        @NonNull
        @Override
        public PictureInfoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.land_mark_image_info_item, viewGroup, false);
            return new PictureInfoAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PictureInfoAdapter.MyViewHolder viewHolder, int position) {
            final Picture picture = pictureList.get(position);
            String url = Common.URL + "/LocationServlet";
            int id = picture.getImageID();
            infoImageTask = new InfoImageTask(url, id, imageSize, viewHolder.imageView);
            infoImageTask.execute();

            //先抓userId
            if (Common.networkConnected(LandMarkImageInfo.this)) {
                url = Common.URL + "/AccountServlet";
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
                if (pictures == null || pictures.isEmpty()) {
                    Toast.makeText(LandMarkImageInfo.this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
                } else {
                    //再抓 user 暱稱
                    url = Common.URL + "/AccountServlet";
                    jsonObject.addProperty("action", "findUserNickName");
                    jsonObject.addProperty("id", userId);
                    //將內容轉成json字串
                    userIdTask = new CommonTask(url, jsonObject.toString());
                    try {
                        String jsonIn = userIdTask.execute().get();
                        userNickName = String.valueOf(jsonIn);
                        //顯示使用者暱稱
                        viewHolder.userName.setText(userNickName);

                        //使用者頭像
                        url = Common.URL + "/AccountServlet";
                        headImageTask = new HeadImageTask(url, userId, imageSize, viewHolder.profilePicture);
                        headImageTask.execute();

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }


                }
            } else {
                Toast.makeText(LandMarkImageInfo.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
            }

            //頭像點擊
            viewHolder.profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(LandMarkImageInfo.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }
            });

            //讚點擊
            viewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(LandMarkImageInfo.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }
            });
            //對話點擊
            viewHolder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(LandMarkImageInfo.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }
            });
            //分享點擊
            viewHolder.collection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(LandMarkImageInfo.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
