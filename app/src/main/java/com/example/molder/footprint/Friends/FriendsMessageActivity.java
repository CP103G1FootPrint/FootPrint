package com.example.molder.footprint.Friends;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.WebSocketClient;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Message;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Message_Messages;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Personal_Friendship_Friends;
import com.example.molder.footprint.HomeNews.HomeNewsFragment_News;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.molder.footprint.Common.Common.getUserId;
import static com.example.molder.footprint.Common.Common.webSocketClient;
import static com.example.molder.footprint.Friends.Common.chatWebSocketClient;
import static com.example.molder.footprint.Friends.Common.connectServer;


public class FriendsMessageActivity extends AppCompatActivity {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    private static final String TAG = "FriendsMessageActivity";
    private LocalBroadcastManager broadcastManager;
    private EditText etMessage;
    private ImageButton ivSend;
    private String friend, sender;
    private CommonTask chatMessageTask, userIdTask;
    private List<ChatMessage> chatMessage;
    private RecyclerView recyclerView;
    private String dbSender;
    private HeadImageTask headImageTask;
    private FriendsMessageActivityAdapter friendsMessageActivityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_message);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerChatReceiver();

        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        sender = preferences.getString("userId", "");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            HomeNewsActivity_Personal_Friendship_Friends friendship = (HomeNewsActivity_Personal_Friendship_Friends) bundle.getSerializable("friend");
            if (friendship != null) {
                friend = friendship.getInvitee();
                if (friend.equals(sender)) {
                    friend = friendship.getInviter();
                }
            }else{
                ChatMessage chatMessage = (ChatMessage) bundle.getSerializable("chatMessage");
                if (chatMessage != null) {
                    friend = chatMessage.getSender();
                    if (friend.equals(sender)) {
                        friend = chatMessage.getReceiver();
                    }
                }
            }
        }

//      String friend = getIntent().getStringExtra("friend");
//        setTitle("friend: " + friend);
        connectServer(sender, this);
        handleViews();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 設定目前聊天對象
        ChatWebSocketClient.friendInChat = friend;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // user有可能直接開啟此頁而非經過Notification，應將對應發訊者的Notification刪除
        if (notificationManager != null) {
            notificationManager.cancel(friend.hashCode());
        }
    }

    private void handleViews() {
        etMessage = findViewById(R.id.ti_Go_To_Comment);
        recyclerView = findViewById(R.id.rv_friendChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//這邊輸入語法可以設定水平或垂直呈現
//      scrollView = findViewById(R.id.scrollView);
//      layout = findViewById(R.id.layout);

        //抓所有聊天記錄
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/FriendsMessageServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findAllMessage");
            jsonObject.addProperty("senderId", sender);
            jsonObject.addProperty("receiverId", friend);
            String jsonOut = jsonObject.toString();
            chatMessageTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = chatMessageTask.execute().get();
                Type listType = new TypeToken<List<ChatMessage>>() {
                }.getType();
                chatMessage = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (chatMessage == null || chatMessage.isEmpty()) {
                Toast.makeText(this, R.string.msg_NoNewsFound, Toast.LENGTH_SHORT).show();
            } else {
//                recyclerView.setAdapter(friendsMessageActivityAdapter);
                friendsMessageActivityAdapter = new FriendsMessageActivityAdapter(chatMessage);
                recyclerView.setAdapter(friendsMessageActivityAdapter);
                recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount() - 1);
//                recyclerView.setAdapter(new FriendsMessageActivityAdapter(chatMessage));
            }
        }

        //發送鍵 監聽
        ivSend = findViewById(R.id.bt_SendMessage);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Intent intent;
                String message = etMessage.getText().toString();
                if (message.trim().isEmpty()) {
                  showToast(R.string.text_MessageEmpty);
                  return;
                }

                //新增聊天記錄
                if (Common.networkConnected(FriendsMessageActivity.this)) {
                    String url = Common.URL + "/FriendsMessageServlet";
//                    ChatMessage messages;
                    ChatMessage messages = new ChatMessage(sender,friend,message);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "insert");
                    jsonObject.addProperty("share", new Gson().toJson(messages));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                        etMessage.setText(null);

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(FriendsMessageActivity.this, R.string.msg_InsertFail);
                    } else {
                        Common.showToast(FriendsMessageActivity.this, R.string.msg_InsertSuccess);
                    }
                } else {
                    Common.showToast(FriendsMessageActivity.this, R.string.msg_NoNetwork);
                }

