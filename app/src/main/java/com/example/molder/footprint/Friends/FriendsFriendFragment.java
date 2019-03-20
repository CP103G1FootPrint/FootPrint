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
import com.example.molder.footprint.HomeNews.HomeNewsFragment;
import com.example.molder.footprint.HomeNews.HomeNewsFragment_News;
import com.example.molder.footprint.R;
import com.example.molder.footprint.Schedule.SchedulePlanDayFragment;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFriendFragment extends Fragment implements FragmentBackHandler {
    private static final String TAG = "FriendsFriendFragment";
    private static final int REQUEST_CODE = 1;
    private FragmentActivity activity;
    private RecyclerView rvFriends;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonTask friendsCommonTask,userIdTask;
    private HeadImageTask headImageTask;
    private Context context;
    private String userId,textUserId;
    private int count;
    private TextView textViewCounter;
    private List<FriendList> friendLists = new ArrayList<>();

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getFriendsFriendFragment_Friend();
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
        View view = inflater.inflate(R.layout.fragment_friends_friend, container, false);
        //刷新資料
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
        rvFriends = view.findViewById(R.id.friends_RvFriends);
        textViewCounter = view.findViewById(R.id.friends_TvCounter);
        //LAYOUT MANAGER
        rvFriends.setLayoutManager(new LinearLayoutManager(activity));
//        getFriendsFriendFragment_Friend();
        return view;
    }

    private void getFriendsFriendFragment_Friend() {
        //目前登入的使用者
        SharedPreferences preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        userId = preferences.getString("userId", "");

        //抓取所有與使用者關係為好友的資料
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/FriendsServlet";
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
                count = friendship_Friends.size();
                String texts = String.valueOf(count);
                Toast.makeText(getActivity(), texts, Toast.LENGTH_SHORT).show();
                textViewCounter.setText(texts);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (friendship_Friends == null || friendship_Friends.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoNewsFound);
            } else {
                rvFriends.setAdapter(new FriendsFriendFragmentAdapter(activity, friendship_Friends));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

    private class FriendsFriendFragmentAdapter extends RecyclerView.Adapter<FriendsFriendFragmentAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<HomeNewsActivity_Personal_Friendship_Friends> friendship_Friends;
        private int imageSize;


        FriendsFriendFragmentAdapter(Context context, List<HomeNewsActivity_Personal_Friendship_Friends> friendship_Friends_) {
            layoutInflater = LayoutInflater.from(context);
            this.friendship_Friends = friendship_Friends_;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView friends_CvProfilePic;
            TextView friends_TvFriendsName;

            public MyViewHolder(View itemView) {
                super(itemView);
                friends_CvProfilePic = itemView.findViewById(R.id.friends_CvProfilePic);
                friends_TvFriendsName = itemView.findViewById(R.id.friends_TvFriendsName);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.fragment_friends_friend_item, parent, false);
            return new FriendsFriendFragmentAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final HomeNewsActivity_Personal_Friendship_Friends friendship = friendship_Friends.get(position);
            String friendsId = friendship.getInvitee();
            if(friendsId.equals(userId)){
                friendsId = friendship.getInviter();
            }

//            List<FriendList> friendLists = new ArrayList<>();
//            friendLists.add(new FriendList(friendsId));
//            friendLists.size();


//            FriendList name;
//            for(FriendList n:friendLists) {
//                 name = n;
//            }


            if (Common.networkConnected(activity)) {
                String url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findUserNickName");
                jsonObject.addProperty("id", friendsId);
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {

                    //顯示使用者暱稱
                    String jsonIn = userIdTask.execute().get();
                    String userNickName = String.valueOf(jsonIn);
                    holder.friends_TvFriendsName.setText(userNickName);

                    //使用者頭像
                    url = Common.URL + "/PicturesServlet";
                    headImageTask = new HeadImageTask(url, friendsId, imageSize, holder.friends_CvProfilePic);
                    headImageTask.execute();

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }else{
//                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
            }
//
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FriendsMessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("friend",friendship);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendship_Friends.size();
        }
    }
}
