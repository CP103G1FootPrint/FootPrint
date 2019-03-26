package com.example.molder.footprint.Schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Message;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Message_Messages;
import com.example.molder.footprint.HomeNews.HomeNewsFragment_News;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScheduleChatActivity extends AppCompatActivity {
    private static String TAG = "ScheduleChatActivity";
    private CircleImageView profile＿picture, personalUserHeadPicture;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView nickName;
    private ImageButton sendButton;
    private EditText commentMessage;
    private RecyclerView recyclerView;
    private String userId, userNickName, description, personalUserId, message,guestId,createID;
    private CommonTask userIdTask, messageTask;
    private HeadImageTask headImageTask;
    private int imageSize, imageId;
    private List<ScheduleChatActivity_Messages> newsMessage;
    private ScheduleChatActivity_Messages messages;
    private ScheduleChatActivity_MessageAdapter messageAdapter;
    private int tripId ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_all_chat);
        recyclerView = findViewById(R.id.sh_rv_home_news_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//這邊輸入語法可以設定水平或垂直呈現
        imageSize = getResources().getDisplayMetrics().widthPixels;

        //刷新資料
        swipeRefreshLayout = this.findViewById(R.id.sh_message_SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showResults();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

//        handleViews();
        showResults();
    }

    private void showResults() {
//        profile＿picture = findViewById(R.id.ci_profile＿picture);
//        nickName = findViewById(R.id.tv_news_userName);

        sendButton = findViewById(R.id.sh_bt_Comment);
        commentMessage = findViewById(R.id.sh_ti_Go_To_Comment);
        personalUserHeadPicture = findViewById(R.id.sh_ci_Comment_user);

        int location = commentMessage.length();
        commentMessage.setSelection(location);

        //打開上一頁的打包的資料
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Trip trip = (Trip) bundle.getSerializable("trip");
            if (trip != null) {
                createID = trip.getCreateID();
                tripId = trip.getTripID();

            }
        }

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
            String url = Common.URL + "/ChatMessageServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findAllMessage");
            jsonObject.addProperty("tripId", tripId);
            String jsonOut = jsonObject.toString();
            messageTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = messageTask.execute().get();
                Type listType = new TypeToken<List<ScheduleChatActivity_Messages>>() {
                }.getType();
                newsMessage = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (newsMessage == null || newsMessage.isEmpty()) {
//                Toast.makeText(this, R.string.msg_NoNewsFound, Toast.LENGTH_SHORT).show();
            } else {
                messageAdapter = new ScheduleChatActivity_MessageAdapter(newsMessage);
                recyclerView.setAdapter(messageAdapter);
            }
        }

        //留言按鍵的監聽器
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentMessage == null) {
                    // Toast.makeText(this, R.string.msg_NoMessage, Toast.LENGTH_SHORT).show();
                }
                if (personalUserId == null) {
                    // Toast.makeText(this, R.string.msg_NoMessage, Toast.LENGTH_SHORT).show();
                } else {
                    insertMessage();
                    onGetMessagesSuccess(messages);

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
            String url = Common.URL + "/ChatMessageServlet";
            messages = new ScheduleChatActivity_Messages(0, personalUserId, message, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insert");
            jsonObject.addProperty("share", new Gson().toJson(messages));
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
                commentMessage.setText(null);

            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (count == 0) {
//                Common.showToast(this, R.string.msg_InsertFail);
            } else {
//                Common.showToast(this, R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }


    public void onGetMessagesSuccess(ScheduleChatActivity_Messages message) {
        if (messageAdapter == null) {
            messageAdapter = new ScheduleChatActivity_MessageAdapter(new ArrayList<ScheduleChatActivity_Messages>());
            recyclerView.setAdapter(messageAdapter);
        }
        messageAdapter.add(message);
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private class ScheduleChatActivity_MessageAdapter extends
            RecyclerView.Adapter<ScheduleChatActivity.ScheduleChatActivity_MessageAdapter.MyViewHolder> {
        Context context;
//        List<ScheduleChatActivity_Messages> newsMessage;

        public ScheduleChatActivity_MessageAdapter(List<ScheduleChatActivity_Messages> newsMessages) {
//            this.context = context;
            newsMessage = newsMessages;
            imageSize = getResources().getDisplayMetrics().widthPixels / 3; //getDisplayMetrics()取得目前螢幕

        }

        public void add(ScheduleChatActivity_Messages message) {
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
        public ScheduleChatActivity.ScheduleChatActivity_MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = layoutInflater.inflate(R.layout.activity_home_news__message_item, viewGroup, false);
            return new ScheduleChatActivity.ScheduleChatActivity_MessageAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ScheduleChatActivity.ScheduleChatActivity_MessageAdapter.MyViewHolder holder, int i) {
            final ScheduleChatActivity_Messages news_Message = newsMessage.get(i);
            //顯示留言
            holder.tv_news_Comment.setText(news_Message.getMessage());
            guestId = news_Message.getUserId();

            //用留言id的使用者id尋找暱稱和頭像
            if (Common.networkConnected(ScheduleChatActivity.this)) {
                String url = Common.URL + "/ChatMessageServlet";
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
//        recyclerView = findViewById(R.id.sh_rv_home_news_message);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));//這邊輸入語法可以設定水平或垂直呈現
//        List<ScheduleChatActivity_Messages> newsMessageList = getNewsMessage();
//        recyclerView.setAdapter(new ScheduleChatActivity.ScheduleChatActivity_MessageAdapter(this, newsMessageList));
//    }

//    protected List<ScheduleChatActivity_Messages> getNewsMessage() {
//        List<ScheduleChatActivity_Messages> newsMessage = new ArrayList<>();
//        return newsMessage;
//    }
}
