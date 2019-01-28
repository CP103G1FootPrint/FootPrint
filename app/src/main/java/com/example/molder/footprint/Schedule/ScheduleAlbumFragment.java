package com.example.molder.footprint.Schedule;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleAlbumFragment extends Fragment {
    private static final String TAG = "ScheduleAlbumFragment";
    private RecyclerView rvAlbum ;
    private FragmentActivity activity;
    private CommonTask albumGetAllTask;

    private ImageTask albumImageTask;
    private static final int REQ_PICK_IMAGE = 0 ;
    private SwipeRefreshLayout shAlbumSwipeRefreshLayout ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_album,container,false);
        shAlbumSwipeRefreshLayout = view.findViewById(R.id.shAlbumSwipeRefreshLayout);
        shAlbumSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shAlbumSwipeRefreshLayout.setRefreshing(true);
                showAllAlbum();
                shAlbumSwipeRefreshLayout.setRefreshing(false);
            }
        });


        handleViews(view);
        return view;
    }

    private void showAllAlbum(){
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/GroupAlbumServlet";
            List<GroupAlbum> groupAlbums = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getImage");
            String jsonOut = jsonObject.toString();
            albumGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = albumGetAllTask.execute().get();
                Type listType = new TypeToken<List<GroupAlbum>>() {
                }.getType();
                groupAlbums = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (groupAlbums == null || groupAlbums.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoImage);
            } else {
                rvAlbum.setAdapter(new GroupAdapter(activity, groupAlbums));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllAlbum(); //重刷抓資料
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<GroupAlbum> groupAlbums;
        private int imageSize;

        public GroupAdapter(Context context, List<GroupAlbum> groupAlbums) {
            layoutInflater = LayoutInflater.from(context);
            this.groupAlbums = groupAlbums;

            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.shAlbumImg);

            }
        }
        @Override
        public int getItemCount() {
            return groupAlbums.size();
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.schedule_album_item, parent, false);
            return new ScheduleAlbumFragment.GroupAdapter.MyViewHolder(itemView);

        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            final GroupAlbum groupAlbum = groupAlbums.get(i);

            String url = Common.URL + "/GroupAlbumServlet"; //圖還未載入
            int id = groupAlbum.getId();
            albumImageTask = new ImageTask(url, id, imageSize, myViewHolder.imageView);
            albumImageTask.execute();
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
    private void handleViews(View view){
        rvAlbum = view.findViewById(R.id.shRecyclerViewAlbum);
        rvAlbum.setLayoutManager(new LinearLayoutManager(activity));
//        rvAlbum.setAdapter(new GroupAlbumAdapter(activity,getAlbums()));
        FloatingActionButton shBtGroupAlbumAdd = view.findViewById(R.id.shBtGroupAlbumAdd);
        shBtGroupAlbumAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_IMAGE);

            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        if (albumImageTask != null) {
            albumImageTask.cancel(true);
            albumImageTask = null;
        }

        if (albumImageTask != null) {
            albumImageTask.cancel(true);
            albumImageTask = null;
        }

        if (albumImageTask != null) {
            albumImageTask.cancel(true);
            albumImageTask = null;
        }
    }

}
