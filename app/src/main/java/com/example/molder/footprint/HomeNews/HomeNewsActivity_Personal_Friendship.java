package com.example.molder.footprint.HomeNews;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.example.molder.footprint.HomeNews.FriendshipNotifyServer.connectServer;
import static com.example.molder.footprint.HomeNews.FriendshipNotifyServer.friendshipNotifyWebSocketClient;

public class HomeNewsActivity_Personal_Friendship extends Activity {
    private static String TAG = "HomeNewsActivity_Personal_Friendship";
    private String inviteeUserId,userId,userNickName,message;
    private CircleImageView ci_ProfilePicture;
    private TextView tv_userNickName;
    private EditText et_message;
    private CommonTask userIdTask;
    private HeadImageTask headImageTask;
    private Button button;
    private int imageSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        userId = preferences.getString("userId", "");
        Intent intent = getIntent();
        inviteeUserId = intent.getStringExtra("userId");
        setContentView(R.layout.activity_home_news__personal__friendship);

        connectServer(userId, this);
        handleView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 設定好友邀請對象
        FriendshipNotifyWebSocketClient.friendInChat = inviteeUserId;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // user有可能直接開啟此頁而非經過Notification，應將對應發訊者的Notification刪除
        if (notificationManager != null) {
            notificationManager.cancel(inviteeUserId.hashCode());
        }
    }

    public void handleView(){
//        Intent intent = getIntent();
//        inviteeUserId = intent.getStringExtra("userId");

        ci_ProfilePicture = findViewById(R.id.friendship_cvProfilePicture);
        tv_userNickName = findViewById(R.id.tvFriendship_UserName);
        et_message = findViewById(R.id.etFriendship);
        button = findViewById(R.id.button);
        imageSize = getResources().getDisplayMetrics().widthPixels/5;

        //找想加入好友的使用者暱稱和頭像
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/PicturesServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findUserNickName");
            jsonObject.addProperty("id", inviteeUserId);
            userIdTask = new CommonTask(url, jsonObject.toString());
            try {
                //顯示使用者暱稱
                String jsonIn = userIdTask.execute().get();
                userNickName = String.valueOf(jsonIn);
                tv_userNickName.setText(userNickName);

                //使用者頭像
                url = Common.URL + "/PicturesServlet";
                headImageTask = new HeadImageTask(url, inviteeUserId, imageSize, ci_ProfilePicture);
                headImageTask.execute();

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        //好友邀請按鍵的監聽器
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message == null) {
                    Toast.makeText(HomeNewsActivity_Personal_Friendship.this,"say something",Toast.LENGTH_LONG).show();
                } else {
                    message = et_message.getText().toString().trim();
                    if (Common.networkConnected(HomeNewsActivity_Personal_Friendship.this)) {
                        String url = Common.URL + "/FriendsServlet";
                        HomeNewsActivity_Personal_Friendship_Friends friends;
                        friends = new HomeNewsActivity_Personal_Friendship_Friends(userId,inviteeUserId,message);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "insert");
                        jsonObject.addProperty("share", new Gson().toJson(friends));
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                            et_message.setText(null);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
                            Common.showToast(HomeNewsActivity_Personal_Friendship.this, R.string.msg_InsertFail);
                        } else {
                            Common.showToast(HomeNewsActivity_Personal_Friendship.this, R.string.msg_InsertFrindSuccess);
                        }
                    } else {
                        Common.showToast(HomeNewsActivity_Personal_Friendship.this, R.string.msg_NoNetwork);
                    }
//                    HomeNewsActivity_Personal_Friendship_Friends friendsMessage = new HomeNewsActivity_Personal_Friendship_Friends("chat", userId, inviteeUserId, message, "text");
//                    String chatMessageJson = new Gson().toJson(friendsMessage);
//                    friendshipNotifyWebSocketClient.send(chatMessageJson);
//                    Log.d(TAG, "output: " + chatMessageJson);
                }
//                Intent intent = new Intent(HomeNewsActivity_Personal_Friendship.this, HomeNewsActivity_Personal.class);
//                intent.putExtra("userId", inviteeUserId);
//                startActivity(intent);
                HomeNewsActivity_Personal_Friendship_Friends friendsMessage = new HomeNewsActivity_Personal_Friendship_Friends("notify", userId, inviteeUserId, message, "text");
                String chatMessageJson = new Gson().toJson(friendsMessage);
                friendshipNotifyWebSocketClient.send(chatMessageJson);
                Log.d(TAG, "output: " + chatMessageJson);
                finish();
            }
        });
    }
}
