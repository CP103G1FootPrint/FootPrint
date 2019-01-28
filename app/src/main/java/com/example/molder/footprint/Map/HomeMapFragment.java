package com.example.molder.footprint.Map;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.Login.LoginTerms;
import com.example.molder.footprint.R;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private final static String TAG = "GeocoderActivity";
    private static final int REQ_PERMISSIONS = 0;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private CommonTask retrieveCategoryTask, retrieveLocationTask;
    private Spinner spinner;
    private LandMark landMark;
    private View view;

    public HomeMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_home_map, container, false);
        //地理編碼服務 ( 地址定位 )
        geocoder = new Geocoder(mContext);
        //取得管理器
        FragmentManager fm = getChildFragmentManager();/// getChildFragmentManager()//getActivity().getSupportFragmentManager();
        //layout連結
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.googleMap);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.googleMap, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

        //取得系統定位服務
//        LocationManager status = (LocationManager) (mContext.getSystemService(Context.LOCATION_SERVICE));
//        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
//            locationServiceInitial();
//        } else {
////            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
//        }


        spinner = view.findViewById(R.id.btnMapState);
        //下拉選單
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
//                Toast.makeText(mContext, "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                googleMap.clear();
                if (Common.networkConnected((Activity) mContext)) {
                    String url = Common.URL + "/LocationServlet";
                    List<LandMark> locations = null;
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", adapterView.getSelectedItem().toString());
                    jsonObject.addProperty("type", adapterView.getSelectedItem().toString());
                    //將內容轉成json字串
                    retrieveLocationTask = new CommonTask(url, jsonObject.toString());
                    try {
                        String jsonIn = retrieveLocationTask.execute().get();
                        Type listType = new TypeToken<List<LandMark>>() {
                        }.getType();
                        //解析 json to gson
                        locations = new Gson().fromJson(jsonIn, listType);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (locations == null || locations.isEmpty()) {
                        Toast.makeText(mContext, R.string.msg_NoFoundLandMark, Toast.LENGTH_SHORT).show();
                    } else {
                        showMap(locations);
                    }
                } else {
                    Toast.makeText(mContext, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }
            }

            public void onNothingSelected(AdapterView arg0) {
//                Toast.makeText(mContext, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        askPermissions();
        Object item = spinner.getSelectedItem();
        String category = item.toString().trim();
        Toast.makeText(mContext, category,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnInfoWindowClickListener(this);
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
//            LocationManager locMan = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
//            Location loc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (loc != null) {
//                Double lat = loc.getLatitude();
//                Log.d(TAG, "latitude: " + lat);
//                Double lng = loc.getLongitude();
//                Log.d(TAG, "longitude: " + lng);
//            }
        }


//        暫時替代
//        LatLng sydney = new LatLng(24.881243, 121.287698);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        // focus on the spot
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(sydney)
//                .zoom(9)
//                .build();
//        CameraUpdate cameraUpdate = CameraUpdateFactory
//                .newCameraPosition(cameraPosition);
//        googleMap.animateCamera(cameraUpdate);

    }

    private void showMap(List<LandMark> locations) {
        for (int index = 0; index < locations.size(); index++) {
            LandMark location = locations.get(index);

            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            String snippet = getString(R.string.col_Name) + ": " + location.getName() + "\n" +
                    getString(R.string.col_Type) + ": " + location.getType() + "\n" +
                    getString(R.string.col_Address) + ": " + location.getAddress();

            // focus on the spot
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)
                    .zoom(10)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newCameraPosition(cameraPosition);
            googleMap.animateCamera(cameraUpdate);

            // ic_add spot on the map
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(location.getName())
                    .snippet(snippet));

            googleMap.setInfoWindowAdapter(new HomeMapInfoWindowAdapter(mContext, location));
        }

    }

    // New Permission see Appendix A
    private void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }

    //Info Window 切到景點內容
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(mContext, LandMarkInfo.class);
        intent.putExtra("landMarkName",marker.getTitle());
        startActivity(intent);
//        String text = marker.getTitle();
//        Toast.makeText(mContext, text,
//                Toast.LENGTH_SHORT).show();
    }

    public class HomeMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private static final String TAG = "TAG_HomeMapInfo";
        private View infoWindow;
        private LandMark location;
        private int imageSize;
        private ImageTask landMarkImageTask;
        private CommonTask imageIdTask;
        private int imageId;

        HomeMapInfoWindowAdapter(Context context, LandMark location) {
            infoWindow = View.inflate(context, R.layout.home_map_info_window, null);
            this.location = location;
            imageSize = context.getResources().getDisplayMetrics().widthPixels / 3;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            //用title＝landMark name 比對找出landMarkID 就可以知道imageID
            if (Common.networkConnected(getActivity())) {
                String url = Common.URL + "/LocationServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findLocationId");
                jsonObject.addProperty("name", marker.getTitle());
                String jsonOut = jsonObject.toString();
                imageIdTask = new CommonTask(url, jsonOut);
                try {
                    String result = imageIdTask.execute().get();
                    imageId = Integer.valueOf(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                Common.showToast(getActivity(), R.string.msg_NoNetwork);
            }

            //秀出圖片,店名,地址
            ImageView imageView = infoWindow.findViewById(R.id.ivWindowPicture);
            String url = Common.URL + "/LocationServlet";
            int id = imageId;

            Bitmap bitmap = null;
            try {
                landMarkImageTask = new ImageTask(url, id, imageSize);
                // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
                bitmap = landMarkImageTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.default_image);
            }
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


    @Override
    public void onStop() {
        super.onStop();
        if (retrieveLocationTask != null) {
            retrieveLocationTask.cancel(true);
            retrieveLocationTask = null;
        }
    }

//    private LocationManager lms;
//
//    private void locationServiceInitial() {
//        lms = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);    //取得系統定位服務
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);    //使用GPS定位座標
//        getLocation(location);
//    }
//    private void getLocation(Location location) {	//將定位資訊顯示在畫面中
//        if(location != null) {
//            TextView longitude_txt =  view.findViewById(R.id.longitude);
//            TextView latitude_txt = view.findViewById(R.id.latitude);
//
//            Double longitude = location.getLongitude();	//取得經度
//            Double latitude = location.getLatitude();	//取得緯度
//
//            longitude_txt.setText(String.valueOf(longitude));
//            latitude_txt.setText(String.valueOf(latitude));
//        }
//        else {
////            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
//        }
//    }


}
