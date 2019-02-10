package com.example.molder.footprint.Common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

public class Common {
	// Android官方模擬器連結本機web server可以直接使用 http://10.0.2.2
//	public final static String URL = "http://10.0.2.2:8080/";
    public final static String URL = "http://10.0.2.2:8080/FootPrint";
//	public final static String URL = "http://10.0.2.2:8080/FootPrint";
//	public final static String URL = "http://10.0.2.2:8080/TextToJson_Web";

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
