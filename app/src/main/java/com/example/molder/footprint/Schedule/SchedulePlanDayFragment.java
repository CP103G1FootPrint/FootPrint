package com.example.molder.footprint.Schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import java.util.Collections;
import java.util.List;


public class SchedulePlanDayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TAG = "SchedulePlanDayFragment";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private int day, tripId;
    //    private TextView textView;
    private View view;

    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton landMarkAdd;

    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;
    private List<LandMark> mDatas = new ArrayList<>();
    private CommonTask retrieveLocationTask;
    private int iNumber = 0;
    private View v;
    private ListView listView;
    private LandMark chooseMember = null;

    public SchedulePlanDayFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
//    public static SchedulePlanDayFragment newInstance(String param1, String param2) {
//        SchedulePlanDayFragment fragment = new SchedulePlanDayFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static SchedulePlanDayFragment newInstances(int position, int tripID) {
        SchedulePlanDayFragment fragment = new SchedulePlanDayFragment();
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
        view = inflater.inflate(R.layout.fragment_schedule_plan_day, container, false);

        mContext = getActivity();
//        mDatas = new ArrayList<>();
//        mDatas2 = new ArrayList<>();

        //刷新
//        swipeRefreshLayout =
//                view.findViewById(R.id.planSwipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
//                initData();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        //加地標
        landMarkAdd = view.findViewById(R.id.landMarkAdd);
        landMarkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清除重複choose Land Mark AlertDialog
                if (v.getParent() != null) {
                    ((ViewGroup) v.getParent()).removeView(v); // <- fix
                }
                //新建choose Land Mark AlertDialog
                new AlertDialog.Builder(mContext)
                        .setView(v)
                        .setNegativeButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatas.add(chooseMember);
                                String action = "insertLandMarkInSchedulePlanDay";
                                internet(action,tripId,day,chooseMember.getId());
                                mAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        LayoutInflater inflaterView = LayoutInflater.from(mContext);
        v = inflaterView.inflate(R.layout.check_in_share_choose_land_mark, null);
        listView = v.findViewById(R.id.lvCheckInShareChooseLandMark);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (position == i) {
                        listView.getChildAt(i).setBackgroundColor(Color.GREEN);
                        chooseMember = (LandMark) parent.getItemAtPosition(position);
                    } else {
                        listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }

            }
        });

        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/LocationServlet";
            List<LandMark> locations = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "All");
            //將內容轉成json字串
            retrieveLocationTask = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = retrieveLocationTask.execute().get();
                Type listType = new TypeToken<List<LandMark>>() {
                }.getType();
                //解析 json to gson
                locations = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (locations == null || locations.isEmpty()) {
                Common.showToast(mContext, R.string.msg_NoFoundLandMark);
            } else {
                showResult(locations);
            }
        } else {
            Common.showToast(mContext, R.string.msg_NoNetwork);
        }

//        initData();
        mRecyclerView = view.findViewById(R.id.planRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mAdapter = new MainAdapter(mContext, mDatas);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ItemTouchHelper.START | ItemTouchHelper.END) {

            /**
             *        拖拽到新位置时候的回调方法
             * @param viewHolder  拖动的ViewHolder
             * @param target   目标位置的ViewHolder
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
                //使用集合工具类Collections，分别把中间所有的item的位置重新交换
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mDatas, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mDatas, i, i - 1);
                    }
                }
                String action = "changePositionLandMarkInSchedulePlanDay";
                changeData(action,tripId,day,mDatas);
                //通知Adapter更新状态
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            /**
             *        滑动时的回调方法
             * @param viewHolder 滑动的ViewHolder
             * @param direction  滑动的方向
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mDatas.remove(position);//侧滑删除数据
                String action = "removePositionLandMarkInSchedulePlanDay";
                changeData(action,tripId,day,mDatas);
                mAdapter.notifyItemRemoved(position);//通知Adapter更新状态
            }

            //改变Item的透明度
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        return view;
    }

    //listView
    public void showResult(List<LandMark> locations) {
        listView.setAdapter(new LandMarkAdapter(mContext, locations));

    }

    private class LandMarkAdapter extends BaseAdapter {
        Context context;
        List<LandMark> memberList;

        LandMarkAdapter(Context context, List<LandMark> memberList) {
            this.context = context;
            this.memberList = memberList;
        }

        @Override
        public int getCount() {
            return memberList.size();
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.check_in_share_choose_land_mark_item, parent, false);
            }

            LandMark member = memberList.get(position);
            TextView tvName = itemView
                    .findViewById(R.id.tvCheckInShareChooseLandMarkId);
            tvName.setText(member.getName());
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return memberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return memberList.get(position).getId();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }


//    private void initData() {

//        mDatas = mDatas2;
//        for (int i = 'A'; i < 'Z'; i++) {
//            mDatas.add("" + (char) i);
//        }
//    }

    public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

        private Context mContext;
        private List<LandMark> mDatas;

        public MainAdapter(Context context, List<LandMark> mDatas) {
            this.mContext = context;
            this.mDatas = mDatas;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.swiplayout, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
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
        if (retrieveLocationTask != null) {
            retrieveLocationTask.cancel(true);
            retrieveLocationTask = null;
        }
    }

    public void internet(String action, int tripId, int day, int landMarkId){
        if (Common.networkConnected(getActivity())){
            String url = Common.URL + "/LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", action);
            jsonObject.addProperty("SchedulePlanDayTripId", tripId);
            jsonObject.addProperty("SchedulePlanDay", day);
            jsonObject.addProperty("SchedulePlanDayLandMark", landMarkId);
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Common.showToast(getActivity(), R.string.msg_InsertFail);
            } else {
                Common.showToast(getActivity(), R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }

    public void changeData(String action, int tripId, int day, List<LandMark> landMarks){
        if (Common.networkConnected(getActivity())){
            String url = Common.URL + "/LocationServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", action);
            jsonObject.addProperty("SchedulePlanDayTripId", tripId);
            jsonObject.addProperty("SchedulePlanDay", day);
            jsonObject.addProperty("SchedulePlanDayLandMark", new Gson().toJson(landMarks));
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Common.showToast(getActivity(), R.string.msg_InsertFail);
            } else {
                Common.showToast(getActivity(), R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }
}
