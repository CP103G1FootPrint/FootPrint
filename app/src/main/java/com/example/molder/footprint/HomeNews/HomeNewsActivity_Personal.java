package com.example.molder.footprint.HomeNews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.Map.LandMark;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeNewsActivity_Personal extends AppCompatActivity {
    private static String TAG = "TAG_HomeNewsFragmentPersonal";
    private CircleImageView profile＿picture;
    private TextView nickName, userName;
    private RecyclerView recyclerView;
    private CheckBox home_news_personal_addFriend;
    private AppCompatActivity HomeNewsActivity_Personal;
    private HeadImageTask headImageTask;
    private CommonTask userIdTask, personalPicturesTask, userTask;
    private String userId, userNickName, userNowId;
    private ImageTask picturesTask;
    private int imageSize;
    private List<HomeNewsFragment_PersonalPictures> personalPictures = null;
    private List<HomeNewsActivity_Personal_Friendship_Friends> friendship_Friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_news__personal);
        imageSize = getResources().getDisplayMetrics().widthPixels;
//        handleViews();
    }
    @Override
    public void onStart() {
        super.onStart();
        handleViews();
    }

    private void handleViews() {
        profile＿picture = findViewById(R.id.ci_profile＿picture);
        nickName = findViewById(R.id.tv_home_news_personal_NickName_);
        userName = findViewById(R.id.tv_home_news_personal_ID_);
        home_news_personal_addFriend = findViewById(R.id.home_news_personal_addFriend);
        recyclerView = findViewById(R.id.rv_home_news_personal_pictures);


        // 取得上一頁userId
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        //取得上一頁userId
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            HomeNewsFragment_News homeNewsFragment_news = (HomeNewsFragment_News) bundle.getSerializable("news");
            LandMark landMark = (LandMark) bundle.getSerializable("landMarkImageInfo");
            if (homeNewsFragment_news != null) {
                userId = homeNewsFragment_news.getUserID();
            }else if(landMark != null){
                userId = landMark.getAccount();
            }
        }
        userName.setText(userId);

        //先抓userId
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/PicturesServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findUserNickName");
            jsonObject.addProperty("id", userId);
            userIdTask = new CommonTask(url, jsonObject.toString());
            try {
                //顯示使用者暱稱
                String jsonIn = userIdTask.execute().get();
                userNickName = String.valueOf(jsonIn);
                nickName.setText(userNickName);

                //使用者頭像
                url = Common.URL + "/PicturesServlet";
                headImageTask = new HeadImageTask(url, userId, imageSize, profile＿picture);
                headImageTask.execute();

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            //用userId 找該使用者上傳的照片id
            if (Common.networkConnected(this)) {
                url = Common.URL + "/PicturesServlet";
                jsonObject.addProperty("action", "findPersonalImageId");
                jsonObject.addProperty("userId", userId);
                String jsonOut = jsonObject.toString();
                personalPicturesTask = new CommonTask(url, jsonOut);
                try {
                    String jsonIn = personalPicturesTask.execute().get();
                    Type listType = new TypeToken<List<HomeNewsFragment_PersonalPictures>>() {
                    }.getType();
                    personalPictures = new Gson().fromJson(jsonIn, listType);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (personalPictures == null || personalPictures.isEmpty()) {
                    Toast.makeText(this, R.string.msg_NoNewsFound, Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setAdapter(new PersonalPicturesAdapter(this, personalPictures));
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
                            StaggeredGridLayoutManager.VERTICAL));
                }
            }
        }
        //取得目前登入使用者id
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        userNowId = preferences.getString("userId", "");
        //判斷欲加好友的使用者id和目前登入使用者的id是否相同
        if (userId.equals(userNowId)) {
            home_news_personal_addFriend.setVisibility(View.INVISIBLE);
        } else {
            home_news_personal_addFriend.setVisibility(View.VISIBLE);
        }

        //判斷兩人是否為好友
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/FriendsServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findFriendId");
            jsonObject.addProperty("userId", userNowId);
            jsonObject.addProperty("inviteeId", userId);
            String jsonOut = jsonObject.toString();
            userTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = userTask.execute().get();
                Type listType = new TypeToken<List<HomeNewsActivity_Personal_Friendship_Friends>>() {
                }.getType();
                friendship_Friends = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (friendship_Friends == null || friendship_Friends.isEmpty()) {
                home_news_personal_addFriend.setChecked(false);
            } else {
                home_news_personal_addFriend.setChecked(true);
            }
        }

        if(home_news_personal_addFriend.isChecked()){
            home_news_personal_addFriend.setClickable(false);
            home_news_personal_addFriend.setFocusable(false);
            home_news_personal_addFriend.setFocusableInTouchMode(false);
            home_news_personal_addFriend.setEnabled(false);
        }else {
            home_news_personal_addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    home_news_personal_addFriend.setChecked(false);
                    Intent intent = new Intent(HomeNewsActivity_Personal.this, HomeNewsActivity_Personal_Friendship.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
//            }); {
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(home_news_personal_addFriend.isChecked()){
//                        home_news_personal_addFriend.setClickable(false);
//                        home_news_personal_addFriend.setFocusable(false);
//                        home_news_personal_addFriend.setFocusableInTouchMode(false);
//                        home_news_personal_addFriend.setEnabled(false);
////
//                    }else{
//                        Intent intent = new Intent(HomeNewsActivity_Personal.this, HomeNewsActivity_Personal_Friendship.class);
//                        intent.putExtra("userId", userId);
//                        startActivity(intent);
//
//                    }
//                }
//            });
            });

        }
//        home_news_personal_addFriend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeNewsActivity_Personal.this, HomeNewsActivity_Personal_Friendship.class);
//                intent.putExtra("userId", userId);
//                startActivity(intent);
//            }
//        });
    }

    //使用者照片的recyclerView
    private class PersonalPicturesAdapter extends
            RecyclerView.Adapter<PersonalPicturesAdapter.MyViewHolder> {
        Context context;
        private List<HomeNewsFragment_PersonalPictures> personalPictures;
        private int imageSize;

        PersonalPicturesAdapter(Context context,
                                List<HomeNewsFragment_PersonalPictures> PersonalPictures) {
            this.context = context;
            this.personalPictures = PersonalPictures;
            imageSize = getResources().getDisplayMetrics().widthPixels / 3; //getDisplayMetrics()取得目前螢幕
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.rv_home_news_personal_pictureItem);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.activity_home_news_personal_pictureitem, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final HomeNewsFragment_PersonalPictures personalPicture = personalPictures.get(position);
            String url = Common.URL + "/PicturesServlet";
            final int id = personalPicture.getImageID();
            picturesTask = new ImageTask(url, id, imageSize, holder.imageView);
            picturesTask.execute();
        }

        @Override
        public int getItemCount() {
            return personalPictures.size();
        }
    }
}
