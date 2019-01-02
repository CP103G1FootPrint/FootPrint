package com.example.molder.footprint;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateNewAccount extends AppCompatActivity {

    private TextInputLayout createAccount;
    private TextInputLayout createPassword;
    private EditText createNickName;
    private EditText createBirthday;
    private Spinner createSpConstellation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        handleView();
    }

    public void handleView(){
        createAccount = findViewById(R.id.createAccount);
        createPassword = findViewById(R.id.createPassword);
        createNickName = findViewById(R.id.createNickName);
        createBirthday = findViewById(R.id.createBirthday);
        createSpConstellation = findViewById(R.id.createSpConstellation);

        createSpConstellation.setSelection(0, true);
        createSpConstellation.setOnItemSelectedListener(listener);
    }

    //監聽選單
    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int pos, long id) {

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
            createAccount.setError("Field can't be empty");
            return false;
        }else if(!emailInput.matches(pattern)){
            createAccount.setError("Not a valid Email");
            return false;
        }else {
            //doSomething
            createAccount.setError(null);
            return true;
        }
    }

    //檢查Password
    private boolean validatePassword(){
        String passwordInput = createPassword.getEditText().getText().toString().trim();

        if(passwordInput.isEmpty()){
            createPassword.setError("Field can't be empty");
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
            createNickName.setError("Field can't be empty");
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
            createBirthday.setError("Field can't be empty");
            return false;
        }else if(!birthday.matches(pattern)){
            createBirthday.setError("Not a valid Birthday");
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
            Toast.makeText(this,"Create Account Success",Toast.LENGTH_LONG).show();
            finish();
//            return ;
        }

    }

    //取消創新帳號
    public void createAccountCancel(View view){
        finish();
    }
//    public Boolean checkRule(String x,Object error,String pattern){
//        if(x.isEmpty()){
//            error.setError("Field can't be empty");
//            return false;
//        }else if(!x.matches(pattern)){
//            error.setError("Not a valid Birthday");
//            return false;
//        }else {
//            //doSomething
//            error.setError(null);
//            return true;
//
//        }
//    }
}
