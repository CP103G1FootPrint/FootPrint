package com.example.molder.footprint.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.R;
import com.facebook.CallbackManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;



public class CreateNewAccount extends AppCompatActivity {

    private static final String TAG = "CreateNewAccount";
    private static final int REQUEST_CODE = 1;
    private TextInputLayout createAccount;
    private TextInputLayout createPassword;
    private EditText createNickName;
    private EditText createBirthday;
    private Spinner createSpConstellation;
    private boolean accountExist = false;
    private CommonTask accountExistTask;
    private CheckBox mCheckBox;
    private boolean result = false;
    private CallbackManager callbackManager;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        callbackManager = CallbackManager.Factory.create();
        handleView();
    }

    public void handleView(){
        createAccount = findViewById(R.id.createAccount);
        createPassword = findViewById(R.id.createPassword);
        createNickName = findViewById(R.id.createNickName);
        createBirthday = findViewById(R.id.createBirthday);
        createSpConstellation = findViewById(R.id.createSpConstellation);
        mCheckBox = findViewById(R.id.loginCbTerms);
        textView = findViewById(R.id.loginTextTerms);

        textView.setText(Html.fromHtml("I Agree to <font color='#ff0000'><big>Terms of Service</big></font> and <font color='#ff0000'><big>Privacy Policy</big></font>"));

        createSpConstellation.setSelection(0, true);
        createSpConstellation.setOnItemSelectedListener(listener);
    }

    //監聽選單
    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int pos, long id) {
//            Object item = createSpConstellation.getSelectedItem();
//            String category = item.toString().trim();
//            showToast(CreateNewAccount.this,category);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //檢查Email
    private boolean validateEmail(){
        String emailInput = createAccount.getEditText().getText().toString().trim();
        String pattern = "^.*@gmail\\.com$";

        if(emailInput.isEmpty() ){
            createAccount.setError("未填");
            return false;
        }else if(!emailInput.matches(pattern)){
            createAccount.setError("無效帳號");
            return false;
        }if (Common.networkConnected(this)) {
            String url = Common.URL + "/AccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "accountExist");
            jsonObject.addProperty("userId", emailInput);
            String jsonOut = jsonObject.toString();
            accountExistTask = new CommonTask(url, jsonOut);
            try {
                String result = accountExistTask.execute().get();
                accountExist = Boolean.valueOf(result);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            // show an error message if the id exists;
            // otherwise, the error message should be clear
            if (accountExist) {
                createAccount.setError("帳號已存在");
                return false;
            } else {
                createAccount.setError(null);
                return true;
            }
        } else {
            //doSomething
            createAccount.setError(null);
            return true;
        }
    }

    //檢查Password
    private boolean validatePassword(){
        String passwordInput = createPassword.getEditText().getText().toString().trim();

        if(passwordInput.isEmpty()){
            createPassword.setError("未填");
            return false;
        }else {
            createPassword.setError(null);
            return true;
        }
    }

    //檢查Nick Name
    private boolean validateNickName(){
        String nickName = createNickName.getText().toString().trim();

        if(nickName.isEmpty()){
            createNickName.setError("未填");
            return false;
        }else {
            createNickName.setError(null);
            return true;
        }
    }

    //檢查birthday
    private boolean validateBirthday(){
        String birthday = createBirthday.getText().toString().trim();
        String pattern = "^(0?[1-9]|1[0-2])/(0?[1-9]|1\\d|2\\d|3[0-1])$";

        if(birthday.isEmpty()){
            createBirthday.setError("未填");
            return false;
        }else if(!birthday.matches(pattern)){
            createBirthday.setError("無效生日");
            return false;
            }else {
            //doSomething
            createBirthday.setError(null);
            return true;
        }
    }

    //確認按鈕 當以上檢查都成立時會送出
    public void createAccountConfirm(View view){
        if(validateEmail() && validatePassword() && validateNickName() && validateBirthday()){

            String emailInput = createAccount.getEditText().getText().toString().trim();
            String passwordInput = createPassword.getEditText().getText().toString().trim();
            String nickName = createNickName.getText().toString().trim();
            String birthday = createBirthday.getText().toString().trim();
            Object item = createSpConstellation.getSelectedItem();
            String category = item.toString().trim();
            int integral = 0;
            int fb = 0;

            if (result == false) {
                Toast.makeText(CreateNewAccount.this, R.string.textNotReadTerms, Toast.LENGTH_SHORT).show();
                return;
            }

            if (Common.networkConnected(this)) {
                String url = Common.URL + "/AccountServlet";
                Account account = new Account(emailInput, passwordInput, nickName, birthday, integral,category,fb);
                JsonObject jsonObject = new JsonObject();
                //新增
                jsonObject.addProperty("action", "accountInsert");
                //地址 電話
                jsonObject.addProperty("account", new Gson().toJson(account));
                int count = 0;
                try {
                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                    count = Integer.valueOf(result);

                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
                    Common.showToast(this, R.string.msg_InsertFail);
                } else {
                    // user ID and password will be saved in the preferences file
                    // and starts UserActivity
                    // while the user account is created successfully
                    SharedPreferences preferences = getSharedPreferences(
                            Common.PREF_FILE, MODE_PRIVATE);
                    preferences.edit().putBoolean("login", true)
                            .putString("userId", emailInput)
                            .putString("password", passwordInput).apply();
//                    Common.showToast(this, R.string.msg_InsertSuccess);
                    finish();
                }
            } else {
                Common.showToast(this, R.string.msg_NoNetwork);
            }

        }

    }

    //取消創新帳號
    public void createAccountCancel(View view){
        finish();
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void onTermsClick(View view){
        Intent intent = new Intent(CreateNewAccount.this, LoginTerms.class);
        intent.putExtra("checkBox",false);
        startActivityForResult(intent,REQUEST_CODE);
    }

    public void onImageViewClick(View view){
        //do nothing. 遮蔽用
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

    @Override
    public void onStop() {
        super.onStop();
        if (accountExistTask != null) {
            accountExistTask.cancel(true);
            accountExistTask = null;
        }
    }
}
