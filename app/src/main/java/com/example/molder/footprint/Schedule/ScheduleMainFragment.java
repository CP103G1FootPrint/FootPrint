package com.example.molder.footprint.Schedule;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Personal_Friendship_Friends;
import com.example.molder.footprint.Login.MainLoginIn;
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
    private CommonTask tripDeleteTask, retrieveTripTask, tripShareTask;
    private ImageTask tripImageTask;
    private RecyclerView recyclerView;
    private Spinner shSpinner;

    private String[] list_items;
    private List<String> friends = new ArrayList<>();
    private List<String> friendship_Friends = new ArrayList<>();
    private boolean[] checked_items;
    private ArrayList<Integer> items_selected = new ArrayList<>();
    private String createID;


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
                (activity, R.array.notify_array, R.layout.support_simple_spinner_dropdown_item);
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


    private class TripAdapter extends RecyclerSwipeAdapter<TripAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        //        private Context context;
        private List<Trip> trips;
        private int imageSize;

        public TripAdapter(Context context, List<Trip> trips) {
            layoutInflater = LayoutInflater.from(context);
//            this.context = context;
            this.trips = trips;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return position;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private TextView shTvTitle;
            private TextView shTvDate;
            private ImageButton shadd, shBtPhoto, shBtChat;
            private SwipeLayout scheduleSwipLayout;
            private Button scheduleButton, scheduleShare;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.shImgView);
                shTvTitle = itemView.findViewById(R.id.shTvTitle);
                shTvDate = itemView.findViewById(R.id.shTvDate);
                shadd = itemView.findViewById(R.id.shBtAdd);
                shBtPhoto = itemView.findViewById(R.id.shBtPhoto);
                shBtChat = itemView.findViewById(R.id.shBtChat);
                scheduleSwipLayout = itemView.findViewById(R.id.schedule_swipe_layout);
                scheduleButton = itemView.findViewById(R.id.schedule_bottom);
                scheduleShare = itemView.findViewById(R.id.schedule_share);
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
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
            myViewHolder.scheduleSwipLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
