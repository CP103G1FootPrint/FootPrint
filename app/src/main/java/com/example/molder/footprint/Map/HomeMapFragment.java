package com.example.molder.footprint.Map;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Geocoder;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.Login.HeadImageTask;
import com.example.molder.footprint.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    //lists for permissions
    private ArrayList<String> permissionToRequest;
    private ArrayList<String> permissionRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    //integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private final static String TAG = "HomeMapFragment";
    private static final int REQ_PERMISSIONS = 0;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private CommonTask retrieveLocationTask;
    private Spinner spinner;
    private View view;
    private Button btnMySelf;
    private LatLng initLocation,updateLocation;
    private Boolean checkInitLocation = false;
    private HeadImageTask headImageTask;
    private Bitmap bitMapHeadImage = null;
    private Bitmap bitHeadImage = null;

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

        //we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionToRequest = permissionToRequest(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(permissionToRequest.size() > 0) {
                requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]),ALL_PERMISSIONS_RESULT);
            }
        }

        //we build google api client
        googleApiClient = new GoogleApiClient.Builder(mContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        setMap();
        btnMySelf = view.findViewById(R.id.btnMySelf);
        btnMySelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInitLocation) {
                    cameraPosition(updateLocation);
                }
            }
        });

        //使用者頭像
        SharedPreferences preferences = mContext.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        int imageSize = getResources().getDisplayMetrics().widthPixels / 6;
        try {
            String url = Common.URL + "/AccountServlet";
            headImageTask = new HeadImageTask(url, userId, imageSize);
            bitMapHeadImage = headImageTask.execute().get();
            bitHeadImage = getRoundedCornerBitmap(bitMapHeadImage);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return view;
    }

    private ArrayList<String> permissionToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String perm:wantedPermissions){
            if (!hasPermission(perm)){
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        askPermissions();
        if(googleApiClient != null){
            googleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            Toast.makeText(mContext, R.string.install, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates
        if(googleApiClient != null && googleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);

        if(resultCode != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(getActivity(),resultCode,PLAY_SERVICES_RESOLUTION_REQUEST);
            }else {
//                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setMap();
        googleMap.setOnInfoWindowClickListener(this);
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
//            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private void setMap(){
        spinner = view.findViewById(R.id.btnMapState);
        //下拉選單
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
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
                        showInit();
                    }
                } else {
                    Toast.makeText(mContext, R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
                }
            }
            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(mContext, R.string.noAnything, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMap(List<LandMark> locations) {
        for (int index = 0; index < locations.size(); index++) {
            LandMark location = locations.get(index);

            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            String snippet = getString(R.string.col_Name) + ": " + location.getName() + "\n" +
                    getString(R.string.col_Type) + ": " + location.getType() + "\n" +
                    getString(R.string.col_Address) + ": " + location.getAddress();

            // ic_add spot on the map
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(location.getName())
                    .snippet(snippet));

            googleMap.setInfoWindowAdapter(new HomeMapInfoWindowAdapter(mContext, location));
        }

    }

    //自己位置
    private void showInit(){
        if(location == null) {
            initLocation = new LatLng(24.967781, 121.191881);
            googleMap.addMarker(new MarkerOptions()
                    .position(initLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitHeadImage)));
            // focus on the spot
            cameraPosition(initLocation);
        }else {
            updateLocation = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(updateLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitHeadImage)));
            checkInitLocation = true;
        }
    }

    //移動到圖標中心
    private void cameraPosition(LatLng location){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);
    }

    //生成圆角图片
    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            //圓角
            final float roundPx = bitmap.getWidth()/2;
            final float roundPy = bitmap.getHeight()/2;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPy, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
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
            intent.putExtra("landMarkName", marker.getTitle());
            startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        //Permission ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        startLocationUpdates();
    }

    private void startLocationUpdates(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if(ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(mContext,R.string.msg_permission,Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ALL_PERMISSIONS_RESULT:
                for (String perm: permissionToRequest){
                    if(!hasPermission(perm)){
                        permissionRejected.add(perm);
                    }
                }

//                if(permissionRejected.size() >= 0){
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                        if(shouldShowRequestPermissionRationale(permissionRejected.get(0))){
//                            new AlertDialog.Builder(mContext)
//                                    .setMessage(R.string.needAllowPermission)
//                                    .setPositiveButton(R.string.msg_ok, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                                                requestPermissions(permissionRejected.toArray(new String[permissionRejected.size()]),ALL_PERMISSIONS_RESULT);
//                                            }
//                                        }
//                                    })
//                                    .setNegativeButton(R.string.msg_cancel,null).create().show();
//                            return;
//                        }
//                    }
//                }else {
//                    if(googleApiClient != null){
//                        googleApiClient.connect();
//                    }
//                }

                break;
        }
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
        if (headImageTask != null) {
            headImageTask.cancel(true);
            headImageTask = null;
        }
    }

}
