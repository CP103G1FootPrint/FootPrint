package com.example.molder.footprint.HomeStroke;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.R;
import com.example.molder.footprint.Schedule.ScheduleMainFragment;
import com.example.molder.footprint.Schedule.Trip;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeStrokeFragment extends Fragment implements FragmentBackHandler {
    private static final String TAG = "HomeStrokeFragment";
    private FragmentActivity activity;
    private RecyclerView rvStroke;
    private CommonTask tripGetAllTask,userIdTask;
    private ImageTask tripImageTask;
    private HeadImageTask headImageTask;
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
        View view = inflater.inflate(R.layout.fragment_home_news, container, false);
        //刷新資料
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getHomeStrokeFragment_stroke();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        rvStroke = view.findViewById(R.id.home_news);
//        getHomeStrokeFragment_stroke();
//        LAYOUT MANAGER
        rvStroke.setLayoutManager(new LinearLayoutManager(activity));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getHomeStrokeFragment_stroke();
    }

    private void getHomeStrokeFragment_stroke() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/TripServlet";
            List<HomeStrokeFragment_stroke> trips = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "stroke");
            jsonObject.addProperty("type", "open");
            String jsonOut = jsonObject.toString();
            tripGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = tripGetAllTask.execute().get();
                Type listType = new TypeToken<List<HomeStrokeFragment_stroke>>() {
                }.getType();
                trips = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (trips == null || trips.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoTripsFound);
            } else {
                rvStroke.setAdapter(new HomeStrokeFragmentAdapter(activity, trips));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

    private class HomeStrokeFragmentAdapter extends RecyclerView.Adapter<HomeStrokeFragmentAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<HomeStrokeFragment_stroke> trips;
        private int imageSize;

        HomeStrokeFragmentAdapter(Context context, List<HomeStrokeFragment_stroke> HomeStrokeFragment_stroke) {
            layoutInflater = LayoutInflater.from(context);
            this.trips = HomeStrokeFragment_stroke;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profile＿picture;
            TextView userName, title, date;
            ImageView imageView;


            public MyViewHolder(View itemView) {
                super(itemView);
                profile＿picture = itemView.findViewById(R.id.stroke_CiUser);
                userName = itemView.findViewById(R.id.stroke_TvUserName);
                title = itemView.findViewById(R.id.stroke_TvTitle);
                date = itemView.findViewById(R.id.stroke_TvDate);
                imageView = itemView.findViewById(R.id.stroke_ImgView);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.fragment_home_stroke_item, parent, false);
            return new HomeStrokeFragmentAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final HomeStrokeFragment_stroke homeStrokeFragmentstrokes = trips.get(position);
            String url = Common.URL + "/TripServlet"; //圖還未載入
            int id = homeStrokeFragmentstrokes.getTripID();
            tripImageTask = new ImageTask(url, id, imageSize, holder.imageView);
            //主執行緒繼續執行 新開的執行緒去抓圖，抓圖需要網址、id ，抓到圖後show在imageView上
            tripImageTask.execute(); //ImageTask類似MyTask 去server端抓圖
            String userId = homeStrokeFragmentstrokes.getCreateID();
            if (Common.networkConnected(activity)) {
                url = Common.URL + "/PicturesServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findUserNickName");
                jsonObject.addProperty("id", userId);
                userIdTask = new CommonTask(url, jsonObject.toString());
                try {

                    //顯示使用者暱稱
                    String jsonIn = userIdTask.execute().get();
                    String userNickName = String.valueOf(jsonIn);
                    holder.userName.setText(userNickName);

                    //使用者頭像
                    url = Common.URL + "/PicturesServlet";
                    headImageTask = new HeadImageTask(url, userId, imageSize, holder.profile＿picture);
                    headImageTask.execute();

                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
                }
            }else{
//                Toast.makeText(HomeNewsFragment.this, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }

            holder.date.setText(homeStrokeFragmentstrokes.getDate());
            holder.title.setText(homeStrokeFragmentstrokes.getTitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),HomeStrokeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news",homeStrokeFragmentstrokes);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    /* 呼叫startActivity()開啟新的頁面 */
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }


    }
}



