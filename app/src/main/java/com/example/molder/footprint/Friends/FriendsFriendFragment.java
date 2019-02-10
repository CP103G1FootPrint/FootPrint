package com.example.molder.footprint.Friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.molder.footprint.R;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFriendFragment extends Fragment implements FragmentBackHandler {
    private FragmentActivity activity;
    private RecyclerView rvFriends;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
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
        //LAYOUT MANAGER
        rvFriends.setLayoutManager(new LinearLayoutManager(activity));
        getFriendsFriendFragment_Friend();
        return view;
    }

    private void getFriendsFriendFragment_Friend() {
        List<FriendsFriendFragment_Friend> friendsFriendFragment_friends = new ArrayList<>();
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.com_facebook_profile_picture_blank_portrait,"None"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.profile_picture_cockroach,"cockroach"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.ic_footprint_logo,"footprint"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.com_facebook_tooltip_blue_bottomnub,"facebook"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.profile_picture_earth,"earth"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.profile_picture_pinky,"pinky"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.com_facebook_close,"close"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.com_facebook_favicon_blue,"blueFacebook"));
        friendsFriendFragment_friends.add(new FriendsFriendFragment_Friend
                (R.drawable.default_image,"image"));

        rvFriends.setAdapter(new FriendsFriendFragmentAdapter(activity, friendsFriendFragment_friends));
    }

    private class FriendsFriendFragmentAdapter extends RecyclerView.Adapter<FriendsFriendFragmentAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<FriendsFriendFragment_Friend> friendsFriendFragment_friends;

        FriendsFriendFragmentAdapter(Context context, List<FriendsFriendFragment_Friend> FriendsFriendFragment_Friend) {
            layoutInflater = LayoutInflater.from(context);
            this.friendsFriendFragment_friends = FriendsFriendFragment_Friend;
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
            final FriendsFriendFragment_Friend friendsFriendFragmentfriends = friendsFriendFragment_friends.get(position);
            holder.friends_CvProfilePic.setImageResource(friendsFriendFragmentfriends.getFriends_CvProfilePicId());
            holder.friends_TvFriendsName.setText(friendsFriendFragmentfriends.getFriends_TvFriendsName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FriendsMessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news",friendsFriendFragmentfriends);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendsFriendFragment_friends.size();
        }



    }
}