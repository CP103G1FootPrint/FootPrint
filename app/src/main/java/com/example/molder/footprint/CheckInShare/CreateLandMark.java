package com.example.molder.footprint.CheckInShare;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.molder.footprint.Common.Common;
import com.example.molder.footprint.Common.CommonTask;
import com.example.molder.footprint.Map.LandMark;
import com.example.molder.footprint.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import static com.example.molder.footprint.CheckInShare.CheckInShareFragment.locationGPS;

public class CreateLandMark extends AppCompatActivity {

    private static final String TAG = "CreateLandMark";
    private static final int REQ_Location = -1;
    private Geocoder geocoder;
    private EditText name,address,description,openHours,star;
    private Spinner type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_land_mark);
        geocoder = new Geocoder(this);
        findView();
    }

    private void findView(){
        name = findViewById(R.id.ckEtLandMarkName);
        address = findViewById(R.id.ckEtAddress);
        description = findViewById(R.id.ckEtDescription);
        openHours = findViewById(R.id.ckEtOpeningHours);
        star = findViewById(R.id.ckEtEvaluation);
        type = findViewById(R.id.btnCkLandMarkType);
        //預選第四個
        type.setSelection(3, true);
    }

    //取消
    public void onCancelCreateLandMarkClick(View view){
        finish();
    }

    //新建地標
    public void onSaveLandMarkClick(View view){

        if(checkInfoData(name) && checkInfoData(address) && checkInfoData(description) &&
        checkInfoData(openHours) && checkStar(star)){
            String landMarkName = name.getText().toString().trim();
            String landMarkDescription = description.getText().toString().trim();
            String landMarkOpenHours = openHours.getText().toString().trim();
            Double landMarkStar = Double.valueOf(star.getText().toString().trim());
            Object item = type.getSelectedItem();
            String landMarkType = item.toString().trim();
            Double latitude,longitude;
            int landMarkId = 0;

            String landMarkAddress = address.getText().toString().trim();
            List<Address> addressList = null;
            //收尋上限
            int maxResults = 1;
            try {
                //將使用者輸入的地址轉成物件
                addressList = geocoder.getFromLocationName(landMarkAddress, maxResults);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            if (addressList == null || addressList.isEmpty()) {
                //GPS 位置
                latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();
            } else {
                //收尋多筆要用for ench
                Address address = addressList.get(0);
                // 取得緯度經度 （將地址轉成緯度經度）
                latitude = address.getLatitude();
                longitude = address.getLongitude();
            }

            //find LandMark last id
            if (Common.networkConnected(this)) {
                String url = Common.URL + "/LocationServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findLandMarkLastId");
                try {
                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                    landMarkId = Integer.valueOf(result);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (landMarkId == 0) {
                    Common.showToast(this, R.string.msg_InsertFail);
                }
            }

            //insert LandMark
            if (Common.networkConnected(this)) {
                String url = Common.URL + "/LocationServlet";
                LandMark landMark = new LandMark(landMarkId + 1,landMarkName,landMarkAddress,latitude,longitude,landMarkDescription,landMarkOpenHours,landMarkType,landMarkStar);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "landMarkInsert");
                jsonObject.addProperty("landMark", new Gson().toJson(landMark));
                int count = 0;
                try {
                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                    count = Integer.valueOf(result);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
                    Common.showToast(this, R.string.msg_InsertFail);
                }else {
                    //回傳
                    Intent intent = getIntent();
                    intent.putExtra("textLandMark",landMarkAddress);
                    intent.putExtra("landMarkID",landMarkId + 1);
                    setResult(REQ_Location, intent);
                    finish();
                }
            }
        }
    }

    private boolean checkInfoData(EditText editText){
        String text = editText.getText().toString().trim();

        if(text.isEmpty()){
            editText.setError("未填");
            return false;
        }else {
            editText.setError(null);
            return true;
        }
    }

    private boolean checkStar(EditText editText){
        String evaluation = editText.getText().toString().trim();
        String pattern = "^([0-5].?[0-9]?)$";

        if(evaluation.isEmpty()){
            editText.setError("未填");
            return false;
        }else if(!evaluation.matches(pattern)){
            editText.setError("無效評價");
            return false;
        }else {
            editText.setError(null);
            return true;
        }
    }

}
