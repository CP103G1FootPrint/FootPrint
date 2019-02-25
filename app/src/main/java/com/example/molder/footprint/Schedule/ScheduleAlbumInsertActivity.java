package com.example.molder.footprint.Schedule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;


import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.R;




public class ScheduleAlbumInsertActivity extends AppCompatActivity {
    private ImageView shAlbumImgConfirm ;
    private Button shAlbumShareBt ,shAlbumCancelBt ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_album_insert);
        handleViews();

//        Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
//        Bitmap downsizedImage = Common.downSize(srcImage, newSize);
//        imageView.setImageBitmap(downsizedImage);

    }

    private void handleViews(){
        shAlbumImgConfirm = findViewById(R.id.shAlbumImgConfirm);
        shAlbumShareBt =findViewById(R.id.shAlbumShareBt);
        shAlbumCancelBt = findViewById(R.id.shAlbumShareBt);
    }




}