//                showMessage(sender, message, false);
                // 將欲傳送的對話訊息轉成JSON後送出

                ChatMessage chatMessages = new ChatMessage("chat", sender, friend, message, "text");
//                chatMessage.add(chatMessages);
                onGetMessagesSuccess(chatMessages);
//                onGetMessagesSuccess(chatMessage);
                String chatMessageJson = new Gson().toJson(chatMessages);
                chatWebSocketClient.send(chatMessageJson);
                Log.d(TAG, "output: " + chatMessageJson);
            }

        });

    }

//    public void onGetMessagesSuccess(List<ChatMessage> chatMessage) {
//        if (friendsMessageActivityAdapter == null) {
////            friendsMessageActivityAdapter = new FriendsMessageActivityAdapter(new ArrayList<ChatMessage>());
//            recyclerView.setAdapter(new FriendsMessageActivityAdapter(chatMessage));
//        }else{
//            recyclerView.setAdapter(new FriendsMessageActivityAdapter(chatMessage));
//            recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount());
//        }

//        ChatMessage c = chatMessage.get(chatMessage.size()-1);
//        friendsMessageActivityAdapter.add(c);

//        if(chatMessage.size() != 0){
//            recyclerView.setAdapter(new FriendsMessageActivityAdapter(chatMessage));
//            recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount());
//            recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount() - 1);
//        }
//        recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount());
//        recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount());
//        friendsMessageActivityAdapter.notifyDataSetChanged();
//    }

    public void onGetMessagesSuccess(ChatMessage chatMessage) {
        if (friendsMessageActivityAdapter == null) {
            friendsMessageActivityAdapter = new FriendsMessageActivityAdapter(new ArrayList<ChatMessage>());
            recyclerView.setAdapter(friendsMessageActivityAdapter);
        }
           friendsMessageActivityAdapter.add(chatMessage);
           recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount() - 1);
// else {
//            friendsMessageActivityAdapter.add(chatMessage);
//            recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount() - 1);
//        }
    }

    public class FriendsMessageActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
//      private List<ChatMessage> chatMessage;
        private int imageSize;


        public FriendsMessageActivityAdapter(List<ChatMessage> chatMessages) {
//            this.context = context;
            chatMessage = chatMessages    ;
            imageSize = getResources().getDisplayMetrics().widthPixels/10;
        }

        public void add(ChatMessage message) {
            chatMessage.add(message);
            notifyItemInserted(chatMessage.size() - 1);
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
//            LayoutInflater layoutInflater = LayoutInflater.from(context);
            RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case VIEW_TYPE_ME:
                    View viewChatMine = layoutInflater.inflate(R.layout.chat_message_right, viewGroup, false);
                    viewHolder = new MyChatViewHolder(viewChatMine);
                    break;
                case VIEW_TYPE_OTHER:
                    View viewChatOther = layoutInflater.inflate(R.layout.chat_message_left, viewGroup, false);
                    viewHolder = new OtherChatViewHolder(viewChatOther);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatMessage chatMessages = chatMessage.get(position);
            dbSender = chatMessages.getSender();
            if (sender.equals(dbSender)) {
                configureMyChatViewHolder((MyChatViewHolder) holder, position);
            } else {
                configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
            }
        }

        private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
            ChatMessage chatMessages = chatMessage.get(position);
//            String senderId = chatMessages.getSender();
//            String userNickName = "";
//            if (Common.networkConnected(FriendsMessageActivity.this)) {
//                String url = Common.URL + "/PicturesServlet";
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("action", "findUserNickName");
//                jsonObject.addProperty("id", senderId);
//                userIdTask = new CommonTask(url, jsonObject.toString());
//                try {
//                    //顯示使用者暱稱
//                    String jsonIn = userIdTask.execute().get();
//                    userNickName = String.valueOf(jsonIn);
////                    holder.userName.setText(userNickName);
//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//            }
            myChatViewHolder.txtChatMessage.setText(chatMessages.getContent());
