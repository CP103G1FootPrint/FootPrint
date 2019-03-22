package com.example.molder.footprint.Schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;

import com.example.molder.footprint.Friends.FriendsFriendFragment;
import com.example.molder.footprint.HomeNews.HomeNewsActivity_Personal_Friendship_Friends;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleCreateActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener{
    private final static String TAG = "ScheduleCreateActivity";
    private EditText shEtTripName, shEtDay;
    private ImageButton shBtAddFriend;
    private Button shBtCancel, shBtSave, shBtPickPicture;
    private TextView shTvGroupM, shTvDatePicker;
    private int year, month, day;
    private FragmentActivity activity;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private byte[] image;
    private ImageView shImgPhoto ;
    private Uri contentUri,croppedImageUri;
    private ListView listView ;
    private String textFriend ,createID ;
    private int intFriendid ;
    private String itemFriends = null ;

    private String[] list_items ;
    private boolean[] checked_items ;
    private ArrayList<Integer> items_selected = new ArrayList<>();
    private CommonTask friendsCommonTask;
    private CommonTask tripIDCommonTask;
    private int tripID;
    private List<TripPlanFriend> tripPlanFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_create);
        findTripId();
        handleViews();
        showNow();
    }

    private void showNow() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        updateDisplay();
    }

    private void updateDisplay() {
        shTvDatePicker.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));

    }

    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        }
        else {
            return "0" + String.valueOf(number);
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int  year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateDisplay();

    }

    public static class DatePickerDialogFragment extends DialogFragment {
        /* 覆寫onCreateDialog()以提供想要顯示的日期挑選器 */
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            /* 呼叫getActivity()會取得此DialogFragment所依附的MainActivity物件 */
            ScheduleCreateActivity activity = (ScheduleCreateActivity) getActivity();
            /* DatePickerDialog建構式第2個參數為OnDateSetListener物件。
               因為MainActivity有實作OnDateSetListener的onDateSet方法，
               所以MainActivity物件亦為OnDateSetListener物件。
               year、month、day會成為日期挑選器預選的年月日 */
            //DatePickerDialong.getDatePicker()->DatePicker
            //DatePick.setMaxDate()/setMinDate()
            //設定最大最小可選取日期
            return new DatePickerDialog(
                    activity, activity,
                    activity.year, activity.month, activity.day);
        }
    }

    public void onDateClick(View view) {
        DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        datePickerFragment.show(fm, "datePicker");
    }


    private void handleViews(){
        shEtTripName = findViewById(R.id.shEtTripName);
        shTvDatePicker = findViewById(R.id.shTvDatePicker);
        shEtDay = findViewById(R.id.shEtDay);
        shBtAddFriend = findViewById(R.id.shBtAddFriend);
        shBtCancel = findViewById(R.id.shBtCancel);
        shBtSave = findViewById(R.id.shBtSave);
        shTvGroupM = findViewById(R.id.shTvGroupM);
        shBtPickPicture = findViewById(R.id.shBtPickPicture);
        shImgPhoto = findViewById(R.id.shImgPhoto);
        getFriendsFriendFragment_Friend();
        checked_items = new boolean[list_items.length];

        shBtAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(ScheduleCreateActivity.this);
                myBuilder.setTitle(R.string.textAddtoGroup);
                myBuilder.setMultiChoiceItems(list_items, checked_items, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (!items_selected.contains(which)){
                            items_selected.add(which);
                        }else {
                            items_selected.remove(which);
                        }
                    }
                }) ;
                myBuilder.setCancelable(false);
                myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i=0;i<items_selected.size();i++){
                            itemFriends = itemFriends + list_items[items_selected.get(i)];
                            if (i!= items_selected.size()-1){
                                itemFriends = itemFriends+ ",";
                            }
                        }
                        shTvGroupM.setText(itemFriends);
                    }
                });
                myBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = myBuilder.create() ;
                dialog.show();







//                LayoutInflater inflater = LayoutInflater.from(ScheduleCreateActivity.this);
//                final View a = inflater.inflate(R.layout.schedule_friendlist, null);
//
//                listView = a.findViewById(R.id.lvCheckFriend);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        for(int i =0 ; i < listView.getChildCount();i++){
//                            if( position == i){
//                                listView.getChildAt(i).setBackgroundColor(Color.GREEN);
//                                FriendsFriendFragment_Friend member = (FriendsFriendFragment_Friend) parent.getItemAtPosition(position);
//                                textFriend = member.getFriends_TvFriendsName();
//                                intFriendid = member.getFriends_CvProfilePicId();
//                                shTvGroupM.setText(textFriend);
//
//                            }else {
//                                listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
//                            }
//                        }
//                    }
//                });


