package com.example.molder.footprint.Schedule;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.molder.footprint.R;

import java.util.ArrayList;
import java.util.List;


public class SchedulePlanDayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int position;
    private TextView textView;
    private View view;

    private RecyclerView mrecycleView;
    private List<String> list;
    private MyRecyclerViewAdapter adapter;
    private Context mContext;


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

    public static SchedulePlanDayFragment newInstances(int position) {
        SchedulePlanDayFragment fragment = new SchedulePlanDayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_plan_day, container, false);

        mContext = getActivity();
        mrecycleView = view.findViewById(R.id.mRecycleView);
        list = new ArrayList<String>();
        for(int i = 0 ; i < 5 ; i++ ){
            list.add("条目" + i );
        }
        adapter = new MyRecyclerViewAdapter(mContext,list);
        mrecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mrecycleView.setAdapter(adapter);

        return view;
    }

    public class MyRecyclerViewAdapter extends RecyclerSwipeAdapter<MyRecyclerViewAdapter.MyViewHolder> {
        private Context context;
        private List<String> list;

        public MyRecyclerViewAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.swiplayout,parent,false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            viewHolder.surface.setText(list.get(position));
            viewHolder.bottom.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return position;
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            private SwipeLayout swipeLayout;
            private Button bottom;
            private TextView surface;
            public MyViewHolder(View itemView) {
                super(itemView);
                swipeLayout = itemView.findViewById(R.id.swipe_layout);
                bottom = itemView.findViewById(R.id.bottom);
                surface = itemView.findViewById(R.id.surface);
            }
        }


    }


}
