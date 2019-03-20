package com.example.molder.footprint.HomeStroke;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Map.LandMark;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeStrokePlanDayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TAG = "HomeStrokePlanDayFragment";

    // TODO: Rename and change types of parameters
    private Context mContext;
    private RecyclerView mRecyclerView;
    private HomeStrokePlanDayFragment.MainAdapter mAdapter;
    private List<LandMark> mDatas = new ArrayList<>();
    private CommonTask retrieveLocationTask2;
    private int day, tripId;
    private View view;

    public HomeStrokePlanDayFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeStrokePlanDayFragment newInstance(int position, int tripID) {
        HomeStrokePlanDayFragment fragment = new HomeStrokePlanDayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        args.putInt(ARG_PARAM2, tripID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getInt(ARG_PARAM1);
            tripId = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_stroke_plan_day, container, false);
        mContext = getActivity();
        initData();
        mRecyclerView = view.findViewById(R.id.homeStrokePlanRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mAdapter = new HomeStrokePlanDayFragment.MainAdapter(mContext, mDatas);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void initData() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findLandMarkInSchedulePlanDay");
            jsonObject.addProperty("SchedulePlanDayTripId", tripId);
            jsonObject.addProperty("SchedulePlanDay", day);
            //將內容轉成json字串
            retrieveLocationTask2 = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveLocationTask2.execute().get();
                Type listType = new TypeToken<List<LandMark>>() {
                }.getType();
                //解析 json to gson
                mDatas = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
        }
    }

    public class MainAdapter extends RecyclerView.Adapter<HomeStrokePlanDayFragment.MainAdapter.MyViewHolder> {

        private Context mContext;
        private List<LandMark> mDatas;

        public MainAdapter(Context context, List<LandMark> mDatas) {
            this.mContext = context;
            this.mDatas = mDatas;
        }

        @Override
        public HomeStrokePlanDayFragment.MainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.swiplayout, null);
            return new HomeStrokePlanDayFragment.MainAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HomeStrokePlanDayFragment.MainAdapter.MyViewHolder holder, final int position) {
            LandMark landMark = mDatas.get(position);
            holder.landMarkName.setText(landMark.getName());
            holder.landMarkAddress.setText(landMark.getAddress());
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView landMarkName, landMarkAddress;

            public MyViewHolder(View view) {
                super(view);
                landMarkName = itemView.findViewById(R.id.schedulePlanDayLandMarkName);
                landMarkAddress = itemView.findViewById(R.id.schedulePlanDayLandMarkAddress);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (retrieveLocationTask2 != null) {
            retrieveLocationTask2.cancel(true);
            retrieveLocationTask2 = null;
        }
    }

}
