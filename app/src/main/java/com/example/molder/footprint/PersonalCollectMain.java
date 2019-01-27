package com.example.molder.footprint;


import android.content.Context;
import android.content.Intent;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCollectMain extends Fragment {


    public PersonalCollectMain() {
        // Required empty public constructor
    }

    private ImageButton b;
    private View personal_fragment;
    private ImageView imageView;
    private Button btTakePictureLarge, btPickPicture;
    private File file;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        personal_fragment = inflater.inflate(R.layout.personal_main, container, false);
        findViews();
        return personal_fragment;

    }

    private void findViews() {
        b = personal_fragment.findViewById(R.id.persona_pic);
        recyclerView = personal_fragment.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL));
        List<PersonalCollectMember> memberList = getMemberList();
        recyclerView.setAdapter(new PersonalCollectAdapter(getActivity(), memberList));


        imageView = personal_fragment.findViewById(R.id.imageView);
        btTakePictureLarge = personal_fragment.findViewById(R.id.btTakePictureLarge);
        btPickPicture = personal_fragment.findViewById(R.id.btPickPicture);

//            b.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), PersonalSelfieMainActivity.class);
//                    startActivity(intent);
//                }
//            });
    }

    /* RecyclerView要透過RecyclerView.Adapter來處理欲顯示的清單內容，
   必須建立RecyclerView.Adapter子類別並覆寫對應的方法：
   getItemCount()、onCreateViewHolder()、onBindViewHolder */
    private class PersonalCollectAdapter extends
            RecyclerView.Adapter<PersonalCollectAdapter.MyViewHolder> {
        private Context context;
        private List<PersonalCollectMember> memberList;

        PersonalCollectAdapter(Context context, List<PersonalCollectMember> memberList) {
            this.context = context;
            this.memberList = memberList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
//            TextView tvId, tvName;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
//                tvId = itemView.findViewById(R.id.tvId);
//                tvName = itemView.findViewById(R.id.tvName);
            }
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.personal_collect_item, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final PersonalCollectMember member = memberList.get(position);
            holder.imageView.setImageResource(member.getImage());
//            holder.tvId.setText(String.valueOf(member.getId()));
//            holder.tvName.setText(member.getName());

//            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    // 要呼叫CardView.setsetCardBackgroundColor()方能精準改變CardView背景
//                    CardView cardView = (CardView) holder.itemView;
//                    int colorActionDown = getResources().getColor(R.color.colorPrimary);
//                    int colorActionUp = getResources().getColor(R.color.colorLightGray);
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            cardView.setCardBackgroundColor(colorActionDown);
//                            ImageView iv = new ImageView(context);
//                            iv.setImageResource(member.getImage());
//                            Toast toast = new Toast(context);
//                            toast.setView(iv);
//                            toast.setDuration(Toast.LENGTH_SHORT);
//                            toast.show();
//                            break;
//                        default:
//                            cardView.setCardBackgroundColor(colorActionUp);
//                            break;
//                    }
//                    return true;
//                }
//            });
        }
    }

    public List<PersonalCollectMember> getMemberList() {
        List<PersonalCollectMember> memberList = new ArrayList<>();
        memberList.add(new PersonalCollectMember(23, R.drawable.sticker, "John"));
        memberList.add(new PersonalCollectMember(75, R.drawable.sticker, "Jack"));
        memberList.add(new PersonalCollectMember(65, R.drawable.sticker, "Mark"));
        memberList.add(new PersonalCollectMember(23, R.drawable.sticker, "John"));
        memberList.add(new PersonalCollectMember(75, R.drawable.sticker, "Jack"));
        memberList.add(new PersonalCollectMember(65, R.drawable.sticker, "Mark"));
        memberList.add(new PersonalCollectMember(23, R.drawable.sticker, "John"));
        memberList.add(new PersonalCollectMember(75, R.drawable.sticker, "Jack"));
        memberList.add(new PersonalCollectMember(65, R.drawable.sticker, "Mark"));
        memberList.add(new PersonalCollectMember(23, R.drawable.sticker, "John"));
        memberList.add(new PersonalCollectMember(75, R.drawable.sticker, "Jack"));
        memberList.add(new PersonalCollectMember(65, R.drawable.sticker, "Mark"));

        return memberList;
    }


}
