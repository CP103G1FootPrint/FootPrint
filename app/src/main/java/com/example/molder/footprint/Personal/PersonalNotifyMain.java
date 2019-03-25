package com.example.molder.footprint.Personal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.molder.footprint.CheckInShare.Picture;
import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Friends.ChatMessage;
import com.example.molder.footprint.Friends.ChatWebSocketClient;
import com.example.molder.footprint.Friends.FriendsMessageActivity;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Personal_Friendship_Friends;
import com.example.molder.footprint.Map.InfoImageTask;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.molder.footprint.HomeNews.FriendshipNotifyServer.connectServer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalNotifyMain extends Fragment {
    private static final String TAG = "PersonalNotifyMain";
    private List<HomeNewsActivity_Personal_Friendship_Friends> friendships;
    public static ChatWebSocketClient chatWebSocketClient;
    private String userId;
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private HeadImageTask headImageTask;
    private View personal_fragment;
    private CommonTask friendsCommonTask,userIdTask;
    private LocalBroadcastManager broadcastManager;
    private FriendShipNotifyAdapter friendShipNotifyAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        SharedPreferences preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
//        userId = preferences.getString("userId", "");
        personal_fragment = inflater.inflate(R.layout.personal_main, container, false);
        recyclerView = personal_fragment.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        activity = getActivity();
        getFriendsFriendFragment_Friend();
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        connectServer(userId, activity);
        registerChatReceiver();
        return personal_fragment;

    }



    private void getFriendsFriendFragment_Friend() {
        //目前登入的使用者
        SharedPreferences preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        userId = preferences.getString("userId", "");

        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/FriendsServlet";
            List<HomeNewsActivity_Personal_Friendship_Friends> friendship_Friends = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllFriendsNotify");
            jsonObject.addProperty("userId", userId);
            String jsonOut = jsonObject.toString();
            friendsCommonTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = friendsCommonTask.execute().get();
                Type listType = new TypeToken<List<HomeNewsActivity_Personal_Friendship_Friends>>() {
                }.getType();
                friendship_Friends = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (friendship_Friends == null || friendship_Friends.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoNewsFound);
            } else {
                friendShipNotifyAdapter = new FriendShipNotifyAdapter(friendship_Friends);
                recyclerView.setAdapter(friendShipNotifyAdapter);
                }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

    public class FriendShipNotifyAdapter extends RecyclerView.Adapter<FriendShipNotifyAdapter.FriendshipViewHolder> {
        private LayoutInflater layoutInflater;
        private int imageSize;

        public FriendShipNotifyAdapter(List<HomeNewsActivity_Personal_Friendship_Friends> friendship) {
//            layoutInflater = LayoutInflater.from(viewGroup.getContext());
            friendships = friendship;
            imageSize = getResources().getDisplayMetrics().widthPixels / 10;
        }
        public void add(HomeNewsActivity_Personal_Friendship_Friends message) {
            friendships.add(0,message);
            notifyItemInserted(0);
        }

        class FriendshipViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_NickName, tv_Notify;
            private CircleImageView ci_ProfilePicture;
            private Button bt_Agree,bt_DisAgree;

            public FriendshipViewHolder(View itemView) {
                super(itemView);
                tv_NickName = itemView.findViewById(R.id.tv_NickName);
                tv_Notify = itemView.findViewById(R.id.tv_Notify);
                ci_ProfilePicture = itemView.findViewById(R.id.ci_ProfilePicture);
                bt_Agree = itemView.findViewById(R.id.bt_Agree);
                bt_DisAgree = itemView.findViewById(R.id.bt_DisAgree);

            }
        }

        @NonNull
        @Override
        public FriendshipViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            FriendshipViewHolder viewHolder = null;
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = layoutInflater.inflate(R.layout.personal_notify_item, viewGroup, false);
            viewHolder = new FriendshipViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull FriendshipViewHolder holder, final int position) {
            final HomeNewsActivity_Personal_Friendship_Friends friendship = friendships.get(position);
//            FriendshipViewHolder friendshipViewHolder;


            holder.tv_Notify.setText(friendship.getMessage());
            if (Common.networkConnected(activity)) {
                String url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findUserNickName");
                jsonObject.addProperty("id", friendship.getInviter());
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {

                    //顯示使用者暱稱
                    String jsonIn = userIdTask.execute().get();
                    String userNickName = String.valueOf(jsonIn);
                    holder.tv_NickName.setText(userNickName + " 想加你好友");

                    //使用者頭像
                    url = Common.URL + "/PicturesServlet";
                    headImageTask = new HeadImageTask(url,friendship.getInviter(),imageSize,holder.ci_ProfilePicture);
                    headImageTask.execute();

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }else{
//                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
            }

            holder.bt_Agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL + "/FriendsServlet";
                        HomeNewsActivity_Personal_Friendship_Friends friends;
                        friends = new HomeNewsActivity_Personal_Friendship_Friends(friendship.getInviter(),friendship.getInvitee());
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "update");
                        jsonObject.addProperty("update", new Gson().toJson(friends));
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
                            Common.showToast(activity, R.string.msg_InsertFail);
                        } else {
//                                Common.showToast(activity, R.string.msg_InsertSuccess);
                        }
                    } else {
                        Common.showToast(activity, R.string.msg_NoNetwork);
                    }

                    removeData(position);
                }
            });

            holder.bt_DisAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL + "/FriendsServlet";
                        HomeNewsActivity_Personal_Friendship_Friends friends;
                        friends = new HomeNewsActivity_Personal_Friendship_Friends(friendship.getInviter(),friendship.getInvitee());
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "delete");
                        jsonObject.addProperty("delete", new Gson().toJson(friends));
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
                            Common.showToast(activity, R.string.msg_InsertFail);
                        } else {
//                                Common.showToast(activity, R.string.msg_InsertSuccess);
                        }
                    } else {
                        Common.showToast(activity, R.string.msg_NoNetwork);
                    }

                    removeData(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendships.size();

        }

        public void removeData(int position) {
            friendships.remove(position);
            //删除动画
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("notify");
        ChatReceiver chatReceiver = new ChatReceiver();
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
    }

    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Common.showToast(activity, R.string.msg_NoNewsFound);

            String message = intent.getStringExtra("message");
            HomeNewsActivity_Personal_Friendship_Friends friendNotify = new Gson().fromJson(message, HomeNewsActivity_Personal_Friendship_Friends.class);
//            String sender = chatMessage.getSender();
            String messageType = friendNotify.getMessageType();
            // 接收到聊天訊息，若發送者與目前聊天對象相同，就顯示訊息

                switch (messageType) {
                    case "text":
                        if (friendShipNotifyAdapter == null) {
                            friendShipNotifyAdapter = new FriendShipNotifyAdapter(new ArrayList<HomeNewsActivity_Personal_Friendship_Friends>());
                            recyclerView.setAdapter(friendShipNotifyAdapter);
                        }

                        friendShipNotifyAdapter.add(friendNotify);
//                        recyclerView.smoothScrollToPosition(friendShipNotifyAdapter.getItemCount() - 1);
                        break;

                    default:
                        break;
                }

            Log.d(TAG, "received message: " + message);
        }
    }
}

