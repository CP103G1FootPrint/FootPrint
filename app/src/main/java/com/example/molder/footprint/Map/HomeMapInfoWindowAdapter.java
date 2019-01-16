package com.example.molder.footprint.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class HomeMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private static final String TAG = "TAG_HomeMapInfo";
    private View infoWindow;
    private LandMark landMark;
    private int imageSize;
    private ImageTask landMarkImageTask;

    HomeMapInfoWindowAdapter(Context context, LandMark landMark) {
        infoWindow = View.inflate(context, R.layout.home_map_info_window, null);
        this.landMark = landMark;
        imageSize = context.getResources().getDisplayMetrics().widthPixels / 3;
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        ImageView imageView = infoWindow.findViewById(R.id.imageView);
        String url = Common.URL + "/LocationServlet";
        int id = landMark.getId();
//        Bitmap bitmap = null;
//        try {
//            landMarkImageTask = new ImageTask(url, id, imageSize);
//            // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
//            bitmap = landMarkImageTask.execute().get();
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//        } else {
//            imageView.setImageResource(R.drawable.default_image);
//        }
        TextView tvTitle = infoWindow.findViewById(R.id.tvHomeMapTitle);
        tvTitle.setText(marker.getTitle());

        TextView tvSnippet = infoWindow.findViewById(R.id.tvHomeMapSnippet);
        tvSnippet.setText(marker.getSnippet());
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
