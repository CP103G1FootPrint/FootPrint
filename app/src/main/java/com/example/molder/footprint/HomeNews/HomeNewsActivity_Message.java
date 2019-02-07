package com.example.molder.footprint.HomeNews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.molder.footprint.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeNewsActivity_Message extends AppCompatActivity {
    private CircleImageView profile＿picture;
    private TextView tv_news_userName;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_news__message);
        profile＿picture = findViewById(R.id.ci_profile＿picture);
        tv_news_userName = findViewById(R.id.tv_news_userName);
        handleViews();
        showResults();
    }

    private void showResults() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            HomeNewsFragment_News homeNewsFragmentNews = (HomeNewsFragment_News) bundle.getSerializable("news");
            if (homeNewsFragmentNews != null) {
//                profile＿picture.setImageResource(homeNewsFragmentNews.getProfilePictureId());
//                tv_news_userName.setText(homeNewsFragmentNews.getUserName());
            }
        }
    }

    private class HomeNews_MessageAdapter extends RecyclerView.Adapter {
        Context context;
        List<HomeNewsActivity_Message_Messages> newsMessage;

        public HomeNews_MessageAdapter(Context context,
                                       List<HomeNewsActivity_Message_Messages> newsMessage) {
            this.context = context;
            this.newsMessage = newsMessage;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.activity_home_news__message_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final HomeNewsActivity_Message_Messages news_Message = newsMessage.get(i);
            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
            myViewHolder.circleImageView.setImageResource(news_Message.getCi_profile＿pictureId());
            myViewHolder.tv_news_userName.setText(news_Message.getTv_news_userName());
            myViewHolder.tv_news_Comment.setText(news_Message.getTv_news_Comment());
        }

        @Override
        public int getItemCount() {
            return newsMessage.size();
        }
    }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView circleImageView;
            TextView tv_news_userName;
            TextView tv_news_Comment;

            public MyViewHolder(View itemView) {
                super(itemView);
                circleImageView = itemView.findViewById(R.id.message_ci_profile＿picture);
                tv_news_userName = itemView.findViewById(R.id.message_tv_news_userName);
                tv_news_Comment = itemView.findViewById(R.id.message_tv_news_Comment);
            }
        }

        private void handleViews(){
            recyclerView = findViewById(R.id.rv_home_news_message);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));//這邊輸入語法可以設定水平或垂直呈現
            List<HomeNewsActivity_Message_Messages> newsMessageList = getNewsMessage();
            recyclerView.setAdapter(new HomeNews_MessageAdapter(this, newsMessageList));
        }

        protected List<HomeNewsActivity_Message_Messages> getNewsMessage() {
            List<HomeNewsActivity_Message_Messages> newsMessage = new ArrayList<>();
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.profile_picture_earth,"Earth","讓我們繼續看下去"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.profile_picture_cockroach,"Cockroach","讓我們繼續看下去+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.profile_picture_pinky,"Pinky","讓我們繼續看下去+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.com_facebook_profile_picture_blank_portrait,"None","讓我們繼續看下去+1+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.ic_facebook_logo,"Facebook","讓我們繼續看下去+1+1+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.ic_footprint_logo,"FootPrint","讓我們繼續看下去+1+1+1+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.default_image,"Hi","讓我們繼續看下去+1+1+1+1+1+1+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.profile_picture_earth,"Earth","讓我們繼續看下去"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.profile_picture_cockroach,"Cockroach","讓我們繼續看下去+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.profile_picture_pinky,"Pinky","讓我們繼續看下去+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.com_facebook_profile_picture_blank_portrait,"None","讓我們繼續看下去+1+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.ic_facebook_logo,"Facebook","讓我們繼續看下去+1+1+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.ic_footprint_logo,"FootPrint","讓我們繼續看下去+1+1+1+1+1"));
            newsMessage.add(new HomeNewsActivity_Message_Messages
                    (R.drawable.default_image,"Hi","讓我們繼續看下去+1+1+1+1+1+1+1+1"));
            return newsMessage;
        }
    }

