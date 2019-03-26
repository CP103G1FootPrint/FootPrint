package com.example.molder.footprint.CheckInShare;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Home;
import com.example.molder.footprint.HomeFragment;
import com.example.molder.footprint.Map.LandMark;
import com.example.molder.footprint.R;
import com.facebook.CallbackManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckInShareFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private final static String TAG = "CheckInShareInsert";
    private FragmentActivity activity;
    private FragmentManager fragmentManager;
    private ImageView ivCheckInShare;
    private Button  btFinishInsert, btCancel, btChooseLandMark,btnCheckInShareStateChooseLandMarkSelf;
    private ImageButton btTakePicture, btPickPicture;
    private Spinner spSate;
    private TextView tvShowLandMark;
    private EditText etDescription;
    private byte[] image;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    public static final int REQ_EXTERNAL_STORAGE = 3;
    private static final int REQ_Location = 4;
    private Uri contentUri, croppedImageUri;
    private View v, rootView;
    private ListView listView;
    private CommonTask retrieveLocationTask;
    private String textLandMark;
    private int intLandMarkID;
    private String mCurrentPhotoPath;
    private File photoFile;

    //GPS
    public static Location locationGPS;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    //lists for permissions
    private ArrayList<String> permissionToRequest;
//    private ArrayList<String> permissionRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    //integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    public CheckInShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        fragmentManager = getFragmentManager();

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        File images = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = "路徑" + images.getAbsolutePath();
        return images;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(photoFile));
        activity.sendBroadcast(mediaScanIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.check_in_share_insert, container, false);


        //GPS
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
        googleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        //////////////////
        findViews(rootView);
        //拍照片
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    if (isIntentAvailable(activity, intent)) {
                        contentUri = FileProvider.getUriForFile(
                                activity, activity.getPackageName() + ".provider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        startActivityForResult(intent, REQ_TAKE_PICTURE);

                    } else {
                        Common.showToast(getActivity(), R.string.checkInShareNoCameraApp);
                    }
                }
            }
        });

        //選照片
        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_IMAGE);
            }
        });

        //上傳
        btFinishInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object item = spSate.getSelectedItem();
                String category = item.toString().trim();

                if (image == null) {
                    Common.showToast(getActivity(), R.string.msg_NoImage);
                    return;
                }

                if (tvShowLandMark == null) {
                    Common.showToast(getActivity(), R.string.msg_NoLandMark);
                    return;
                }

                String description = etDescription.getText().toString().trim();

                SharedPreferences preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                String userId = preferences.getString("userId", "");

                if (Common.networkConnected(activity)){
                    String url = Common.URL + "/PictureServlet";
                    Picture picture = new Picture(0, description, category, userId, intLandMarkID);
                    String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "shareInsert");
                    jsonObject.addProperty("share", new Gson().toJson(picture));
                    jsonObject.addProperty("imageBase64", imageBase64);
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);

                        Fragment fragment = new HomeFragment();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content, fragment);
                        fragmentTransaction.commit();

//                        Intent intent = new Intent(getActivity(), Home.class);
//                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), R.string.msg_InsertFail);
                    } else {
                        Common.showToast(getActivity(), R.string.msg_InsertSuccess);
                    }
                } else {
                    Common.showToast(getActivity(), R.string.msg_NoNetwork);
                }
            }
        });

        //取消
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.commit();

            }
        });

        //選地標
        btChooseLandMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清除重複choose Land Mark AlertDialog
                if (v.getParent() != null) {
                    ((ViewGroup) v.getParent()).removeView(v); // <- fix
                }
                //新建choose Land Mark AlertDialog
                new AlertDialog.Builder(activity)
                        .setView(v)
                        .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.textCancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        //創地標
        btnCheckInShareStateChooseLandMarkSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateLandMark.class);
                intent.putExtra("textLandMark",textLandMark);
                startActivityForResult(intent,REQ_Location);
            }
        });

        return rootView;
    }

    private void findViews(View rootView) {
        ivCheckInShare = rootView.findViewById(R.id.ivCheckInShare);
        btTakePicture = rootView.findViewById(R.id.btCheckInShareTakePicture);
        btPickPicture = rootView.findViewById(R.id.btCheckInSharePickPicture);
        btFinishInsert = rootView.findViewById(R.id.btCheckInShareFinishInsert);
        btCancel = rootView.findViewById(R.id.btCheckInShareCancel);
        spSate = rootView.findViewById(R.id.btnCheckInShareState);
        tvShowLandMark = rootView.findViewById(R.id.tvShowLandMark);
        etDescription = rootView.findViewById(R.id.etCheckInShareDescription);
        btChooseLandMark = rootView.findViewById(R.id.btnCheckInShareStateChooseLandMark);
        btnCheckInShareStateChooseLandMarkSelf = rootView.findViewById(R.id.btnCheckInShareStateChooseLandMarkSelf);

        LayoutInflater inflater = LayoutInflater.from(activity);
        v = inflater.inflate(R.layout.check_in_share_choose_land_mark, null);
        listView = v.findViewById(R.id.lvCheckInShareChooseLandMark);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (position == i) {
                        listView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorToolbar));
                        LandMark member = (LandMark) parent.getItemAtPosition(position);
                        textLandMark = member.getName();
                        intLandMarkID = member.getId();
                        tvShowLandMark.setText(textLandMark);
                    } else {
                        listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }

            }
        });


    }

    private boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    galleryAddPic();
                    break;
                case REQ_PICK_IMAGE:
                    Uri uri = intent.getData();
                    crop(uri);
                    break;
                case REQ_CROP_PICTURE:
