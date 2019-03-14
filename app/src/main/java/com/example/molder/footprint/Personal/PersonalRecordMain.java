package com.example.molder.footprint.Personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.molder.footprint.CheckInShare.Picture;
import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.Map.InfoImageTask;
import com.example.molder.footprint.Map.LandMarkInfo;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalRecordMain extends Fragment {


    public PersonalRecordMain() {
    }


    private RecyclerView recyclerView;
    private View personal_fragment;
    private List<Picture> pictures = null; //存imageID
    private CommonTask retrieveLocationTask;
    private InfoImageTask infoImageTask;
    private int imageSize;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        personal_fragment = inflater.inflate(R.layout.personal_main, container, false);
        findViews();
        return personal_fragment;

    }


    private void findViews() {

        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String userID = preferences.getString("userId", "");
        recyclerView = personal_fragment.findViewById(R.id.recyclerView);
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/RecordServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findImageId");
            jsonObject.addProperty("id", userID);
            //將內容轉成json字串

            retrieveLocationTask = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveLocationTask.execute().get();
                Type listType = new TypeToken<List<Picture>>() {
                }.getType();
                //解析 json to gson
                pictures = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (pictures == null || pictures.isEmpty()) {
//                Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
            } else {
                //recycleView
                recyclerView.setLayoutManager(
                        new StaggeredGridLayoutManager(3,
                                StaggeredGridLayoutManager.VERTICAL));
                recyclerView.setAdapter(new PersonalRecorAdapter(getActivity(), pictures));
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }


    }


    private class PersonalRecorAdapter extends
            RecyclerView.Adapter<PersonalRecorAdapter.MyViewHolder> {
        private Context context;
        private List<Picture> picList;

        PersonalRecorAdapter(Context context, List<Picture> picList) {
            this.context = context;
            this.picList = picList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;


            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);

            }
        }

        @Override
        public int getItemCount() {
            return picList.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.personal_record_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final Picture picture = picList.get(position);
            String url = Common.URL + "/RecordServlet";
            int id = picture.getImageID();
            infoImageTask = new InfoImageTask(url, id, imageSize, holder.imageView);
            infoImageTask.execute();
        }
    }


}
