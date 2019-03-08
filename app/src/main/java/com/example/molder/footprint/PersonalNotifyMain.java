package com.example.molder.footprint;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.molder.footprint.CheckInShare.Picture;
import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Map.InfoImageTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalNotifyMain extends Fragment {


    public PersonalNotifyMain() {
    }


    private RecyclerView recyclerView;
    private View personal_fragment;
    private TextView notifyType;
//    private TextView ;
    private TextView id;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        personal_fragment = inflater.inflate(R.layout.personal_main, container, false);
//        findViews();
        return personal_fragment;

    }

//    private void findViews() {
//
//        SharedPreferences preferences = getActivity().getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
//        String userID = preferences.getString("userId", "");
//
//        recyclerView = personal_fragment.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(
//                new StaggeredGridLayoutManager(1,
//                        StaggeredGridLayoutManager.VERTICAL));
//        List<PersonalNotifyMember> memberList = getMemberList();
//        recyclerView.setAdapter(new PersonalNotifyMain.PersonalNotifyAdapter(getActivity(), memberList));
//
//
//
//        imageView = personal_fragment.findViewById(R.id.imageView);
//        btTakePictureLarge = personal_fragment.findViewById(R.id.btTakePictureLarge);
//        btPickPicture = personal_fragment.findViewById(R.id.btPickPicture);
//        id = personal_fragment.findViewById(R.id.id);
//        notify = personal_fragment.findViewById(R.id.notify);
//
////        b.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent intent = new Intent(getActivity(), PersonalSelfieMainActivity.class);
////                startActivity(intent);
////            }
////        });
//
//
//    }
//
//    /* RecyclerView要透過RecyclerView.Adapter來處理欲顯示的清單內容，
//  必須建立RecyclerView.Adapter子類別並覆寫對應的方法：
//  getItemCount()、onCreateViewHolder()、onBindViewHolder */
//    private class PersonalNotifyAdapter extends
//            RecyclerView.Adapter<PersonalNotifyMain.PersonalNotifyAdapter.MyViewHolder> {
//        private Context context;
//        private List<PersonalNotifyMember> memberList;
//
//        PersonalNotifyAdapter(Context context, List<PersonalNotifyMember> memberList) {
//            this.context = context;
//            this.memberList = memberList;
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//
//            ImageView personal_pic;
//            TextView id;
//            TextView notify;
//
//            MyViewHolder(View itemView) {
//                super(itemView);
//                personal_pic = itemView.findViewById(R.id.personal_pic);
//                id = itemView.findViewById(R.id.id);
//                notify = itemView.findViewById(R.id.notify);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return memberList.size();
//        }
//
//
//        @NonNull
//        @Override
//        public PersonalNotifyMain.PersonalNotifyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//            View itemView = LayoutInflater.from(context).
//                    inflate(R.layout.personal_notify_item, viewGroup, false);
//            return new PersonalNotifyMain.PersonalNotifyAdapter.MyViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull final PersonalNotifyMain.PersonalNotifyAdapter.MyViewHolder holder, int position) {
//            final PersonalNotifyMember member = memberList.get(position);
//            holder.personal_pic.setImageResource(member.getImage());
//            holder.id.setText(String.valueOf(member.getId()));
//            holder.notify.setText(String.valueOf(member.getNotify()));
//
//
//        }
//    }

    }