//                new AlertDialog.Builder(ScheduleCreateActivity.this)
//
//                        .setTitle(R.string.textAddtoGroup)
//
//                        .setMultiChoiceItems(
//                                new String[]{"May", "Jack", "Sunny","Vivian","Tom"},
//                                new boolean[]{false,true,true,false,false},
//                                new DialogInterface.OnMultiChoiceClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
////                                        Toast.makeText(getApplicationContext(), "which " + which + ", isChecked " + isChecked, Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//
//                        .setView(a)
//                        .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(getApplicationContext(), R.string.textSuccess, Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .setNegativeButton(R.string.textCancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        })
//                        .show();


            }
        });




        shBtPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_IMAGE);
            }
        });


    }





    // 新增行程列表
    public void onSaveTripClick(View view) {
        String title = shEtTripName.getText().toString().trim();
        if (title.length() <= 0) {
            Common.showToast(ScheduleCreateActivity.this, R.string.msg_NameIsInvalid);
            return;
        }
        String date = shTvDatePicker.getText().toString();

        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String createID = preferences.getString("userId", "");

        String stringDays = shEtDay.getText().toString().trim() ;
        int days = Integer.valueOf(stringDays);

        String type ;
                if (itemFriends == null){
                    type = "Personal" ;

                }else {
                    type= "Group";
                }

        if (image == null) {
            Common.showToast(ScheduleCreateActivity.this, R.string.msg_NoImage);
            return;
        }

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/TripServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "tripPlanFriendInsert");
            jsonObject.addProperty("tripPlanFriends", new Gson().toJson(tripPlanFriends));
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Common.showToast(ScheduleCreateActivity.this, R.string.msg_InsertFail);
            } else {
                Common.showToast(ScheduleCreateActivity.this, R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(ScheduleCreateActivity.this, R.string.msg_NoNetwork);
        }



        if (Common.networkConnected(this)) {
            String url = Common.URL + "/TripServlet";
            Trip trip = new Trip(title,date,type,createID,days,tripID + 1);
            String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "tripInsert");
            jsonObject.addProperty("trip", new Gson().toJson(trip));
            jsonObject.addProperty("imageBase64", imageBase64);
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Common.showToast(ScheduleCreateActivity.this, R.string.msg_InsertFail);
            } else {
                Common.showToast(ScheduleCreateActivity.this, R.string.msg_InsertSuccess);
            }
        } else {
            Common.showToast(ScheduleCreateActivity.this, R.string.msg_NoNetwork);
        }
        ScheduleCreateActivity.this.finish();


    }


    public void onCancelCreateTripClick (View view){
        finish();

    }


    //挑選行程列表封面照片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_PICK_IMAGE:
                    Uri uri = intent.getData();
                    crop(uri);
                    break;
                case REQ_CROP_PICTURE:
                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {
                        Bitmap picture = BitmapFactory.decodeStream(

                                this.getContentResolver().openInputStream(croppedImageUri));
                        shImgPhoto.setImageBitmap(picture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.toString());
                    }


            }
        }
    }

    //裁切照片
    private void crop(Uri sourceImageUri) {
        File file = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            Common.showToast(ScheduleCreateActivity.this, "This device doesn't support the crop action!");
        }
    }

    private void getFriendsFriendFragment_Friend() {
        //目前登入的使用者
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String userId = preferences.getString("userId", "");

        //抓取所有與使用者關係為好友的資料
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/FriendsServlet";
            List<HomeNewsActivity_Personal_Friendship_Friends> friendship_Friends = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllFriends");
            jsonObject.addProperty("userId", userId);
            String jsonOut = jsonObject.toString();
            friendsCommonTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = friendsCommonTask.execute().get();
                Type listType = new TypeToken<List<HomeNewsActivity_Personal_Friendship_Friends>>() {
                }.getType();
                friendship_Friends = new Gson().fromJson(jsonIn, listType);
                int count = friendship_Friends.size();
                list_items = new String[count];
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (friendship_Friends == null || friendship_Friends.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoNewsFound);
            } else {
                for (int position = 0;position < friendship_Friends.size();position++){
                    HomeNewsActivity_Personal_Friendship_Friends friendship = friendship_Friends.get(position);
                    String friendsId = friendship.getInvitee();
                    if(friendsId.equals(userId)){
                        friendsId = friendship.getInviter();
                    }
                    list_items[position] = friendsId;
                    TripPlanFriend tripPlanFriend = new TripPlanFriend(userId,friendsId,tripID+1);
                    tripPlanFriends.add(tripPlanFriend);
                }
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

    private void findTripId(){
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/TripServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findTripId");
            String jsonOut = jsonObject.toString();
            tripIDCommonTask = new CommonTask(url, jsonOut);
            try {
                String result = tripIDCommonTask.execute().get();
                tripID = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

}