//            LayoutInflater inflater = LayoutInflater.from(activity);
//            final View v = inflater.inflate(R.layout.fragment_forgot_password, null);
            final Trip trip = trips.get(i);
            String url = Common.URL + "/TripServlet"; //圖還未載入
            final int id = trip.getTripID();


            //目前登入的使用者
            SharedPreferences preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
            final String userId = preferences.getString("userId", "");

            //抓取所有與使用者關係為好友的資料
            if (Common.networkConnected(activity)) {
                String urls = Common.URL + "/FriendsServlet";

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getTripFriends");
                jsonObject.addProperty("userId", userId);
                String jsonOut = jsonObject.toString();
                CommonTask friendsCommonTask = new CommonTask(urls, jsonOut);
                try {
                    //取得所有好友array (array內包含自己)
                    String jsonIn = friendsCommonTask.execute().get();
                    Type listType = new TypeToken<List<String>>() {
                    }.getType();
                    friendship_Friends = new Gson().fromJson(jsonIn, listType);
                    for(int j =0;j<friendship_Friends.size();j++){
                        if(friendship_Friends.get(j).equals(userId)){
                            friendship_Friends.remove(j);
                        }
                    }
                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
                }
                if (friendship_Friends == null || friendship_Friends.isEmpty()) {
//                    Common.showToast(activity, R.string.msg_NoNewsFound);
                } else {

                }
            } else {
                Common.showToast(activity, R.string.msg_NoNetwork);
            }



            //trip圖片
            tripImageTask = new ImageTask(url, id, imageSize, myViewHolder.imageView);
            //主執行緒繼續執行 新開的執行緒去抓圖，抓圖需要網址、id ，抓到圖後show在imageView上
            tripImageTask.execute(); //ImageTask類似MyTask 去server端抓圖

            myViewHolder.shTvTitle.setText(trip.getTitle());
            myViewHolder.shTvDate.setText(trip.getDate());
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, SchedulePlanActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("trip", trip);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });

            //刪除行程
            myViewHolder.scheduleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.delete)
                            .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL + "/TripServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "tripDelete");
                                        jsonObject.addProperty("tripId", id);
                                        int count = 0;
                                        try {
                                            tripDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = tripDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
//                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
//                                            Common.showToast(activity, R.string.msg_DeleteFail);
                                        } else {
                                            trips.remove(i);
                                            notifyDataSetChanged();
//                                            Common.showToast(activity, R.string.msg_DeleteSuccess);
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.msg_NoNetwork);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.textCancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });

            //分享行程
            myViewHolder.scheduleShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.textShare)
                            .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL + "/TripServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "tripShare");
                                        jsonObject.addProperty("openState", "open");
                                        jsonObject.addProperty("tripId", id);
                                        int count = 0;
                                        try {
                                            tripShareTask = new CommonTask(url, jsonObject.toString());
                                            String result = tripShareTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
//                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
//                                            Common.showToast(activity, R.string.msg_ShareFail);
                                        } else {

//                                            Common.showToast(activity, R.string.msg_ShareSuccess);
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.msg_NoNetwork);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.textCancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });

            //增加朋友
            myViewHolder.shadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //抓取行程好友
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL + "/TripServlet";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "getTripFriends");
                        jsonObject.addProperty("tripId", id);
                        CommonTask friendsCommonTask = new CommonTask(url, jsonObject.toString());
                        try {
                            String jsonIn = friendsCommonTask.execute().get();
                            Type listType = new TypeToken<List<String>>() {
                            }.getType();
                            friends = new Gson().fromJson(jsonIn, listType);
                            if(friends.isEmpty()){
                                int count = friendship_Friends.size();
                                list_items = null;
                                list_items = new String[count];
                                for(int k = 0; k < count ; k++){
                                    list_items[k] = friendship_Friends.get(k);
                                }
                            }else {
                                List<String> friendsAll = new ArrayList<>();
                                friendsAll.addAll(friendship_Friends);
                                friendsAll.removeAll(friends);
                                int count = friendsAll.size();
                                list_items = null;
                                list_items = new String[count];
                                for(int k = 0; k < count ; k++){
                                    list_items[k] = friendsAll.get(k);
                                }
                            }
                        } catch (Exception e) {
//                            Log.e(TAG, e.toString());
                        }
                    }
                    checked_items = new boolean[list_items.length];
                    AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
                    myBuilder.setTitle(R.string.textAddFriendtoGroup);
                    myBuilder.setMultiChoiceItems(list_items, checked_items, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (!items_selected.contains(which)) {
                                items_selected.add(which);
                            } else {
                                items_selected.remove(which);
                            }
                        }
                    });
                    myBuilder.setCancelable(false);
                    myBuilder.setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<TripPlanFriend> tripPlanFriends = new ArrayList<>();
                            String items;
                            for (int i = 0; i < items_selected.size(); i++) {
                                items = list_items[items_selected.get(i)];
                                TripPlanFriend tripPlanFriend = new TripPlanFriend(userId,items,id);
                                tripPlanFriends.add(tripPlanFriend);
                            }
                            if (Common.networkConnected(activity)) {
                                String url = Common.URL + "/TripServlet";
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "tripPlanFriendInsert");
                                jsonObject.addProperty("tripPlanFriends", new Gson().toJson(tripPlanFriends));
                                int count = 0;
                                try {
                                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                    count = Integer.valueOf(result);
                                } catch (Exception e) {
//                                    Log.e(TAG, e.toString());
                                }
                                if (count == 0) {
//                                    Common.showToast(activity, R.string.msg_InsertFail);
                                } else {
//                                    Common.showToast(activity, R.string.msg_InsertSuccess);
                                }
                            } else {
                                Common.showToast(activity, R.string.msg_NoNetwork);
                            }

                        }
                    });
                    myBuilder.setNegativeButton(R.string.textCancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = myBuilder.create();
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
                    Intent intent = new Intent(activity, ScheduleChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("trip", trip);
                    /* 將Bundle儲存在Intent內方便帶至下一頁 */
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });


        }


    }


    private void handleViews(View view) {
        //取得目前登入使用者id
        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        createID = preferences.getString("userId", "");
        shSpinner = view.findViewById(R.id.shSpinner);
        shSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if (Common.networkConnected((activity))) {
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
//                        Log.e(TAG, e.toString());
                    }
                    if (trips == null || trips.isEmpty()) {
//                        Toast.makeText(activity, R.string.msg_NoTripsFound, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getActivity(), ScheduleCreateActivity.class);
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
        if (retrieveTripTask != null) {
            retrieveTripTask.cancel(true);
            retrieveTripTask = null;
        }
        if (tripShareTask != null) {
            tripShareTask.cancel(true);
            tripShareTask = null;
        }
    }

//    private List<Trip>getTrip(){
//        List<Trip> trips = new ArrayList<>();
//        trips.add(new Trip(R.drawable.sh_mountaine,"TripName","2019/05/24"));
//        return trips ;
//    }


}
