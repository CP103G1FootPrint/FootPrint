package com.example.molder.footprint.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.CheckInShare.Picture;
import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.HomeNews.HomeNewsFragment_News;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LandMarkInfo extends AppCompatActivity {

    private static final String TAG = "TAG_LandMarkInfo";
    private ImageButton mapInfoDetailBack, mapInfoDetailPlan;
    private TextView mapInfoDetailTitle, mapInfoDetailAddress, mapInfoDetailDescription, mapInfoDetailOpenHours;
    private ImageView mapInfoDetailPicture;
    private RatingBar mapInfoDetailRatingBar;
    private RecyclerView mapInfoDetailRecyclerView;
    private String landMarkName;
    private int imageSize;
    private ImageTask landMarkImageTask;
    private InfoImageTask infoImageTask;
    private CommonTask imageIdTask, retrieveLocationTask,landMarkIdTask;
    private int imageId,landMarkId;
    private Boolean imageIdCheck = false;
    private List<LandMark> locations = null;
    private List<Picture> pictures = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_mark_info);
        imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        handleView();
    }

    public void handleView() {
        mapInfoDetailBack = findViewById(R.id.mapInfoDetailBack);
        mapInfoDetailPlan = findViewById(R.id.mapInfoDetailPlan);
        mapInfoDetailTitle = findViewById(R.id.mapInfoDetailTitle);
        mapInfoDetailAddress = findViewById(R.id.mapInfoDetailAddress);
        mapInfoDetailDescription = findViewById(R.id.mapInfoDetailDescription);
        mapInfoDetailOpenHours = findViewById(R.id.mapInfoDetailOpenHours);
        mapInfoDetailPicture = findViewById(R.id.mapInfoDetailPicture);
        mapInfoDetailRatingBar = findViewById(R.id.mapInfoDetailRatingBar);
        mapInfoDetailRecyclerView = findViewById(R.id.mapInfoDetailRecyclerView);

        //取得地標的名稱
        if(landMarkName!=null) {
            Intent intent = getIntent();
            landMarkName = intent.getStringExtra("landMarkName");
        }else{
            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                HomeNewsFragment_News homeNewsFragment_news = (HomeNewsFragment_News) bundle.getSerializable("landMarkName");
                if (homeNewsFragment_news != null){
                    landMarkId = homeNewsFragment_news.getLandMarkID();
                }
            }if(Common.networkConnected(this)) {
                String url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findLandMark");
                jsonObject.addProperty("id", landMarkId);
                landMarkIdTask = new CommonTask(url, jsonObject.toString());
                try {
                    String jsonIn = landMarkIdTask.execute().get();
                    landMarkName = String.valueOf(jsonIn);
                    mapInfoDetailTitle.setText(landMarkName);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }

        //返回上一頁
        mapInfoDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //加入行程
        mapInfoDetailPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳到選行程？
                Toast.makeText(LandMarkInfo.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
            }
        });

        //用title＝landMark name 比對找出landMarkID 就可以知道imageID
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findLocationId");
            jsonObject.addProperty("name", landMarkName);
            String jsonOut = jsonObject.toString();
            imageIdTask = new CommonTask(url, jsonOut);
            try {
                String result = imageIdTask.execute().get();
                imageId = Integer.valueOf(result);
                imageIdCheck = true;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
            imageIdCheck = false;
        }
        //顯示圖片
        if (imageIdCheck) {
            String url = Common.URL + "/LocationServlet";
            int id = imageId;
            Bitmap bitmap = null;
            try {
                landMarkImageTask = new ImageTask(url, id, imageSize);
                // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
                bitmap = landMarkImageTask.execute().get();
                imageIdCheck = false;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                mapInfoDetailPicture.setImageBitmap(bitmap);
            } else {
                mapInfoDetailPicture.setImageResource(R.drawable.default_image);
                imageIdCheck = false;
            }
        }

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
                //顯示地標名稱
                mapInfoDetailTitle.setText(location.getName());
                //顯示地標地址
                mapInfoDetailAddress.setText("Address : " + location.getAddress());
                //顯示地標描述
                mapInfoDetailDescription.setText("Description : " + location.getDescription());
                //顯示地標開放時間
                mapInfoDetailOpenHours.setText("Open Hours : " + location.getOpeningHours());

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
                        mapInfoDetailRecyclerView.setLayoutManager(
                                new StaggeredGridLayoutManager(3,
                                        StaggeredGridLayoutManager.VERTICAL));
                        mapInfoDetailRecyclerView.setAdapter(new PictureAdapter(this, pictures));
                    }
                } else {
                    Toast.makeText(this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onStop() {
        super.onStop();

        if (landMarkImageTask != null) {
            landMarkImageTask.cancel(true);
            landMarkImageTask = null;
        }

        if (imageIdTask != null) {
            imageIdTask.cancel(true);
            imageIdTask = null;
        }

        if (retrieveLocationTask != null) {
            retrieveLocationTask.cancel(true);
            retrieveLocationTask = null;
        }

        if (infoImageTask != null) {
            infoImageTask.cancel(true);
            infoImageTask = null;
        }

    }

    private class PictureAdapter extends
            RecyclerView.Adapter<PictureAdapter.MyViewHolder> {
        private Context context;
        private List<Picture> pictureList;

        PictureAdapter(Context context, List<Picture> pictureList) {
            this.context = context;
            this.pictureList = pictureList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;


            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.mapInfoDetailImageView);

            }
        }

        @Override
        public int getItemCount() {
            return pictureList.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.map_info_detail_image_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
            final Picture picture = pictureList.get(position);
            String url = Common.URL + "/LocationServlet";
            int id = picture.getImageID();
            infoImageTask = new InfoImageTask(url, id, imageSize, viewHolder.imageView);
            infoImageTask.execute();
            final String text = String.valueOf(position);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //點選照片跳到照片詳細內容
                    Toast.makeText(LandMarkInfo.this, text, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LandMarkInfo.this, LandMarkImageInfo.class);
                    intent.putExtra("landMarkName",landMarkName);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            });
        }
    }

}
