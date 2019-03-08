package com.example.molder.footprint.Schedule;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.molder.footprint.R;

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
    private List<String> mDatas;
    private List<String> mDatas2;
    private int iNumber = 0;

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
        mDatas = new ArrayList<>();
        mDatas2 = new ArrayList<>();

        //刷新
        swipeRefreshLayout =
                view.findViewById(R.id.planSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                initData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //加地標
        landMarkAdd = view.findViewById(R.id.landMarkAdd);
        landMarkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatas2.add("" + (char) iNumber+1);
            }
        });

        initData();
        mRecyclerView = view.findViewById(R.id.planRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        mAdapter = new MainAdapter(mContext,mDatas);
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

    @Override
    public void onStart() {
        super.onStart();

    }


    private void initData() {

        mDatas = mDatas2;
//        for (int i = 'A'; i < 'Z'; i++) {
//            mDatas.add("" + (char) i);
//        }
    }

    public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

        private Context mContext;
        private List<String> mDatas;

        public MainAdapter(Context context, List<String> mDatas) {
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
            holder.landMarkName.setText(mDatas.get(position));
            holder.landMarkAddress.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView landMarkName,landMarkAddress;

            public MyViewHolder(View view) {
                super(view);
                landMarkName = itemView.findViewById(R.id.schedulePlanDayLandMarkName);
                landMarkAddress = itemView.findViewById(R.id.schedulePlanDayLandMarkAddress);
            }
        }
    }

}
