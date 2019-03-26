package com.example.molder.footprint.Personal;

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
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Home;
import com.example.molder.footprint.HomeNews.HeadImageTask;
import com.example.molder.footprint.Login.Account;
import com.example.molder.footprint.Login.MainLoginIn;
import com.example.molder.footprint.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class PersonalSetting extends AppCompatActivity {

    private ImageButton back;
    private Button save;
    private ImageButton selfie;
    private EditText nameEdit;
    private EditText birthEdit;
    private EditText starEdit;
    private EditText passwordEdit;
    private Button logout;
    private CommonTask userIdTask;
    private HeadImageTask headImageTask;
    private static final String TAG = "PersonalSetting";
    private String userId;
    private int imageSize;
    private Account account;

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

        // find Self Info
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/AccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findSelfInfo");
            jsonObject.addProperty("id", userId);
            userIdTask = new CommonTask(url, jsonObject.toString());
            try {
                String jsonIn = userIdTask.execute().get();
                Type listType = new TypeToken<Account>() {
                }.getType();
                //解析 json to gson
                account = new Gson().fromJson(jsonIn, listType);

                nameEdit.setText(account.getNickname());
                birthEdit.setText(account.getBirthday());
                starEdit.setText(account.getConstellation());
                passwordEdit.setText(account.getPassword());

            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
        }

        //使用者頭像
        String url = Common.URL + "/PicturesServlet";
        headImageTask = new HeadImageTask(url, userId, imageSize, selfie);
        headImageTask.execute();

        //設定大頭照
        selfie.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalSetting.this, choosePic.class);
                startActivity(intent);
            }
        });

        //返回上一頁
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(PersonalSetting.this, PersonalHome.class);
//                startActivity(intent);
//                Fragment fragment = new PersonalHome();
//                Fragment f = new Fragment();
//                  loadFragment(fragment);
                finish();
            }
        });

        //登出
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                preferences.edit().putBoolean("login", false)
                        .putString("userId", null)
                        .putString("password", null).apply();
                finish();
                Intent intent = new Intent(PersonalSetting.this, MainLoginIn.class);
                startActivity(intent);
            }
        });

    }

//    private void loadFragment(Fragment fragment) {
//
//// create a FragmentManager
//        FragmentManager fm = getSupportFragmentManager();
//// create a FragmentTransaction to begin the transaction and replace the Fragment
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//// replace the FrameLayout with new Fragment
//        fragmentTransaction.replace(R.id.content, fragment);
//        fragmentTransaction.commit(); // save the changes
//    }


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
//                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
//                    Common.showToast(this, R.string.msg_InsertFail);
                } else {
//                    Common.showToast(this, R.string.msg_InsertSuccess);
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

    @Override
    public void onStop() {
        super.onStop();
        if (userIdTask != null) {
            userIdTask.cancel(true);
            userIdTask = null;
        }
        if (headImageTask != null) {
            headImageTask.cancel(true);
            headImageTask = null;
        }
    }

}
