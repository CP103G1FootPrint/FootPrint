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

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
        Bitmap downsizedImage = downSize(srcImage, 512);
        shAlbumImgConfirm.setImageBitmap(downsizedImage);

    }

    private void handleViews(){
        shAlbumImgConfirm = findViewById(R.id.shAlbumImgConfirm);
        shAlbumShareBt =findViewById(R.id.shAlbumShareBt);
        shAlbumCancelBt = findViewById(R.id.shAlbumShareBt);
    }

    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        if (newSize <= 20) {
            // 如果欲縮小的尺寸過小，就直接定為20
            newSize = 20;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
//        Log.d(TAG, text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " + scale + "\nscaled image size = " +
                    srcBitmap.getWidth() + "x" + srcBitmap.getHeight();
//            Log.d(TAG, text);
        }
        return srcBitmap;
    }




}
