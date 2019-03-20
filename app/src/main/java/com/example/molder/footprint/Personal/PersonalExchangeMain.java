package com.example.molder.footprint.Personal;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalExchangeMain extends Fragment {


    public PersonalExchangeMain() {
        // Required empty public constructor
    }

    private ListView listView;
    private View personal_fragment;
    private List<PersonalExchangeMemberr> exchangeList = null;
    //與PersonalExchangeMemberr連結宣告實體變數
    private CommonTask retrieveLocationTask;
    private InfoImageTask infoImageTask;
    private int imageSize;
    private PersonalExchangeMemberr member;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        personal_fragment = inflater.inflate(R.layout.personal_list_main, container, false);
        findViews();
        return personal_fragment;

    }


    private void findViews() {

        listView = personal_fragment.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                member= exchangeList.get(position);
                String productId = member.getProductId();
                if(productId != null || productId != ""){
                    Intent intent = new Intent(getActivity(), Qrcode.class); //從PersonalExchangeMain到Qrcode.class
                    intent.putExtra("key",productId); //類似bundle
                    startActivity(intent);
                }else{

                }

            }
        });

//        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
//        String userId = preferences.getString("userId", "");

        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/ExchangeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            //將內容轉成json字串

            retrieveLocationTask = new CommonTask(url, jsonObject.toString());
            try {   //接收資料
                String jsonIn = retrieveLocationTask.execute().get();
                Type listType = new TypeToken<List<PersonalExchangeMemberr>>() {
                }.getType();
                //解析 json to gson
                exchangeList = new Gson().fromJson(jsonIn, listType); //暫存資料
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }

            if (exchangeList == null || exchangeList.isEmpty()) {
//                Toast.makeText(this, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
            } //顯示資料
else {
//設定recycleView 指定以recycleView的樣式呈現。ＸＸＸManager為取得權限。
//                recyclerView.setLayoutManager(
//                        new StaggeredGridLayoutManager(3,
//                                StaggeredGridLayoutManager.VERTICAL));
//   listView.setAdapter(new PersonalExchangeMain.PersonalExchangeAdapter(getActivity(), exchangeList));
               showResult( exchangeList); //執行114行


            }
        }
        else {
            Toast.makeText(getActivity(), R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }


    }


    //listView
    public void showResult(List<PersonalExchangeMemberr> exchangeList) {
        listView.setAdapter(new ExchangeAdapter(getActivity(), exchangeList));

    }

    private class ExchangeAdapter extends BaseAdapter {
        Context context;
        List<PersonalExchangeMemberr> exchangeList;

        ExchangeAdapter(Context context, List<PersonalExchangeMemberr> exchangeList) {
            this.context = context;
            this.exchangeList = exchangeList;
        }

        @Override
        public int getCount() {
            return exchangeList.size();
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.personal_exchange_item, parent, false);
            }

            member= exchangeList.get(position);
            ImageView productId = itemView.findViewById(R.id.productId);
            TextView productName = itemView.findViewById(R.id.productName);
            TextView description = itemView.findViewById(R.id.description);
            TextView point = itemView.findViewById(R.id.point);


            String url = Common.URL + "/ExchangeServlet"; //抓圖片
            int id_exchange = member.getId(); //根據id_exchange抓圖
            infoImageTask = new InfoImageTask(url, id_exchange, imageSize, productId);
            infoImageTask.execute();

            productName.setText(member.getProductId());
            description.setText(member.getDescription());
            point.setText(member.getPoint());

            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return exchangeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return exchangeList.get(position).getId();
        }
    }


}
