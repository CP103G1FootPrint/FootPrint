package com.example.molder.footprint.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.molder.footprint.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageTask extends AsyncTask<Object, Integer, Bitmap> {
    private final static String TAG = "ImageTask";
    private String url;
    private int id, imageSize;
    /* ImageTask的屬性strong參照到SpotListFragment內的imageView不好，
        會導致SpotListFragment進入背景時imageView被參照而無法被釋放，
        而且imageView會參照到Context，也會導致Activity無法被回收。
        改採weak參照就不會阻止imageView被回收 */
    private WeakReference<ImageView> imageViewWeakReference;

    public ImageTask(String url, int id, int imageSize) {
        this(url, id, imageSize, null);
    }

    public ImageTask(String url, int id, int imageSize, ImageView imageView) {
        this.url = url;
        this.id = id;
        if(imageSize >512){
            this.imageSize = 512;
        }else{
            this.imageSize = imageSize;
        }

        //改採weak參照就不會阻止imageView被回收
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("imageSize", imageSize);
        return getRemoteImage(url, jsonObject.toString());
    }

    //刷新到手機上並顯示
    //主執行緒做
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        //用WeakReference要檢查imageView是不是空值
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            //如果是空值就顯示小綠人
            imageView.setImageResource(R.drawable.default_image);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
//            Log.d(TAG, "output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(
                        new BufferedInputStream(connection.getInputStream()));
            } else {
//                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
//            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }


    public static byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 轉成PNG不會失真，所以quality參數值會被忽略
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);//Bitmap可以壓縮的功能

        return baos.toByteArray();
    }


}