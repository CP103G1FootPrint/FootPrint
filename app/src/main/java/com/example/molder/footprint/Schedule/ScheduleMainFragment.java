package com.example.molder.footprint.Schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Message;
import com.example.molder.footprint.Map.LandMark;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ScheduleMainFragment extends Fragment {
    private static final String TAG = "TripListFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentActivity activity;
    private CommonTask tripGetAllTask;
    private CommonTask tripDeleteTask,retrieveTripTask;
    private ImageTask tripImageTask;
    private RecyclerView recyclerView ;
    private Spinner shSpinner ;

    private String[] list_items;
    private boolean[] checked_items ;
    private ArrayList<Integer> items_selected = new ArrayList<>();
    private String createID ;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_main, container, false);
        swipeRefreshLayout =
                view.findViewById(R.id.shSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true); //開啟動畫
//                showAllTrips(); //顯示所有旅遊景點
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        handleViews(view);
        ArrayAdapter<CharSequence> nAdapter = ArrayAdapter.createFromResource
                (activity,R.array.notify_array,R.layout.support_simple_spinner_dropdown_item);
        shSpinner.setAdapter(nAdapter);

        return view;

//        recyclerView.setAdapter(new TripAdapter(this,getTrip()));

    }

//    private void showAllTrips() {
//        if (Common.networkConnected(activity)) {
//            String url = Common.URL + "/TripServlet";
//            List<Trip> trips = null;
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "All");
//            String jsonOut = jsonObject.toString();
//            tripGetAllTask = new CommonTask(url, jsonOut);
//            try {
//                String jsonIn = tripGetAllTask.execute().get();
//                Type listType = new TypeToken<List<Trip>>() {
//                }.getType();
//                trips = new Gson().fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            if (trips == null || trips.isEmpty()) {
//                Common.showToast(activity, R.string.msg_NoTripsFound);
//            } else {
//                recyclerView.setAdapter(new TripAdapter(activity, trips));
//            }
//        } else {
//            Common.showToast(activity, R.string.msg_NoNetwork);
//        }
//    }


    @Override
    public void onStart() {

        super.onStart();
//        showAllTrips(); //重刷抓資料
    }



    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder>{
        private LayoutInflater layoutInflater;
        //        private Context context;
        private List<Trip> trips;
        private int imageSize;

        public TripAdapter(Context context,List<Trip>trips){
            layoutInflater = LayoutInflater.from(context);
//            this.context = context;
            this.trips = trips;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView ;
            TextView shTvTitle ;
            TextView shTvDate ;
            ImageButton shadd,shBtPhoto,shBtChat;
            public MyViewHolder(View itemView){
                super(itemView);
                imageView = itemView.findViewById(R.id.shImgView);
                shTvTitle = itemView.findViewById(R.id.shTvTitle);
                shTvDate =itemView.findViewById(R.id.shTvDate);
                shadd = itemView.findViewById(R.id.shBtAdd);
                shBtPhoto =itemView.findViewById(R.id.shBtPhoto);
                shBtChat = itemView.findViewById(R.id.shBtChat);
            }
        }

        @NonNull
        @Override //載入item_view的layout檔
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.schedule_main_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            final Trip trip = trips.get(i);
            String url = Common.URL + "/TripServlet"; //圖還未載入
            int id = trip.getTripID();
            tripImageTask = new ImageTask(url, id, imageSize, myViewHolder.imageView);
            //主執行緒繼續執行 新開的執行緒去抓圖，抓圖需要網址、id ，抓到圖後show在imageView上
            tripImageTask.execute(); //ImageTask類似MyTask 去server端抓圖

            myViewHolder.shTvTitle.setText(trip.getTitle());
            myViewHolder.shTvDate.setText(trip.getDate());
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity,SchedulePlanActivity.class);
                    startActivity(intent);

                }
            });
//            myViewHolder.imageView.setImageResource(trip.getImageid());

