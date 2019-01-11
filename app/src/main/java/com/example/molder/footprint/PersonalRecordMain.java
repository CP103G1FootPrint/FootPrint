package com.example.molder.footprint;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PersonalRecordMain {



//import java.util.ArrayList;
//import java.util.List;

    public class MainActivity extends AppCompatActivity {
        private RecyclerView recyclerView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.personal_activity_main);
//            handleViews();
        }

//        private void handleViews() {
//            recyclerView = findViewById(R.id.recyclerView);
//            recyclerView.setLayoutManager(
//                    new StaggeredGridLayoutManager(2,
//                            StaggeredGridLayoutManager.HORIZONTAL));
//            List<PersonalRecordParameter> memberList = getMemberList();
//            recyclerView.setAdapter(new MemberAdapter(this, memberList));
//        }
//
//        /* RecyclerView要透過RecyclerView.Adapter來處理欲顯示的清單內容，
//           必須建立RecyclerView.Adapter子類別並覆寫對應的方法：
//           getItemCount()、onCreateViewHolder()、onBindViewHolder */
//        private class MemberAdapter extends
//                RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
//            private Context context;
//            private List<PersonalRecordParameter> memberList;
//
//            MemberAdapter(Context context, List<PersonalRecordParameter> memberList) {
//                this.context = context;
//                this.memberList = memberList;
//            }
//
//            class MyViewHolder extends RecyclerView.ViewHolder {
//                ImageView imageView;
//                TextView tvId, tvName;
//
//                MyViewHolder(View itemView) {
//                    super(itemView);
//                    imageView = itemView.findViewById(R.id.imageView);
//                    tvId = itemView.findViewById(R.id.tvId);
//
//                }
//            }
//
//            @Override
//            public int getItemCount() {
//                return memberList.size();
//            }
//
//
//            @NonNull
//            @Override
//            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//                View itemView = LayoutInflater.from(context).
//                        inflate(R.layout.personal_record_item, viewGroup, false);
//                return new MyViewHolder(itemView);
//            }
//
//            @Override
//            public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
//                final PersonalRecordParameter member = memberList.get(position);
//                holder.imageView.setImageResource(member.getImage());
//                holder.tvId.setText(String.valueOf(member.getId()));
//
//                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        // 要呼叫CardView.setsetCardBackgroundColor()方能精準改變CardView背景
//                        CardView cardView = (CardView) holder.itemView;
//                        int colorActionDown = getResources().getColor(R.color.colorPrimary);
//                        switch (event.getAction()) {
//                            case MotionEvent.ACTION_DOWN:
//                                cardView.setCardBackgroundColor(colorActionDown);
//                                ImageView iv = new ImageView(context);
//                                iv.setImageResource(member.getImage());
//                                Toast toast = new Toast(context);
//                                toast.setView(iv);
//                                toast.setDuration(Toast.LENGTH_SHORT);
//                                toast.show();
//                                break;
//                            default:
//
//                                break;
//                        }
//                        return true;
//                    }
//                });
//            }
//        }
//
//        public List<PersonalRecordParameter> getMemberList() {
//            List<PersonalRecordParameter> memberList = new ArrayList<>();
//
//            return memberList;
//        }
//    }
    }
}
