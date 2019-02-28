package com.example.molder.footprint.Schedule;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Common.ImageTask;
import com.example.molder.footprint.Home;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleAlbumFragment extends Fragment {
    private static final String TAG = "ScheduleAlbumFragment";
    private RecyclerView rvAlbum ;
    private FragmentActivity activity;
    private CommonTask albumGetAllTask,albumDeleteTask;

    private ImageTask albumImageTask;
    private static final int REQ_PICK_IMAGE = 0 ;
    private SwipeRefreshLayout shAlbumSwipeRefreshLayout ;
    private static final int REQ_CROP_PICTURE = 2;
    private static final int IMAGE_CODE = 1;

    private Uri contentUri,croppedImageUri;
    private  ImageView shAlbumImg ;
    private int tripId ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_album,container,false);
        shAlbumSwipeRefreshLayout = view.findViewById(R.id.shAlbumSwipeRefreshLayout);
        shAlbumSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shAlbumSwipeRefreshLayout.setRefreshing(true);
                showAllAlbum();
                shAlbumSwipeRefreshLayout.setRefreshing(false);
            }
        });


        handleViews(view);
        return view;
    }

    private boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }





    private void showAllAlbum(){
        //打開上一頁的打包的資料
        Bundle bundle = getArguments();
        if (bundle != null) {
            Trip trip = (Trip) bundle.getSerializable("trip");
            if (trip != null) {

                tripId = trip.getTripID();
                rvAlbum.setLayoutManager(
                        new StaggeredGridLayoutManager(3,
                                StaggeredGridLayoutManager.VERTICAL));
                rvAlbum.setAdapter(new GroupAdapter(activity, tripId));
            }
        }


//        if (Common.networkConnected(activity)) {
//            String url = Common.URL + "/GroupAlbumServlet";
//            List<GroupAlbum> groupAlbums = null;
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getImage");
//            jsonObject.addProperty("tripID",tripId);
//            String jsonOut = jsonObject.toString();
//            albumGetAllTask = new CommonTask(url, jsonOut);
//            try {
//                String jsonIn = albumGetAllTask.execute().get();
//                Type listType = new TypeToken<List<GroupAlbum>>() {
//                }.getType();
//                groupAlbums = new Gson().fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            if (groupAlbums == null || groupAlbums.isEmpty()) {
//                Common.showToast(activity, R.string.msg_NoImage);
//            } else {
//
//            }
//        } else {
//            Common.showToast(activity, R.string.msg_NoNetwork);
//        }
    }
    @Override
    public void onStart() {
        super.onStart();
        showAllAlbum(); //重刷抓資料
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private int groupAlbumsid;
        private int imageSize;

        public GroupAdapter(Context context, int groupAlbumsid) {
            layoutInflater = LayoutInflater.from(context);
            this.groupAlbumsid = groupAlbumsid;

            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.shAlbumImg);

            }
        }
        @Override
        public int getItemCount() {
            return groupAlbumsid;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.schedule_album_item, parent, false);
            return new ScheduleAlbumFragment.GroupAdapter.MyViewHolder(itemView);

        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            final int albumId = groupAlbumsid;

            String url = Common.URL + "/GroupAlbumServlet"; //圖還未載入
//            int albumId = groupAlbum.getAlbumID();
            albumImageTask = new ImageTask(url, albumId, imageSize, myViewHolder.imageView);
            albumImageTask.execute();
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
    private void handleViews(View view){
        rvAlbum = view.findViewById(R.id.shRecyclerViewAlbum);
        rvAlbum.setLayoutManager(new LinearLayoutManager(activity));
//        rvAlbum.setAdapter(new GroupAlbumAdapter(activity,getAlbums()));
        FloatingActionButton shBtGroupAlbumAdd = view.findViewById(R.id.shBtGroupAlbumAdd);
        shBtGroupAlbumAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, REQ_PICK_IMAGE);
//                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                galleryIntent.setType("image/*");//圖片
//                startActivityForResult(galleryIntent, IMAGE_CODE);

            }
        });
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQ_PICK_IMAGE:

                    Uri uri = intent.getData(); //資源路徑Uri
//                    Uri selectedImage = intent.getData();

                    if (uri != null) {
                        //獲取圖片的路徑
                        String[] columns = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().getContentResolver().query(uri, columns,//cursor指標
                                null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String imagePath = cursor.getString(0);
                            cursor.close();


                            Intent intentss = new Intent(getActivity(), ScheduleAlbumInsertActivity.class);
                            intentss.putExtra("imagePath", imagePath);
                            startActivity(intentss);
                        }


//                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                        cursor.moveToFirst();
//                        String path= cursor.getString(columnIndex);
//
//                        BitmapFactory.Options options = new BitmapFactory.Options(); options.inPreferredConfig = Bitmap.Config.RGB_565;
//                        Bitmap newBitMapPhoto = BitmapFactory.decodeFile(path,options);


                    }
            }

            }
        }






    @Override
    public void onStop() {
        super.onStop();
        if (albumGetAllTask != null) {
            albumGetAllTask.cancel(true);
            albumGetAllTask = null;
        }

        if (albumImageTask != null) {
            albumImageTask.cancel(true);
            albumImageTask = null;
        }

        if (albumDeleteTask != null) {
            albumDeleteTask.cancel(true);
            albumDeleteTask = null;
        }
    }


//    private void crop(Uri sourceImageUri) {
//        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        file = new File(file, "picture_cropped.jpg");
//        croppedImageUri = Uri.fromFile(file);
//        // take care of exceptions
//        try {
//            // call the standard crop action intent (the user device may not support it)
//            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//            // the recipient of this Intent can read soruceImageUri's data
//            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            // set image source Uri and type
//            cropIntent.setDataAndType(sourceImageUri, "image/*");
//            // send crop message
//            cropIntent.putExtra("crop", "true");
//            // aspect ratio of the cropped area, 0 means user define
//            cropIntent.putExtra("aspectX", 0); // this sets the max width
//            cropIntent.putExtra("aspectY", 0); // this sets the max height
//            // output with and height, 0 keeps original size
//            cropIntent.putExtra("outputX", 0);
//            cropIntent.putExtra("outputY", 0);
//            // whether keep original aspect ratio
//            cropIntent.putExtra("scale", true);
//            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
//            // whether return data by the intent
//            cropIntent.putExtra("return-data", true);
//            // start the activity - we handle returning in onActivityResult
//            startActivityForResult(cropIntent, REQ_CROP_PICTURE);
//        }
//        // respond to users whose devices do not support the crop action
//        catch (ActivityNotFoundException anfe) {
//            Common.showToast(activity, "This device doesn't support the crop action!");
//        }
//    }

}
