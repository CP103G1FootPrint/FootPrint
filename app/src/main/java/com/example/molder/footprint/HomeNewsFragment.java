package com.example.molder.footprint;


import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

//import com.example.molder.footprint.main.task.HomeNewsActivity_personal;
//import com.example.molder.footprint.main.task.CommonTask;
//import com.example.molder.footprint.main.task.ImageTask;

import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNewsFragment extends Fragment implements FragmentBackHandler {
//    private static final String TAG = "TAG_HomeNewsFragment";
    private FragmentActivity activity;
    private RecyclerView rvNews;
    private SwipeRefreshLayout swipeRefreshLayout;

//    private CommonTask spotGetAllTask;
//    private CommonTask spotDeleteTask;
//    private ImageTask spotImageTask;


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
        View view = inflater.inflate(R.layout.fragment_home_news, container, false);
        //刷新資料
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //REFERENCE
        rvNews = view.findViewById(R.id.home_news);
        //LAYOUT MANAGER
        rvNews.setLayoutManager(new LinearLayoutManager(activity));
//      rvNews.setLayoutManager(new LinearLayoutManager(getActivity()));
        getHomeNewsFragment_News();
        return view;
    }

    private void getHomeNewsFragment_News() {
        List<HomeNewsFragment_News> homeNewsFragment_news = new ArrayList<>();
        homeNewsFragment_news.add(new HomeNewsFragment_News(R.drawable.profile_picture_cockroach, "cockroach", "Taipei", R.drawable.testpicture1));
        homeNewsFragment_news.add(new HomeNewsFragment_News(R.drawable.profile_picture_earth, "earth", "Taoyuan", R.drawable.testpicture3));
        homeNewsFragment_news.add(new HomeNewsFragment_News(R.drawable.profile_picture_pinky, "pinky", "Taichung", R.drawable.testpicture4));
        rvNews.setAdapter(new HomeNewsFragmentAdapter(activity, homeNewsFragment_news));
    }

    private class HomeNewsFragmentAdapter extends RecyclerView.Adapter<HomeNewsFragmentAdapter.MyViewHolder> {

        private LayoutInflater layoutInflater;
        private List<HomeNewsFragment_News> homeNewsFragment_news;


        HomeNewsFragmentAdapter(Context context, List<HomeNewsFragment_News> HomeNewsFragment_News) {
            layoutInflater = LayoutInflater.from(context);
            this.homeNewsFragment_news = HomeNewsFragment_News;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profile＿picture;
            TextView userName, landmark;
            ImageView news_picture, likes;
            ImageButton message, collection;


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
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final HomeNewsFragment_News homeNewsFragmentnews = homeNewsFragment_news.get(position);
            holder.profile＿picture.setImageResource(homeNewsFragmentnews.getProfilePictureId());
            holder.userName.setText(homeNewsFragmentnews.getUserName());
            holder.landmark.setText(homeNewsFragmentnews.getLandmark());
            holder.news_picture.setImageResource(homeNewsFragmentnews.getNewsPictureId());
            holder.profile＿picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HomeNewsActivity_Personal.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
            holder.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HomeNewsActivity_Personal.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
            holder.landmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new HomeNewsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", homeNewsFragmentnews);
                    fragment.setArguments(bundle);
                    changeFragment(fragment);
                }
            });
            holder.news_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

//            holder.likes.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View arg0, MotionEvent arg1) {
//                likes.setSelected(arg1.getAction()== MotionEvent.ACTION_DOWN);
//                return true;
//                }
//            });

            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),HomeNewsActivity_Message.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", homeNewsFragmentnews);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return homeNewsFragment_news.size();
        }

        private void changeFragment(Fragment fragment) {
            if (getActivity().getSupportFragmentManager() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.content, fragment).addToBackStack(null).commit();
            }
        }
    }
}
