package com.example.molder.footprint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class MainLoginIn extends AppCompatActivity {

    CallbackManager callbackManager;
    private AccessToken accessToken;
    LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);

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
        Intent intent = new Intent(MainLoginIn.this, Home.class);
        startActivity(intent);
    }



    //登入後進入 Home 頁面
    public void onLoginInClick(View view){
        LayoutInflater inflater = LayoutInflater.from(MainLoginIn.this);
        final View v = inflater.inflate(R.layout.fragment_login_in, null);
        new AlertDialog.Builder(MainLoginIn.this)
                .setTitle(R.string.textLoginIn)
                .setView(v)
                .setMessage(R.string.textLoginInMessage)
                .setPositiveButton(R.string.textConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainLoginIn.this, Home.class);
                        startActivity(intent);

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
        startActivity(intent);
    }

    public void onImageViewClick(View view){
        //do nothing. 遮蔽用
    }
}
