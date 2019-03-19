package com.example.molder.footprint.Schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.molder.footprint.Common.Common;

import java.net.URI;
import java.net.URISyntaxException;

import static android.content.Context.MODE_PRIVATE;


public class ScheduleDayServer {

    private final static String TAG = "ScheduleDayCommon";
    public static final String SERVER_URI = "ws://10.0.2.2:8080/FootPrint/ScheduleDayServer/";
//    public static final String SERVER_URI =
//            "http://192.168.196.89:8080/FootPrint/ScheduleDayServer/";
    public static ScheduleDayWebSocketClient scheduleDayWebSocketClient;

    // 建立WebSocket連線
    public static void connectServer(Context context, String userName) {
        URI uri = null;
        try {
            uri = new URI(SERVER_URI + userName);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        if (scheduleDayWebSocketClient == null) {
            scheduleDayWebSocketClient = new ScheduleDayWebSocketClient(uri, context);
            scheduleDayWebSocketClient.connect();
        }
    }

    // 中斷WebSocket連線
    public static void disconnectServer() {
        if (scheduleDayWebSocketClient != null) {
            scheduleDayWebSocketClient.close();
            scheduleDayWebSocketClient = null;
        }
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        return userId;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
    }
}
