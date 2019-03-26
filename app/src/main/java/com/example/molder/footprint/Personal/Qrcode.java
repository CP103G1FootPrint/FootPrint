package com.example.molder.footprint.Personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.molder.footprint.R;
import com.example.molder.footprint.qrcode.Contents;
import com.example.molder.footprint.qrcode.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.security.Key;

import static android.content.ContentValues.TAG;

public class Qrcode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_exchange_qrcode); //連到的layout
        Intent intent = getIntent();

        String productId = intent.getStringExtra("key");
        Toast.makeText(this, productId, Toast.LENGTH_SHORT).show();
        ImageView qrcode = findViewById(R.id.qrcode);
        Button btCancel = findViewById(R.id.btCancel);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // QR code image's length is the same as the width of the window,
        int dimension = getResources().getDisplayMetrics().widthPixels;

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(productId, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                dimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            qrcode.setImageBitmap(bitmap);


        } catch (WriterException e) {
//            Log.e(TAG, e.toString());
        }



    }
}
