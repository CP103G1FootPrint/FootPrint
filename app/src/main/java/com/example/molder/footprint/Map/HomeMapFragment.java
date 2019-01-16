package com.example.molder.footprint.Map;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements OnMapReadyCallback {

    private final static String TAG = "GeocoderActivity";

    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private CommonTask retrieveCategoryTask, retrieveLocationTask;


    public HomeMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_map, container, false);

        geocoder = new Geocoder(mContext);
        FragmentManager fm = getChildFragmentManager();/// getChildFragmentManager()//getActivity().getSupportFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.googleMap);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.googleMap, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (Common.networkConnected((Activity) mContext)) {
            String url = Common.URL + "/LocationServlet";
            List<LandMark> locations = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
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
//                showToast(this, R.string.text_NoBooksFound);
            } else {
                showMap(locations);
            }
        } else {
//            showToast(this, R.string.text_NoNetwork);
        }

        //暫時替代
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
        for(int index =0; index < locations.size(); index++){
            LandMark location = locations.get(index);

            LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
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


    // 吐司顯示
    private void showToast(int messageResId) {
        Toast.makeText(mContext, messageResId, Toast.LENGTH_SHORT).show();
    }



}