//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

            myViewHolder.shadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
                    myBuilder.setTitle(R.string.textAddtoGroup);
                    myBuilder.setMultiChoiceItems(list_items, checked_items, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (!items_selected.contains(which)){
                                items_selected.add(which);
                            }else {
                                items_selected.remove(which);
                            }
                        }
                    }) ;
                    myBuilder.setCancelable(false);
                    myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String items = "" ;
                            for (int i=0;i<items_selected.size();i++){
                                items = items + list_items[items_selected.get(i)];
                                if (i!= items_selected.size()-1){
                                    items = items+ "";
                                }
                            }

                        }
                    });
                    myBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = myBuilder.create() ;
                    dialog.show();











//                    Fragment fragment = new ScheduleFriendListFragment();
//                    changeFragment(fragment);

//                    LayoutInflater inflater = LayoutInflater.from(getActivity());
//                    final View a = inflater.inflate(R.layout.schedule_friendlist, null);


//                    new AlertDialog.Builder(getActivity())
//
//                            .setTitle(R.string.textAddtoGroup)
//
//                            .setMultiChoiceItems(
//                                    new String[]{"May", "Jack", "Sunny","Vivian","Tom"},
//                                    new boolean[]{false,true,true,false,false},
//                                    new DialogInterface.OnMultiChoiceClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                                            Toast.makeText(getActivity(), "which " + which + ", isChecked " + isChecked, Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//
//                            .setView(a)
//                            .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(getActivity(), R.string.textSuccess, Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .setNegativeButton(R.string.textCancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            })
//                            .show();

                }
            });
            myViewHolder.shBtPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new ScheduleAlbumFragment();
//                    ScheduleAlbumFragment fragment = new ScheduleAlbumFragment();
//                    FragmentManager fragmentManager = getFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("trip", trip);
                    fragment.setArguments(bundle);
                    changeFragment(fragment);
//


                }
            });
            myViewHolder.shBtChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity,ScheduleChatActivity.class);


                    Bundle bundle = new Bundle();
                    bundle.putSerializable("trip", trip );
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });




        }

    }





    private void handleViews(View view){
        //取得目前登入使用者id
        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        createID = preferences.getString("userId", "");
        shSpinner =view.findViewById(R.id.shSpinner);
        shSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if (Common.networkConnected((activity) )) {
                    String url = Common.URL + "/TripServlet";
                    List<Trip> trips = null;
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", adapterView.getSelectedItem().toString());
                    jsonObject.addProperty("type", adapterView.getSelectedItem().toString());

                    jsonObject.addProperty("createID", createID);

                    //將內容轉成json字串
                    retrieveTripTask = new CommonTask(url, jsonObject.toString());
                    try {
                        String jsonIn = retrieveTripTask.execute().get();
                        Type listType = new TypeToken<List<Trip>>() {
                        }.getType();
                        //解析 json to gson
                        trips = new Gson().fromJson(jsonIn, listType);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (trips == null || trips.isEmpty()) {
                        Toast.makeText(activity, R.string.msg_NoTripsFound, Toast.LENGTH_SHORT).show();
                    } else {
//                        showAllTrips();
                        recyclerView.setAdapter(new TripAdapter(activity, trips));
                    }
                } else {
                    Toast.makeText(activity, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView = view.findViewById(R.id.shRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        FloatingActionButton shBtAdd = view.findViewById(R.id.shBtAdd);
        shBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ScheduleCreateActivity.class);
                startActivity(intent);
//                Fragment fragment = new ScheduleCreateFragment();
//                changeFragment(fragment);
            }
        });

//        shSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


    }

    private void changeFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().
                    replace(R.id.content, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tripGetAllTask != null) {
            tripGetAllTask.cancel(true);
            tripGetAllTask = null;
        }

        if (tripImageTask != null) {
            tripImageTask.cancel(true);
            tripImageTask = null;
        }

        if (tripDeleteTask != null) {
            tripDeleteTask.cancel(true);
            tripDeleteTask = null;
        }
    }

//    private List<Trip>getTrip(){
//        List<Trip> trips = new ArrayList<>();
//        trips.add(new Trip(R.drawable.sh_mountaine,"TripName","2019/05/24"));
//        return trips ;
//    }


}
