package com.example.molder.footprint;


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

import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsMessageFragment extends Fragment implements FragmentBackHandler {
    private FragmentActivity activity;
    private RecyclerView rvMessage;
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
        View view = inflater.inflate(R.layout.fragment_friends_message, container, false);
        rvMessage = view.findViewById(R.id.friends_RvMessage);
        //LAYOUT MANAGER
        rvMessage.setLayoutManager(new LinearLayoutManager(activity));
        getFriendsMessageFragment_Message();
        return view;
    }

    private void getFriendsMessageFragment_Message() {
        List<FriendsMessageFragment_Message> friendsMessageFragment_message = new ArrayList<>();
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.com_facebook_profile_picture_blank_portrait,"None","hihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.profile_picture_cockroach,"cockroach","hi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.ic_footprint_logo,"footprint","hihihihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.com_facebook_tooltip_blue_bottomnub,"facebook","hihihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.profile_picture_earth,"earth","hihihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.profile_picture_pinky,"pinky","hihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.com_facebook_close,"close","hihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.com_facebook_favicon_blue,"blueFacebook","hihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.default_image,"image","hihihihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.com_facebook_profile_picture_blank_portrait,"None","hihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.profile_picture_cockroach,"cockroach","hi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.ic_footprint_logo,"footprint","hihihihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.com_facebook_tooltip_blue_bottomnub,"facebook","hihihihi"));
        friendsMessageFragment_message.add(new FriendsMessageFragment_Message
                (R.drawable.profile_picture_earth,"earth","hihihihi"));

        rvMessage.setAdapter(new FriendsMessageFragmentAdapter(activity, friendsMessageFragment_message));
    }

    private class FriendsMessageFragmentAdapter extends RecyclerView.Adapter<FriendsMessageFragmentAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<FriendsMessageFragment_Message> friendsMessageFragment_message;

        FriendsMessageFragmentAdapter(Context context, List<FriendsMessageFragment_Message> FriendsMessageFragment_Message) {
            layoutInflater = LayoutInflater.from(context);
            this.friendsMessageFragment_message = FriendsMessageFragment_Message;
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
            final FriendsMessageFragment_Message friendsMessageFragmentMessage = friendsMessageFragment_message.get(position);
            holder.friends_CvProfilePic.setImageResource(friendsMessageFragmentMessage.getFriends_CvProfilePicId());
            holder.friends_TvFriendsName.setText(friendsMessageFragmentMessage.getFriends_TvFriendsName());
            holder.friends_TvMessage.setText(friendsMessageFragmentMessage.getFriends_TvMessage());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FriendsMessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news",friendsMessageFragmentMessage);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendsMessageFragment_message.size();
        }
    }
}