//                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {
                        Bitmap picture = BitmapFactory.decodeStream(
                                activity.getContentResolver().openInputStream(croppedImageUri));
                        ivCheckInShare.setImageBitmap(picture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                    } catch (FileNotFoundException e) {
//                        Log.e(TAG, e.toString());
                    }
                    break;
                case REQ_Location:
                    tvShowLandMark.setText("");
                    String result = intent.getStringExtra("textLandMark");
                    intLandMarkID = intent.getIntExtra("landMarkID",0);
                    tvShowLandMark.setText(result);
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        croppedImageUri = Uri.fromFile(file);
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // the recipient of this Intent can read soruceImageUri's data
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // set image source Uri and type
            cropIntent.setDataAndType(sourceImageUri, "image/*");
            // send crop message
            cropIntent.putExtra("crop", "true");
            // aspect ratio of the cropped area, 0 means user define
            cropIntent.putExtra("aspectX", 0); // this sets the max width
            cropIntent.putExtra("aspectY", 0); // this sets the max height
            // output with and height, 0 keeps original size
            cropIntent.putExtra("outputX", 0);
            cropIntent.putExtra("outputY", 0);
            // whether keep original aspect ratio
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            // whether return data by the intent
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQ_CROP_PICTURE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Common.showToast(getActivity(), "This device doesn't support the crop action!");
        }
    }

    //listView
    public void showResult(List<LandMark> locations) {
        listView.setAdapter(new LandMarkAdapter(activity, locations));

    }

    //////////////////////////
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
            return checkSelfPermission(getActivity(),permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

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

    private void startLocationUpdates(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getActivity(),R.string.msg_permission,Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, (com.google.android.gms.location.LocationListener) this);

        if(locationGPS != null){
            if (Common.networkConnected(activity)) {
                String url = Common.URL + "/LocationServlet";
                List<LandMark> locations = null;
                Double latitude = locationGPS.getLatitude();
                Double longitude = locationGPS.getLongitude();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "nearByMySelf");
                jsonObject.addProperty("latitude", latitude);
                jsonObject.addProperty("longitude", longitude);
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
                    Common.showToast(activity, R.string.msg_NoFoundLandMark);
                } else {
                    showResult(locations);
                }
            } else {
                Common.showToast(activity, R.string.msg_NoNetwork);
            }
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        //Permission ok, we get last location
        locationGPS = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startLocationUpdates();
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
    /////////////////////////////


    private class LandMarkAdapter extends BaseAdapter {
        Context context;
        List<LandMark> memberList;

        LandMarkAdapter(Context context, List<LandMark> memberList) {
            this.context = context;
            this.memberList = memberList;
        }

        @Override
        public int getCount() {
            return memberList.size();
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.check_in_share_choose_land_mark_item, parent, false);
            }

            LandMark member = memberList.get(position);
            TextView tvName = itemView
                    .findViewById(R.id.tvCheckInShareChooseLandMarkId);
            tvName.setText(member.getName());
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return memberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return memberList.get(position).getId();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
//        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        askPermissions(activity, permissions, REQ_EXTERNAL_STORAGE);
        if(googleApiClient != null){
            googleApiClient.connect();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
//            Toast.makeText(getActivity(), R.string.install, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStop() {
        super.onStop();
        if (retrieveLocationTask != null) {
            retrieveLocationTask.cancel(true);
            retrieveLocationTask = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btPickPicture.setEnabled(true);
                    btTakePicture.setEnabled(true);
                } else {
                    btPickPicture.setEnabled(false);
                    btTakePicture.setEnabled(false);
                }
                break;
        }
    }

    // New Permission see Appendix A
    public static void askPermissions(Activity activity, String[] permissions, int requestCode) {
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    requestCode);
        }
    }


}
