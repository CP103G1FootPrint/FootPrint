package com.example.molder.footprint.Schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_create);
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

        shBtAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(ScheduleCreateActivity.this);
                final View a = inflater.inflate(R.layout.schedule_friendlist, null);


                new AlertDialog.Builder(ScheduleCreateActivity.this)

                        .setTitle(R.string.textAddtoGroup)

                        .setMultiChoiceItems(
                                new String[]{"May", "Jack", "Sunny","Vivian","Tom"},
                                new boolean[]{false,true,true,false,false},
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        Toast.makeText(getApplicationContext(), "which " + which + ", isChecked " + isChecked, Toast.LENGTH_SHORT).show();
                                    }
                                })

                        .setView(a)
                        .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), R.string.textSuccess, Toast.LENGTH_SHORT).show();
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
        shBtPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_IMAGE);
            }
        });


    }

    public void onSaveTripClick(View view) {
        String title = shEtTripName.getText().toString().trim();
        if (title.length() <= 0) {
            Common.showToast(ScheduleCreateActivity.this, R.string.msg_NameIsInvalid);
            return;
        }
        String date = shTvDatePicker.getText().toString();
        if (image == null) {
            Common.showToast(ScheduleCreateActivity.this, R.string.msg_NoImage);
            return;
        }
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/TripServlet";
            Trip trip = new Trip(0, title, date);
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

//        finish();
    }

    public void onCancelCreateTripClick (View view){
        finish();

    }

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
                                activity.getContentResolver().openInputStream(croppedImageUri));
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
            Common.showToast(ScheduleCreateActivity.this, "This device doesn't support the crop action!");
        }
    }

}