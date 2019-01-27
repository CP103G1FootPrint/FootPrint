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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeStrokeFragment extends Fragment implements FragmentBackHandler {
    private FragmentActivity activity;
    private RecyclerView rvStroke;
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
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        rvStroke = view.findViewById(R.id.home_news);
        //LAYOUT MANAGER
        rvStroke.setLayoutManager(new LinearLayoutManager(activity));
        getHomeStrokeFragment_stroke();
        return view;
    }

    private void getHomeStrokeFragment_stroke() {
        List<HomeStrokeFragment_stroke> homeStrokeFragment_strokes = new ArrayList<>();
        homeStrokeFragment_strokes.add(new HomeStrokeFragment_stroke
                (R.drawable.testpicture1,R.drawable.profile_picture_cockroach
                        ,"cockroach","東京遊","2018/03/03"));
        homeStrokeFragment_strokes.add(new HomeStrokeFragment_stroke
                (R.drawable.testpicture2,R.drawable.profile_picture_earth
                        ,"earth","大阪遊","2018/03/03"));
        homeStrokeFragment_strokes.add(new HomeStrokeFragment_stroke
                (R.drawable.testpicture3,R.drawable.profile_picture_pinky
                        ,"pinky","北極遊","2018/03/03"));
        homeStrokeFragment_strokes.add(new HomeStrokeFragment_stroke
                (R.drawable.testpicture4,R.drawable.ic_footprint_logo
                        ,"footprint","南極遊","2018/03/03"));
        rvStroke.setAdapter(new HomeStrokeFragmentAdapter(activity, homeStrokeFragment_strokes));

    }

    private class HomeStrokeFragmentAdapter extends RecyclerView.Adapter<HomeStrokeFragmentAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<HomeStrokeFragment_stroke> homeStrokeFragment_strokes;

        HomeStrokeFragmentAdapter(Context context, List<HomeStrokeFragment_stroke> HomeStrokeFragment_stroke) {
            layoutInflater = LayoutInflater.from(context);
            this.homeStrokeFragment_strokes = HomeStrokeFragment_stroke;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profile＿picture;
            TextView userName, title, date;
            ImageView imageView,likes,message,collection;


            public MyViewHolder(View itemView) {
                super(itemView);
                profile＿picture = itemView.findViewById(R.id.stroke_CiUser);
                userName = itemView.findViewById(R.id.stroke_TvUserName);
                title = itemView.findViewById(R.id.stroke_TvTitle);
                date = itemView.findViewById(R.id.stroke_TvDate);
                imageView = itemView.findViewById(R.id.stroke_ImgView);
                likes = itemView.findViewById(R.id.stroke_IvLike);
                message = itemView.findViewById(R.id.stroke_IvMessage);
                collection = itemView.findViewById(R.id.stroke_IvCollection);
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
            final HomeStrokeFragment_stroke homeStrokeFragmentstrokes = homeStrokeFragment_strokes.get(position);
            holder.userName.setText(homeStrokeFragmentstrokes.getStroke_TvUserName());
            holder.date.setText(homeStrokeFragmentstrokes.getStroke_TvDate());
            holder.title.setText(homeStrokeFragmentstrokes.getStroke_TvTitle());
            holder.imageView.setImageResource(homeStrokeFragmentstrokes.getStroke_ImgViewId());
            holder.profile＿picture.setImageResource(homeStrokeFragmentstrokes.getStroke_CiUserId());
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
//            holder.likes.setImageResource(homeStrokeFragmentstrokes.);
//            holder.message.setImageResource(homeStrokeFragmentstrokes.getStroke_CiUserId());
//            holder.collection.setImageResource(homeStrokeFragmentstrokes.getStroke_CiUserId());
        }

        @Override
        public int getItemCount() {
            return homeStrokeFragment_strokes.size();
        }


    }
}



