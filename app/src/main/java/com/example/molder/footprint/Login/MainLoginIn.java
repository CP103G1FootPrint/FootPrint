package com.example.molder.footprint.Login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Home;
import com.example.molder.footprint.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class MainLoginIn extends AppCompatActivity {

    private static final String TAG = "MainLoginIn";
    private static final int REQUEST_CODE = 1;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private LoginButton loginButton;
    private static final int REQ_PERMISSIONS = 0;
    private TextInputEditText tIETAccount,tIETPassword;
    private CommonTask userValidTask;
    private CheckBox mCheckBox;
    private boolean result =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);

        mCheckBox = findViewById(R.id.loginCbTerms);

        setResult(RESULT_CANCELED);
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //登入成功

            @Override
            public void onSuccess(LoginResult loginResult) {

                //accessToken之後或許還會用到 先存起來

                accessToken = loginResult.getAccessToken();

                Log.d("FB","access token got.");

                //send request and call graph api

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                //讀出姓名 ID FB個人頁面連結

                                Log.d("FB","complete");
                                Log.d("FB",object.optString("name"));
                                Log.d("FB",object.optString("link"));
                                Log.d("FB",object.optString("id"));

                            }
                        });

                //包入你想要得到的資料 送出request

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();

            }

            //登入取消

            @Override
            public void onCancel() {
                // App code

                Log.d("FB","CANCEL");
            }

            //登入失敗

            @Override
            public void onError(FacebookException exception) {
                // App code

                Log.d("FB",exception.toString());
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case REQUEST_CODE:
                result = data.getBooleanExtra("checkBox",false);
                if(result){
                    mCheckBox.setChecked(true);
                }
                break;
        }
    }



    //登入後進入 Home 頁面
    public void onLoginInClick(View view){
        LayoutInflater inflater = LayoutInflater.from(MainLoginIn.this);
        final View v = inflater.inflate(R.layout.fragment_login_in, null);
        tIETAccount = v.findViewById(R.id.inputAccount);
        tIETPassword = v.findViewById(R.id.inputPassword);
        new AlertDialog.Builder(MainLoginIn.this)
                .setTitle(R.string.textLoginIn)
                .setView(v)
                .setMessage(R.string.textLoginInMessage)
                .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String user = tIETAccount.getText().toString().trim();
                        String password = tIETPassword.getText().toString().trim();
                        if (user.length() <= 0 || password.length() <= 0) {
                            Toast.makeText(getApplicationContext(), R.string.textNotValid, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(result == false){
                            Toast.makeText(getApplicationContext(), R.string.textNotReadTerms, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isUserValid(user, password)) {
                            SharedPreferences preferences = getSharedPreferences(
                                    Common.PREF_FILE, MODE_PRIVATE);
                            preferences.edit().putBoolean("login", true)
                                    .putString("userId", user)
                                    .putString("password", password).apply();
                            setResult(RESULT_OK);
                            Intent intent = new Intent(MainLoginIn.this, Home.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.textNotValid, Toast.LENGTH_SHORT).show();
                        }

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

    //進入創帳號頁面
    public void onCreateNewAccountClick(View view){
        Intent intent = new Intent(MainLoginIn.this, CreateNewAccount.class);
        startActivity(intent);
    }

    //忘記密碼dialog視窗
    public void onForgotPasswordClick(View view){
        LayoutInflater inflater = LayoutInflater.from(MainLoginIn.this);
        final View v = inflater.inflate(R.layout.fragment_forgot_password, null);
        new AlertDialog.Builder(MainLoginIn.this)
                .setTitle(R.string.textForgotPassword)
                .setView(v)
                .setMessage(R.string.textForgotPasswordMessage)
                .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), R.string.textSendForgotPasswordMessage, Toast.LENGTH_SHORT).show();
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

    public void onTermsClick(View view){
        Intent intent = new Intent(MainLoginIn.this, LoginTerms.class);
        intent.putExtra("checkBox",false);
        startActivityForResult(intent,REQUEST_CODE);
    }

    public void onImageViewClick(View view){
        //do nothing. 遮蔽用
    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();

        //自動登入
//        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE,
//                MODE_PRIVATE);
//        boolean login = preferences.getBoolean("login", false);
//        if (login) {
//            String userId = preferences.getString("userId", "");
//            String password = preferences.getString("password", "");
//            if (isUserValid(userId, password)) {
//                setResult(RESULT_OK);
//                Intent intent = new Intent(MainLoginIn.this, Home.class);
//                startActivity(intent);
//            }
//        }
    }



    // New Permission see Appendix A
    private void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }

    private boolean isUserValid(final String userId, final String password) {
        boolean isUser = false;
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/AccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "accountValid");
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("password", password);
            String jsonOut = jsonObject.toString();
            userValidTask = new CommonTask(url, jsonOut);
            try {
                String result = userValidTask.execute().get();
                isUser = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                isUser = false;
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
        return isUser;
    }

    //for debug test
    public void gologinClick(View view){
        Intent intent = new Intent(MainLoginIn.this, Home.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userValidTask != null) {
            userValidTask.cancel(true);
            userValidTask = null;
        }
    }
}
