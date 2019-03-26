package com.example.molder.footprint.Schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class ScheduleAlbumInsertActivity extends AppCompatActivity {
    private ImageView shAlbumImgConfirm ;
    private Button shAlbumShareBt ,shAlbumCancelBt ;
    private byte[] image;
    private Bitmap srcImage ;
    private final static String TAG = "ScheduleAlbumInsertActivity";
    private String userID ;
    private int tripId ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_album_insert);
        handleViews();


        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        tripId = intent.getIntExtra("tripId",0);
        srcImage = BitmapFactory.decodeFile(imagePath);
        Bitmap downsizedImage = downSize(srcImage,512);
        shAlbumImgConfirm.setImageBitmap(downsizedImage);

    }

    private void handleViews(){
        shAlbumImgConfirm = findViewById(R.id.shAlbumImgConfirm);
        shAlbumShareBt =findViewById(R.id.shAlbumShareBt);
        shAlbumCancelBt = findViewById(R.id.shAlbumShareBt);
    }

    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        // 如果欲縮小的尺寸過小，就直接定為128
        if (newSize <= 50) {
            newSize = 128;
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



    public void insertCancelOnClick (View view){
        finish();

    }

    @SuppressLint("LongLogTag")
    public void shareOnClick (View view){


        if (srcImage == null){
            Common.showToast(this,R.string.msg_NoImage);
            return;
        }

        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        userID = preferences.getString("userId", "");

        //打開上一頁的打包的資料
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            Trip trip = (Trip) bundle.getSerializable("trip");
//            if (trip != null) {
//                tripId = trip.getTripID();
//            }
//        }


        if(Common.networkConnected(this)){
            String url = Common.URL +"/GroupAlbumServlet" ;
//            GroupAlbum groupAlbum = new GroupAlbum(tripId);
            byte[] image = ImageTask.bitmapToPNG(srcImage);
            String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "groupalbumInsert");
            jsonObject.addProperty("tripId", tripId);
            jsonObject.addProperty("imageBase64", imageBase64);
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            }catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (count == 0) {
//                Common.showToast(this, R.string.msg_InsertFail);
            } else {
//                Common.showToast(this, R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
        finish();

    }




}
