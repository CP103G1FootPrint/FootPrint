package com.example.molder.footprint.Schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.Login.Account;
import com.example.molder.footprint.Login.MainLoginIn;
import com.example.molder.footprint.PersonalHome;
import com.example.molder.footprint.R;
import com.example.molder.footprint.choosePic;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PersonalSetting extends AppCompatActivity {

    private ImageButton back;
    private Button save;
    private ImageButton selfie;
    private EditText nameEdit;
    private EditText birthEdit;
    private EditText starEdit;
    private EditText passwordEdit;
    private Button logout;
    private CommonTask newsCommonTask, userIdTask, landMarkIdTask;
    private HeadImageTask headImageTask;
    private static final String TAG = "PersonalSetting";
    private String userId;
    private int imageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_setting);
        handleView();
    }

    public void handleView() {
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        selfie = findViewById(R.id.selfie);
        nameEdit = findViewById(R.id.nameEdit);
        birthEdit = findViewById(R.id.birthEdit);
        starEdit = findViewById(R.id.starEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        logout = findViewById(R.id.logout);

        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        userId = preferences.getString("userId", ""); //抓userid

        imageSize = getResources().getDisplayMetrics().widthPixels;
        String url = Common.URL + "/PicturesServlet";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "findUserNickName");
        jsonObject.addProperty("id", userId);

        //將內容轉成json字串
        userIdTask = new CommonTask(url, jsonObject.toString());
        try {

            //使用者頭像

            url = Common.URL + "/PicturesServlet";
            headImageTask = new HeadImageTask(url, userId, imageSize, selfie);
            headImageTask.execute();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


        selfie.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {



                Intent intent = new Intent(PersonalSetting.this, choosePic.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(PersonalSetting.this, PersonalHome.class);
//                startActivity(intent);
                Fragment fragment = new PersonalHome();
                Fragment f = new Fragment();
                  loadFragment(fragment);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalSetting.this, MainLoginIn.class);
                startActivity(intent);
            }
        });

    }

    private void loadFragment(Fragment fragment) {

// create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit(); // save the changes
    }


    //檢查Password
    private boolean validatePassword() {
        String passwordInput = passwordEdit.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            passwordEdit.setError("未填");
            return false;
        } else {
            passwordEdit.setError(null);
            return true;
        }
    }

    //檢查Nick Name
    private boolean validateNickName() {
        String nickName = nameEdit.getText().toString().trim();

        if (nickName.isEmpty()) {
            nameEdit.setError("未填");
            return false;
        } else {
            nameEdit.setError(null);
            return true;
        }
    }

    //檢查birthday
    private boolean validateBirthday() {
        String birthday = birthEdit.getText().toString().trim();
        String pattern = "^(0?[1-9]|1[0-2])/(0?[1-9]|1\\d|2\\d|3[0-1])$";

        if (birthday.isEmpty()) {
            birthEdit.setError("未填");
            return false;
        } else if (!birthday.matches(pattern)) {
            birthEdit.setError("無效生日");
            return false;
        } else {
            //doSomething
            birthEdit.setError(null);
            return true;
        }
    }

    //檢查star
    private boolean validateStarEdit() {
        String starEdits = starEdit.getText().toString().trim();

        if (starEdits.isEmpty()) {
            starEdit.setError("未填");
            return false;
        } else {
            starEdit.setError(null);
            return true;
        }
    }

    //確認按鈕 當以上檢查都成立時會送出
    public void saveConfirm(View view) {
        if (validatePassword() && validateNickName() && validateBirthday() && validateStarEdit()) {

            String passwordInput = passwordEdit.getText().toString().trim();
            String nickName = nameEdit.getText().toString().trim();
            String birthday = birthEdit.getText().toString().trim();
            String starEdits = starEdit.getText().toString().trim();


            if (Common.networkConnected(this)) {
                String url = Common.URL + "/AccountServlet";
                Account accounts = new Account(passwordInput, nickName, birthday, starEdits,userId);

                JsonObject jsonObject = new JsonObject();
                //新增
                jsonObject.addProperty("action", "accountUpdate");
                //passwordInput, nickName, birthday, starEdits存在account丟給ＳＥＲＶＥＲ
                jsonObject.addProperty("account", new Gson().toJson(accounts));
                int count = 0;
                try {
                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                    count = Integer.valueOf(result);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
                    Common.showToast(this, R.string.msg_InsertFail);
                } else {
                    // user ID and password will be saved in the preferences file
                    // and starts UserActivity
                    // while the user account is created successfully
//                    SharedPreferences preferences = getSharedPreferences(
//                            Common.PREF_FILE, MODE_PRIVATE);
//                    preferences.edit().putBoolean("login", true)
//                            .putString("password", passwordInput).apply();
                    Common.showToast(this, R.string.msg_InsertSuccess);
                    finish();
                }
            } else {
                Common.showToast(this, R.string.msg_NoNetwork);
            }

        }

    }

    //取消
    public void cancelChange(View view) {
        finish();
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
