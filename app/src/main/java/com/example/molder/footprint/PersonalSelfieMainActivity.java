package com.example.molder.footprint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class PersonalSelfieMainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btTakePictureLarge, btPickPicture;
    private File file;
    private static final int REQUEST_TAKE_PICTURE_SMALL = 0;
    private static final int REQUEST_TAKE_PICTURE_LARGE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_selfie);
        findViews();
    }

    private void findViews() {
        imageView = findViewById(R.id.imageView);
        btTakePictureLarge = findViewById(R.id.btTakePictureLarge);
        btPickPicture = findViewById(R.id.btPickPicture);
    }

    public void onTakePictureSmallClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQUEST_TAKE_PICTURE_SMALL);
        } else {
            Toast.makeText(this, R.string.textNoCameraAppsFound,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onTakePictureLargeClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");
        Uri contentUri = FileProvider.getUriForFile(
                this, getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQUEST_TAKE_PICTURE_LARGE);
        } else {
            Toast.makeText(this, R.string.textNoCameraAppsFound,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onPickPictureClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PICTURE);
    }

    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE_SMALL:
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Bitmap picture = (Bitmap) bundle.get("data");
                        imageView.setImageBitmap(picture);
                    }
                    break;
                case REQUEST_TAKE_PICTURE_LARGE:
                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    Bitmap downsizedPicture = PersonalSelfieCommon.downSize(srcPicture, newSize);
                    imageView.setImageBitmap(downsizedPicture);
                    break;
                case REQUEST_PICK_PICTURE:
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String[] columns = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri, columns,
                                null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String imagePath = cursor.getString(0);
                            cursor.close();
                            Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                            Bitmap downsizedImage = PersonalSelfieCommon.downSize(srcImage, newSize);
                            imageView.setImageBitmap(downsizedImage);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PersonalSelfieCommon.askPermissions(this, permissions, PersonalSelfieCommon.REQ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PersonalSelfieCommon.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btTakePictureLarge.setEnabled(true);
                    btPickPicture.setEnabled(true);
                } else {
                    btTakePictureLarge.setEnabled(false);
                    btPickPicture.setEnabled(false);
                }
                break;
        }
    }


}
