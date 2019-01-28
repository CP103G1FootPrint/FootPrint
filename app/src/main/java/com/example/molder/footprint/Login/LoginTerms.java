package com.example.molder.footprint.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.molder.footprint.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginTerms extends AppCompatActivity {
    private final static String TAG = "Error on Terms page";
    private static final int REQUEST_CODE = 1;
    private TextView tvAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_terms);

        //讀取規章
        tvAsset = findViewById(R.id.tvLoginTerms);
        BufferedReader br = null;
        try {
            InputStream is = getAssets().open("Terms of Service and Privacy Policy.txt");
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
                sb.append("\n");
            }
            tvAsset.setText(sb);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void onTermsConfirmClick(View view){

        Intent intent = getIntent();
        intent.putExtra("checkBox",true);
        setResult(REQUEST_CODE, intent); //REQUEST_CODE 需跟AActivity.class的一樣
        finish();
    }

    public void onTermsCancelClick(View view){
        finish();
    }
}