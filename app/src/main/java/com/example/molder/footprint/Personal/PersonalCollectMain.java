package com.example.molder.footprint.Personal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Map.InfoImageTask;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCollectMain extends Fragment {


    public PersonalCollectMain() {
    }


    private RecyclerView recyclerView;
    private View personal_fragment;
    private List<PersonalCollectMember> pictures = null; //存imageID
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

        recyclerView = personal_fragment.findViewById(R.id.recyclerView);
        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String userId = preferences.getString("userId", "");

        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/CollectServlet";  //改
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findImageId");
            jsonObject.addProperty("id", userId); //
            //將內容轉成json字串

            retrieveLocationTask = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveLocationTask.execute().get();
                Type listType = new TypeToken<List<PersonalCollectMember>>() {
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
                recyclerView.setAdapter(new PersonalCollectMain.PersonalCollectAdapter(getActivity(), pictures));
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }


    }


    private class PersonalCollectAdapter extends
            RecyclerView.Adapter<PersonalCollectMain.PersonalCollectAdapter.MyViewHolder> {
        private Context context;
        private List<PersonalCollectMember> picList;

        PersonalCollectAdapter(Context context, List<PersonalCollectMember> picList) {
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
        public PersonalCollectMain.PersonalCollectAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.personal_collect_item, viewGroup, false);
            return new PersonalCollectMain.PersonalCollectAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final PersonalCollectMember picture = picList.get(position);
            String url = Common.URL + "/CollectServlet";
            int id = picture.getImage();
            infoImageTask = new InfoImageTask(url, id, imageSize, holder.imageView);
            infoImageTask.execute();
        }
    }


}
