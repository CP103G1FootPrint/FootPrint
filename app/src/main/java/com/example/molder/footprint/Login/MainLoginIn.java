package com.example.molder.footprint.Login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Home;
import com.example.molder.footprint.Map.LandMark;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MainLoginIn extends AppCompatActivity {

    private static final String TAG = "MainLoginIn";
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private LoginButton loginButton;
    private static final int REQ_PERMISSIONS = 0;
    private TextInputEditText tIETAccount, tIETPassword;
    private CommonTask userValidTask;
    private byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        setResult(RESULT_CANCELED);
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //登入成功

            @Override
            public void onSuccess(LoginResult loginResult) {

                //accessToken之後或許還會用到 先存起來

                accessToken = loginResult.getAccessToken();

//                Log.d("FB", "access token got.");

                //send request and call graph api

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                //讀出姓名 ID FB個人頁面連結
//                                Log.d("FB1", "complete");
//                                Log.d("FB2", object.optString("name"));
//                                Log.d("FB3", object.optString("link"));
//                                Log.d("FB4", object.optString("id"));
//                                Log.d("FB5", object.optString("email"));
//                                Log.d("FB6", object.optString("picture"));
//                                Log.d("FB7", object.optString("birthday"));

                                String emailInput = object.optString("id");
                                String nickName = object.optString("name");
                                String passwordInput = "footPrint"; //預設
                                int integral = 0;
                                int fb = 1;
                                String birthday = object.optString("birthday");
                                String category = "Capricorn 12/21 - 1/20"; //預設

                                String p1 = object.optString("picture");
                                JsonObject jsonObjectPicture = new Gson().fromJson(p1, JsonObject.class);

                                String data = jsonObjectPicture.get("data").toString();
                                JsonObject jsonObjectData = new Gson().fromJson(data, JsonObject.class);
                                //url
                                String uriString = jsonObjectData.get("url").getAsString();
                                Bitmap picture = null;
                                LoginTask loginTask = new LoginTask(uriString);
                                try {
                                    picture = loginTask.execute().get();
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    image = out.toByteArray();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);

                                if(isUserValidFb(object.optString("id"),1)){
                                    preferences.edit().putBoolean("login", true)
                                            .putString("userId", emailInput)
                                            .putString("password", passwordInput).apply();
                                    setResult(RESULT_OK);
                                    finish();
                                    Intent intent = new Intent(MainLoginIn.this, Home.class);
                                    startActivity(intent);
                                }else {
                                    //fb帳號 insert
                                    if (Common.networkConnected(MainLoginIn.this)) {
                                        String url = Common.URL + "/AccountServlet";
                                        Account account = new Account(emailInput, passwordInput, nickName, birthday, integral,category,fb);
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "accountInsert");
                                        jsonObject.addProperty("account", new Gson().toJson(account));
                                        if(image != null){
                                            String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
                                            jsonObject.addProperty("imageBase64", imageBase64);
                                        }
                                        int count = 0;
                                        try {
                                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                            count = Integer.valueOf(result);

                                        } catch (Exception e) {
//                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(MainLoginIn.this, R.string.msg_InsertFail);
                                        } else {
                                            // user ID and password will be saved in the preferences file
                                            // and starts UserActivity
                                            // while the user account is created successfully
                                            preferences.edit().putBoolean("login", true)
                                                    .putString("userId", emailInput)
                                                    .putString("password", passwordInput).apply();
                                            setResult(RESULT_OK);
                                            finish();
                                            Intent intent = new Intent(MainLoginIn.this, Home.class);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Common.showToast(MainLoginIn.this, R.string.msg_NoNetwork);
                                    }
                                }
                            }
                        });

                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture.width(600).height(600),birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            //登入取消

            @Override
            public void onCancel() {
                // App code

//                Log.d("FB", "CANCEL");
            }

            //登入失敗

            @Override
            public void onError(FacebookException exception) {
                // App code

//                Log.d("FB", exception.toString());
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //登入後進入 Home 頁面
    public void onLoginInClick(View view) {
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

                        if (isUserValid(user, password)) {
                            SharedPreferences preferences = getSharedPreferences(
                                    Common.PREF_FILE, MODE_PRIVATE);
                            preferences.edit().putBoolean("login", true)
                                    .putString("userId", user)
                                    .putString("password", password).apply();
                            setResult(RESULT_OK);
                            finish();
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
    public void onCreateNewAccountClick(View view) {
        Intent intent = new Intent(MainLoginIn.this, CreateNewAccount.class);
        startActivity(intent);
    }

    //忘記密碼dialog視窗
    public void onForgotPasswordClick(View view) {
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

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();

        //自動登入
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        boolean login = preferences.getBoolean("login", false);
        if (login) {
            String userId = preferences.getString("userId", "");
            String password = preferences.getString("password", "");
            if (isUserValid(userId, password)) {
                setResult(RESULT_OK);
                finish();
                Intent intent = new Intent(MainLoginIn.this, Home.class);
                startActivity(intent);
            }
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
//                Log.e(TAG, e.toString());
                isUser = false;
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
        return isUser;
    }

    private boolean isUserValidFb(final String userId, final int fbID) {
        boolean isUser = false;
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/AccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "accountValidFB");
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("fbId", fbID);
            String jsonOut = jsonObject.toString();
            userValidTask = new CommonTask(url, jsonOut);
            try {
                String result = userValidTask.execute().get();
                isUser = Boolean.valueOf(result);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
                isUser = false;
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
        return isUser;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static  Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("downloadImage"+ e1.toString());
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream

    public static  InputStream getHttpConnection(String urlString)  throws IOException {

        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("downloadImage" + ex.toString());
        }
        return stream;
    }

    /**     AsyncTAsk for Image Bitmap  */
    private class LoginTask extends AsyncTask<Void, Void, Bitmap> {
        String url;

        public LoginTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return downloadImage(url);
        }

    }
//    private class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {
//
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//
//            System.out.println("doInBackground");
//
//            Bitmap bitmap = null;
//
//            bitmap = AppMethods.downloadImage(params[0]);
//
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//
//            System.out.println("bitmap" + bitmap);
//
//        }
//    }



    @Override
    public void onStop() {
        super.onStop();
        if (userValidTask != null) {
            userValidTask.cancel(true);
            userValidTask = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (keyCode == KeyEvent.KEYCODE_BACK && count == 0) {
            new AlertDialogFragment().show(getSupportFragmentManager(), "exit");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class AlertDialogFragment
            extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.text_Exit)
                    .setMessage(R.string.msg_WantToExit)
                    .setPositiveButton(R.string.msg_ok, this)
                    .setNegativeButton(R.string.text_No, this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (getActivity() != null) {
                        getActivity().finish();

                    }
                    break;
                default:
                    dialog.cancel();
                    break;
            }
        }
    }
}
