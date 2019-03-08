package com.example.molder.footprint;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;


import com.example.molder.footprint.Common.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalExchangeMain extends Fragment {


    public PersonalExchangeMain() {
        // Required empty public constructor
    }

    private View personal_fragment;
    private Context mContext;
    private ImageView imageView;
    private Button btTakePictureLarge, btPickPicture;
    private File file;
    private RecyclerView recyclerView;
    private ImageView piggy;
    private TextView id;
    private TextView point;
    private CommonTask retrieveExchangeTask;
    private ImageTask imageTask;
    private List<PersonalExchangeMemberr> exchange;
    private int imageSize;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        personal_fragment = inflater.inflate(R.layout.personal_main, container, false);
        mContext = getActivity();
        findViews();

        return personal_fragment;

    }

    private void findViews() {
//        b = personal_fragment.findViewById(R.id.persona_pic);
        recyclerView = personal_fragment.findViewById(R.id.recyclerView);


        imageView = personal_fragment.findViewById(R.id.imageView);
        btTakePictureLarge = personal_fragment.findViewById(R.id.btTakePictureLarge);
        btPickPicture = personal_fragment.findViewById(R.id.btPickPicture);
        piggy = personal_fragment.findViewById(R.id.piggy);
        id = personal_fragment.findViewById(R.id.id);
        point = personal_fragment.findViewById(R.id.point);


        if (Common.networkConnected((Activity) mContext)) {
            String url = Common.URL + "/ExchangeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
//            jsonObject.addProperty("name", landMarkName); 指定條件搜尋ＴＡＢＬＥ內的欄位
            //將內容轉成json字串
            retrieveExchangeTask = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveExchangeTask.execute().get();
                Type listType = new TypeToken<List<PersonalExchangeMemberr>>() {
                }.getType();
                //解析 json to gson
                exchange = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (exchange == null || exchange.isEmpty()) {
//                Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();

            }else {
                recyclerView.setLayoutManager(
                        new StaggeredGridLayoutManager(1,
                                StaggeredGridLayoutManager.VERTICAL));
                recyclerView.setAdapter(new PersonalExchangeAdapter(getActivity(), exchange));

            }
        }



    }

    /* RecyclerView要透過RecyclerView.Adapter來處理欲顯示的清單內容，
    必須建立RecyclerView.Adapter子類別並覆寫對應的方法：
    getItemCount()、onCreateViewHolder()、onBindViewHolder */
    private class PersonalExchangeAdapter extends
            RecyclerView.Adapter<PersonalExchangeAdapter.MyViewHolder> {
        private Context context;
        private List<PersonalExchangeMemberr> memberList;

        PersonalExchangeAdapter(Context context, List<PersonalExchangeMemberr> memberList) {
            this.context = context;
            this.memberList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageButton personal_pic;
            TextView id;
            TextView point;
            ImageView piggy;



            MyViewHolder(View itemView) {
                super(itemView);
                personal_pic = itemView.findViewById(R.id.personal_pic);
                id = itemView.findViewById(R.id.id);
                point = itemView.findViewById(R.id.point);
                piggy = itemView.findViewById(R.id.piggy);
                imageSize = getResources().getDisplayMetrics().widthPixels / 3;

            }
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }


        @NonNull
        @Override
        public PersonalExchangeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.personal_exchange_item, viewGroup, false);
            return new PersonalExchangeAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final PersonalExchangeAdapter.MyViewHolder holder, int position) {
            final PersonalExchangeMemberr member = memberList.get(position);
            String url = Common.URL + "/LocationServlet";
            int id = member.getId();
            imageTask = new ImageTask(url, id, imageSize, holder.personal_pic);
            imageTask.execute();
            holder.id.setText(String.valueOf(member.getProductName()));
            holder.point.setText(String.valueOf(member.getProductPoint()));
            holder.piggy.setImageResource(R.drawable.ic_piggy_bank_with_dollar_coins);



        }
    }


}
