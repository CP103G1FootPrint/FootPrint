package com.example.molder.footprint.Common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;

import static android.content.Context.MODE_PRIVATE;

public class Common {
	// Android官方模擬器連結本機web server可以直接使用 http://10.0.2.2
//	public final static String URL = "http://10.0.2.2:8080/";
//    public final static String URL = "http://sewd.no-ip.org:8080/FootPrint";
//    public final static String URL = "http://192.168.196.157:8080/FootPrint";
    public final static String URL = "http://10.0.2.2:8080/FootPrint";
//	public final static String URL = "http://10.0.2.2:8080/FootPrint";
//	public final static String URL = "http://10.0.2.2:8080/TextToJson_Web";

    private final static String TAG = "Common";
    public static final String SERVER_URI =
            "ws://10.0.2.2:8080/WSChatBasic_Web/TwoChatServer/";
//    public static final String SERVER_URI =
//            "ws://192.168.196.157/WSChatBasic_Web/TwoChatServer/";
    public static WebSocketClient webSocketClient;

    // 建立WebSocket連線
    public static void connectServer(Context context, String userName) {
        URI uri = null;
        try {
            uri = new URI(SERVER_URI + userName);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        if (webSocketClient == null) {
            webSocketClient = new WebSocketClient(uri, context);
            webSocketClient.connect();
        }
    }

    // 中斷WebSocket連線
    public static void disconnectServer() {
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        Log.d(TAG, "userId = " + userId);
        return userId;
    }

	public final static String PREF_FILE = "preference";
	// check if the device connect to the network
	public static boolean networkConnected(Activity activity) {
		ConnectivityManager conManager =
				(ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
		return networkInfo != null && networkInfo.isConnected();
	}

	public static void showToast(Context context, int messageResId) {
		Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
