package com.example.molder.footprint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainLoginIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
    }

    //登入後進入 Home 頁面
    public void onLoginInClick(View view){
        Intent intent = new Intent(MainLoginIn.this, Home.class);
        startActivity(intent);
    }
}
