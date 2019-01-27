package com.example.molder.footprint;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeNewsActivity_Personal extends AppCompatActivity {
    private CircleImageView profile＿picture;
    private RecyclerView recyclerView;
    private ImageView home_news_personal_addFriend;
    private AppCompatActivity HomeNewsActivity_Personal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_news__personal);
        profile＿picture = findViewById(R.id.ci_profile＿picture);
        handleViews();
        showResults();
        handlehandleViews();

    }

    private void handlehandleViews(){
        home_news_personal_addFriend = findViewById(R.id.home_news_personal_addFriend);
        home_news_personal_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeNewsActivity_Personal.this, HomeNewsActivity_Personal_Friendship.class);
                startActivity(intent);
            }
        });

    }

    //上一頁打包的資料
    private void showResults() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            /* Bundle物件呼叫getSerializable()可以取得前頁儲存的Serializable物件 */
            HomeNewsFragment_News homeNewsFragmentNews = (HomeNewsFragment_News) bundle.getSerializable("news");
            if (homeNewsFragmentNews != null) {
                profile＿picture.setImageResource(homeNewsFragmentNews.getProfilePictureId());

            }
        }
    }

    //使用者照片的recyclerView
    private class PersonalPicturesAdapter extends RecyclerView.Adapter{
        Context context;
        List<HomeNewsFragment_PersonalPictures> personalPictures;
//        private int imageSize;
        public PersonalPicturesAdapter(Context context,
                                       List<HomeNewsFragment_PersonalPictures> PersonalPictures) {
            this.context = context;
            this.personalPictures = PersonalPictures;
//            imageSize = getResources().getDisplayMetrics().widthPixels / 3; //getDisplayMetrics()取得目前螢幕
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.activity_home_news_personal_pictureitem,viewGroup,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final HomeNewsFragment_PersonalPictures personalPicture = personalPictures.get(i);
            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
            myViewHolder.imageView.setImageResource(personalPicture.getPersonalNewsPictureId());
        }

        @Override
        public int getItemCount() {
            return personalPictures.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_home_news_personal_pictureItem);
        }
    }

    private void handleViews(){
        recyclerView = findViewById(R.id.rv_home_news_personal_pictures);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        List<HomeNewsFragment_PersonalPictures> PersonalPicturesList = getPersonalPictures();
        recyclerView.setAdapter(new PersonalPicturesAdapter(this, PersonalPicturesList));
    }

    protected List<HomeNewsFragment_PersonalPictures> getPersonalPictures(){
        List<HomeNewsFragment_PersonalPictures> personalPictures = new ArrayList<>();
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture1));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture2));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture3));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture4));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture5));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture6));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture6));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture5));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture4));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture3));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture2));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture1));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture1));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture2));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture3));
        personalPictures.add(new HomeNewsFragment_PersonalPictures(R.drawable.testpicture4));
        return personalPictures;
    }
}
