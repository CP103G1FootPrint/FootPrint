package com.example.molder.footprint.Friends;


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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Personal_Friendship_Friends;
import com.example.molder.footprint.R;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsMessageFragment extends Fragment implements FragmentBackHandler {
    private static final String TAG = "FriendsMessageFragment";
    private FragmentActivity activity;
    private RecyclerView rvMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId,friendsId;
    private HeadImageTask headImageTask;
    private CommonTask friendsCommonTask,userIdTask;
//    private List<String>friendList = new ArrayList<>();
    private int imageSize;

//    private List friendList = new ArrayList<>();
//    private List<FriendList> friendList = new ArrayList<>();


    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getFriendsMessageFragment_Message();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_message, container, false);
        rvMessage = view.findViewById(R.id.friends_RvMessage);
        //LAYOUT MANAGER
        rvMessage.setLayoutManager(new LinearLayoutManager(activity));
        return view;
    }

    private void getFriendsMessageFragment_Message() {
        List<String>friendList = new ArrayList<>();
        SharedPreferences preferences = activity.getSharedPreferences(com.example.molder.footprint.Common.Common.PREF_FILE, MODE_PRIVATE);
        userId = preferences.getString("userId", "");

        //抓取所有與使用者關係為好友的資料
        if (com.example.molder.footprint.Common.Common.networkConnected(activity)) {
            String url = com.example.molder.footprint.Common.Common.URL + "/FriendsServlet";
            List<HomeNewsActivity_Personal_Friendship_Friends> friendship_Friends = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllFriends");
            jsonObject.addProperty("userId", userId);
            String jsonOut = jsonObject.toString();
            friendsCommonTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = friendsCommonTask.execute().get();
                Type listType = new TypeToken<List<HomeNewsActivity_Personal_Friendship_Friends>>() {
                }.getType();
                friendship_Friends = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (friendship_Friends == null || friendship_Friends.isEmpty()) {
                com.example.molder.footprint.Common.Common.showToast(activity, R.string.msg_NoNewsFound);
            } else {
//                final HomeNewsActivity_Personal_Friendship_Friends friendship = friendship_Friends.get();
                for(int i = 0;i<friendship_Friends.size();i++) {
                    friendship_Friends.get(i);
                    friendsId = friendship_Friends.get(i).getInvitee();
                    if(friendsId.equals(userId)){
                        friendsId = friendship_Friends.get(i).getInviter();
                    }
//                    FriendList friends = new FriendList(userId,friendsId);

//                    String friendListJson = new Gson().toJson(friendList);
                    friendList.add(friendsId);
                }
                //抓取所有為好友的聊天清單
                url = Common.URL + "/FriendsMessageServlet";
                List<ChatMessage> chatMessages = null;
                String friendListJson = new Gson().toJson(friendList);
                jsonObject.addProperty("action", "findMessageList");
//              jsonObject.addProperty("sender", userId);
                jsonObject.addProperty("friendList", friendListJson);
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {
                    String jsonIn = userIdTask.execute().get();
                    Type listType = new TypeToken<List<ChatMessage>>() {
                    }.getType();
                    chatMessages = new Gson().fromJson(jsonIn, listType);
                }catch (Exception e) {
//                    Log.e(TAG, e.toString());
                }if (chatMessages == null || chatMessages.isEmpty()) {
                    com.example.molder.footprint.Common.Common.showToast(activity, R.string.msg_NoNewsFound);
                }else{
                    rvMessage.setAdapter(new FriendsMessageFragmentAdapter(activity,chatMessages));
                }
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

    private class FriendsMessageFragmentAdapter extends RecyclerView.Adapter<FriendsMessageFragmentAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<ChatMessage> chatMessages;
//        private List<FriendsMessageFragment_Message> friendsMessageFragment_message;

        FriendsMessageFragmentAdapter(Context context, List<ChatMessage> chatMessage) {
            layoutInflater = LayoutInflater.from(context);
            this.chatMessages = chatMessage;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView friends_CvProfilePic;
            TextView friends_TvFriendsName, friends_TvMessage;

            public MyViewHolder(View itemView) {
                super(itemView);
                friends_CvProfilePic = itemView.findViewById(R.id.friends_CvProfilePic);
                friends_TvFriendsName = itemView.findViewById(R.id.friends_TvFriendsName);
                friends_TvMessage = itemView.findViewById(R.id.friends_TvMessage);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.fragment_friends_message_item, parent, false);
            return new FriendsMessageFragmentAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final ChatMessage chatMessage = chatMessages.get(position);
            String friendsID = chatMessage.getReceiver();
            if(friendsID.equals(userId)){
                friendsID = chatMessage.getSender();
            }

            if (Common.networkConnected(activity)) {
                String url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findUserNickName");
                jsonObject.addProperty("id", friendsID);
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {
                    //顯示使用者暱稱
                    String jsonIn = userIdTask.execute().get();
                    String userNickName = String.valueOf(jsonIn);
                    holder.friends_TvFriendsName.setText(userNickName);

                    //使用者頭像
                    url = Common.URL + "/PicturesServlet";
                    headImageTask = new HeadImageTask(url, friendsID, imageSize, holder.friends_CvProfilePic);
                    headImageTask.execute();

                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
                }
            }else{
//                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
            }
            holder.friends_TvMessage.setText(chatMessage.getContent());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FriendsMessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("chatMessage",chatMessage);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }
    }
}
