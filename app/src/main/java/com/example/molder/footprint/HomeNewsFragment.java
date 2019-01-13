package com.example.molder.footprint;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNewsFragment extends Fragment {
    private FragmentActivity activity;
    private RecyclerView home_news;

    public HomeNewsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home_news, container, false);
        //REFERENCE
        home_news = view.findViewById(R.id.home_news);
        //LAYOUT MANAGER
        home_news.setLayoutManager(new LinearLayoutManager(activity));
        home_news.setLayoutManager(new LinearLayoutManager(getActivity()));
        getHomeNewsFragment_News();
        return view;
    }

    private void getHomeNewsFragment_News() {
        List<HomeNewsFragment_News> HomeNewsFragment_News = new ArrayList<>();
        HomeNewsFragment_News.add(new HomeNewsFragment_News(R.drawable.profile_picture_cockroach, "cockroach", "Taipei", R.drawable.home_news_picture));
        HomeNewsFragment_News.add(new HomeNewsFragment_News(R.drawable.profile_picture_earth, "earth", "Taipei", R.drawable.home_news_picture));
        HomeNewsFragment_News.add(new HomeNewsFragment_News(R.drawable.profile_picture_pinky, "pinky", "Taipei", R.drawable.home_news_picture));
        home_news.setAdapter(new HomeNewsFragmentAdapter(activity, HomeNewsFragment_News));

    }

    @Override
    public void onStart() {
        super.onStart();
        getHomeNewsFragment_News();
    }

    private class HomeNewsFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<HomeNewsFragment_News> HomeNewsFragment_News;
        private int news_picture_size;

        HomeNewsFragmentAdapter(Context context, List<HomeNewsFragment_News> HomeNewsFragment_News) {
            layoutInflater = LayoutInflater.from(context);
            this.HomeNewsFragment_News = HomeNewsFragment_News;
            news_picture_size = getResources().getDisplayMetrics().widthPixels / 4; //getDisplayMetrics()取得目前螢幕
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profile＿picture;
            TextView userName, landmark;
            ImageView news_picture;
            ImageButton likes, message, collection;

            public MyViewHolder(View itemView) {
                super(itemView);
                profile＿picture = itemView.findViewById(R.id.ci_profile＿picture);
                userName = itemView.findViewById(R.id.tv_news_userName);
                landmark = itemView.findViewById(R.id.tv_news_landmark);
                news_picture = itemView.findViewById(R.id.iv_news_picture);
                likes = itemView.findViewById(R.id.ib_news_like);
                message = itemView.findViewById(R.id.ib_news_message);
                collection = itemView.findViewById(R.id.ib_news_collection);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.fragment_home_news_listitem, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return HomeNewsFragment_News.size();
        }
    }
}