//            myChatViewHolder.txtUserAlphabet.setText(userNickName);
        }

        private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
            ChatMessage chatMessages = chatMessage.get(position);
            String senderId = chatMessages.getSender();
            String userNickName = "";

            if (Common.networkConnected(FriendsMessageActivity.this)) {
                String url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findUserNickName");
                jsonObject.addProperty("id", senderId);
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {
                    //顯示使用者暱稱
                    String jsonIn = userIdTask.execute().get();
                    userNickName = String.valueOf(jsonIn);
//                    holder.userName.setText(userNickName);
                    //使用者頭像
                    url = Common.URL + "/PicturesServlet";
                    headImageTask = new HeadImageTask(url, senderId, imageSize,otherChatViewHolder.circleImageView);
                    headImageTask.execute();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
//            String alphabet = userNickName.substring(0, 1);
            otherChatViewHolder.txtChatMessage.setText(chatMessages.getContent());
            otherChatViewHolder.txtUserAlphabet.setText(userNickName);
        }


        @Override
        public int getItemCount() {
            return chatMessage.size();
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage chatMessages = chatMessage.get(position);
            dbSender = chatMessages.getSender();
            if (dbSender.equals(sender)) {
                return VIEW_TYPE_ME;
            } else {
                return VIEW_TYPE_OTHER;
            }
        }

        private class MyChatViewHolder extends RecyclerView.ViewHolder {
            private TextView txtChatMessage, txtUserAlphabet;

            public MyChatViewHolder(View itemView) {
                super(itemView);
                txtChatMessage = itemView.findViewById(R.id.textView);
//                txtUserAlphabet = itemView.findViewById(R.id.textViewUserName);
            }
        }

        private class OtherChatViewHolder extends RecyclerView.ViewHolder {
            private TextView txtChatMessage, txtUserAlphabet;
            private CircleImageView circleImageView;


            public OtherChatViewHolder(View itemView) {
                super(itemView);
                txtChatMessage = itemView.findViewById(R.id.textView);
                txtUserAlphabet = itemView.findViewById(R.id.textViewUserName);
                circleImageView = itemView.findViewById(R.id.chatMessage_picture);
            }
        }
    }




    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("chat");
        ChatReceiver chatReceiver = new ChatReceiver();
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
    }
//    private void showMessage(String sender,String message) {
//        String text = sender + ": ";
//        String text2 = message;
//        View view;
////        // 準備左右2種layout給不同種類發訊者(他人/自己)使用
////        if (left) {
////            view = View.inflate(this, R.layout.chat_message_left, null);
////        } else {
////            view = View.inflate(this, R.layout.chat_message_right, null);
////        }
//
//        TextView textView = view.findViewById(R.id.textViewUserName);
//        TextView textView2 = view.findViewById(R.id.textView);
//
//        textView.setText(text);
//        textView2.setText(text2);
//        recyclerView.addView(view);
//
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(View.FOCUS_DOWN);
//            }
//        });
//    }



//    public void onGetMessagesSuccess(ChatMessage chatMessages) {
//        if (friendsMessageActivityAdapter == null) {
//            friendsMessageActivityAdapter = new FriendsMessageActivityAdapter(new ArrayList<ChatMessage>());
//            recyclerView.setAdapter(friendsMessageActivityAdapter);
//        }
//        friendsMessageActivityAdapter.add(chatMessages);
//        recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount() - 1);
//        friendsMessageActivityAdapter.notifyDataSetChanged();
//    }

    // 接收到聊天訊息會在TextView呈現
    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
            String sender = chatMessage.getSender();
            String messageType = chatMessage.getMessageType();
            // 接收到聊天訊息，若發送者與目前聊天對象相同，就顯示訊息
            if (sender.equals(friend)) {
                switch (messageType) {
                    case "text":
                        if (friendsMessageActivityAdapter == null) {
                            friendsMessageActivityAdapter = new FriendsMessageActivityAdapter(new ArrayList<ChatMessage>());
                            recyclerView.setAdapter(friendsMessageActivityAdapter);
                        }
                            friendsMessageActivityAdapter.add(chatMessage);
                            recyclerView.smoothScrollToPosition(friendsMessageActivityAdapter.getItemCount() - 1);
//                        friendsMessageActivityAdapter.notifyDataSetChanged();
//                        showMessage(sender,chatMessage.getContent(), true);
//                        recyclerView.setAdapter(new FriendsMessageActivityAdapter(FriendsMessageActivity.this, chatMessages));
//                        FriendsMessageActivityAdapter.this.notifyDataSetChanged();

                        break;
//                    case "image":
//                        byte[] image = Base64.decode(chatMessage.getContent(), Base64.DEFAULT);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//                        showImage(sender, bitmap, true);
//                        break;
                    default:
                        break;
                }
            }
            Log.d(TAG, "received message: " + message);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ChatWebSocketClient.friendInChat = null;
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
    }
}

