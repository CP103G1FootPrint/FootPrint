package com.example.molder.footprint.HomeNews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Home;
import com.example.molder.footprint.R;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeNewsActivity_Message extends AppCompatActivity {
    private static String TAG = "HomeNewsActivity_Message";
    private CircleImageView profile＿picture, personalUserHeadPicture;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView nickName, tv_description;
    private ImageButton sendButton;
    private EditText commentMessage;
    private RecyclerView recyclerView;
    private String userId, userNickName, description, personalUserId, message,guestId;
    private CommonTask userIdTask, messageTask;
    private HeadImageTask headImageTask;
    private int imageSize, imageId;
    private List<HomeNewsActivity_Message_Messages> newsMessage;
    private HomeNewsActivity_Message_Messages messages;
    private HomeNews_MessageAdapter homeNews_messageAdapter;
//    private static final String SERVER_URI = "ws://10.0.2.2:8080/WSChatBasic_Web/AllChatServer/";
    private static final String SERVER_URI = "ws://sewd.no-ip.org:8080/WSChatBasic_Web/AllChatServer/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_news__message);
        imageSize = getResources().getDisplayMetrics().widthPixels/10;
        recyclerView = findViewById(R.id.rv_home_news_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//這邊輸入語法可以設定水平或垂直呈現
        //刷新資料
        swipeRefreshLayout = this.findViewById(R.id.message_SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showResults();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        showResults();
    }

    private void showResults() {
        profile＿picture = findViewById(R.id.ci_profile＿picture);
        nickName = findViewById(R.id.tv_news_userName);
        tv_description = findViewById(R.id.tv_news_UserDescription);
        sendButton = findViewById(R.id.bt_Comment);
        commentMessage = findViewById(R.id.ti_Go_To_Comment);
        personalUserHeadPicture = findViewById(R.id.ci_Comment_user);
        //打開上一頁的打包的資料
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            HomeNewsFragment_News homeNewsFragmentNews = (HomeNewsFragment_News) bundle.getSerializable("news");
            if (homeNewsFragmentNews != null) {
                userId = homeNewsFragmentNews.getUserID();
                description = homeNewsFragmentNews.getDescription();
                imageId = homeNewsFragmentNews.getImageID();
            }
        }

        //顯示照片描述
        tv_description.setText(description);

        //用userID 尋找使用者暱稱和頭像
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
//                Log.e(TAG, e.toString());
            }
        }

        //取得目前登入使用者id
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        personalUserId = preferences.getString("userId", "");

        //用目前登入的使用者ID尋找使用者頭像
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/PicturesServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findUserNickName");
            jsonObject.addProperty("userId", personalUserId);
            userIdTask = new CommonTask(url, jsonObject.toString());
            try {
                //使用者頭像
                url = Common.URL + "/PicturesServlet";
                headImageTask = new HeadImageTask(url, personalUserId, imageSize, personalUserHeadPicture);
                headImageTask.execute();

            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
        }

        //用照片id尋找所有留言
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/CommentServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findAllMessage");
            jsonObject.addProperty("imageId", imageId);
            String jsonOut = jsonObject.toString();
            messageTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = messageTask.execute().get();
                Type listType = new TypeToken<List<HomeNewsActivity_Message_Messages>>() {
                }.getType();
                newsMessage = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (newsMessage == null || newsMessage.isEmpty()) {
                Toast.makeText(this, R.string.msg_NoNewsFound, Toast.LENGTH_SHORT).show();
            } else {
                homeNews_messageAdapter = new HomeNews_MessageAdapter(newsMessage);
                recyclerView.setAdapter(homeNews_messageAdapter);
            }
        }

        //留言按鍵的監聽器
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentMessage == null) {
                    Toast.makeText(HomeNewsActivity_Message.this,"uhh",Toast.LENGTH_LONG).show();
                } else {
                    insertMessage();
                    onGetMessagesSuccess(messages);
//                  HomeNews_MessageAdapter;
//                  HomeNewsActivity_Message.this.notifyDataSetChanged();
//                    homeNews_messageAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    //上傳留言的方法
    private void insertMessage() {
        message = commentMessage.getText().toString().trim();
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        personalUserId = preferences.getString("userId", "");
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/CommentServlet";
//            HomeNewsActivity_Message_Messages messages;
            messages = new HomeNewsActivity_Message_Messages(0, personalUserId, message, imageId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insert");
            jsonObject.addProperty("share", new Gson().toJson(messages));
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
                commentMessage.setText(null);
//              Intent intent = new Intent(this, HomeNewsActivity_Message.class);
//              startActivity(intent);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Common.showToast(this, R.string.msg_InsertFail);
            } else {
                Common.showToast(this, R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    public void onGetMessagesSuccess(HomeNewsActivity_Message_Messages message) {
        if (homeNews_messageAdapter == null) {
            homeNews_messageAdapter = new HomeNews_MessageAdapter(new ArrayList<HomeNewsActivity_Message_Messages>());
            recyclerView.setAdapter(homeNews_messageAdapter);
        }
        homeNews_messageAdapter.add(message);
        recyclerView.smoothScrollToPosition(homeNews_messageAdapter.getItemCount() - 1);
    }

    private class HomeNews_MessageAdapter extends RecyclerView.Adapter<HomeNews_MessageAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        Context context;
//        List<HomeNewsActivity_Message_Messages> newsMessage;

        public HomeNews_MessageAdapter(List<HomeNewsActivity_Message_Messages> newsMessages) {
            newsMessage = newsMessages;
            imageSize = getResources().getDisplayMetrics().widthPixels / 20; //getDisplayMetrics()取得目前螢幕

        }
        public void add(HomeNewsActivity_Message_Messages message) {
            newsMessage.add(message);
            notifyItemInserted(newsMessage.size() - 1);
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView guestHeadPicture;
            TextView tv_news_userName;
            TextView tv_news_Comment;

            public MyViewHolder(View itemView) {
                super(itemView);
                guestHeadPicture = itemView.findViewById(R.id.message_ci_profile＿picture);
                tv_news_userName = itemView.findViewById(R.id.message_tv_news_userName);
                tv_news_Comment = itemView.findViewById(R.id.message_tv_news_Comment);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            MyViewHolder myViewHolder = null;
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = layoutInflater.inflate(R.layout.activity_home_news__message_item, viewGroup, false);
            myViewHolder = new MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
            final HomeNewsActivity_Message_Messages news_Message = newsMessage.get(i);
            //顯示留言
            holder.tv_news_Comment.setText(news_Message.getMessage());
            guestId = news_Message.getUserId();

            //用留言id的使用者id尋找暱稱和頭像
            if (Common.networkConnected(HomeNewsActivity_Message.this)) {
                String url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findUserNickName");
                jsonObject.addProperty("id", guestId);
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {
                    //顯示使用者暱稱
                    String jsonIn = userIdTask.execute().get();
                    userNickName = String.valueOf(jsonIn);
                    holder.tv_news_userName.setText(userNickName);

                    //使用者頭像
                    url = Common.URL + "/PicturesServlet";
                    headImageTask = new HeadImageTask(url, guestId, imageSize, holder.guestHeadPicture);
                    headImageTask.execute();

                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
                }
            }

        }

        @Override
        public int getItemCount() {
            return newsMessage.size();
        }
    }

//    private void handleViews() {
//        recyclerView = findViewById(R.id.rv_home_news_message);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));//這邊輸入語法可以設定水平或垂直呈現
//        List<HomeNewsActivity_Message_Messages> newsMessageList = getNewsMessage();
//        recyclerView.setAdapter(new HomeNews_MessageAdapter(newsMessageList));
//    }

//    protected List<HomeNewsActivity_Message_Messages> getNewsMessage() {
//        List<HomeNewsActivity_Message_Messages> newsMessage = new ArrayList<>();
//        return newsMessage;
//    }
}

