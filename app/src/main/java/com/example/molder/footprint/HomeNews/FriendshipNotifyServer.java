package com.example.molder.footprint.HomeNews;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.molder.footprint.Friends.ChatWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class FriendshipNotifyServer {

    private static final String TAG = "FriendshipNotifyCommon";
    public final static String SERVER_URI =
            "ws://10.0.2.2:8080/FootPrint/FriendShipServer/";
//    public final static String SERVER_URI =
//            "http://192.168.196.89:8080/FootPrint/ChatServer/";
    public static FriendshipNotifyWebSocketClient friendshipNotifyWebSocketClient;
    private static List<String> friendList = new ArrayList<>();

    // 建立WebSocket連線
    public static void connectServer(String userName, Context context) {
        if (friendshipNotifyWebSocketClient == null) {
            URI uri = null;
            try {
                uri = new URI(SERVER_URI + userName);
            } catch (URISyntaxException e) {
                Log.e(TAG, e.toString());
            }
            friendshipNotifyWebSocketClient = new FriendshipNotifyWebSocketClient(uri, context);
            friendshipNotifyWebSocketClient.connect();
        }
    }

    // 中斷WebSocket連線
    public static void disconnectServer() {
        if (friendshipNotifyWebSocketClient != null) {
            friendshipNotifyWebSocketClient.close();
            friendshipNotifyWebSocketClient = null;
        }
        friendList.clear();
    }

    public static List<String> getFriendList() {
        return friendList;
    }

    public static void setFriendList(List<String> friendList) {
        FriendshipNotifyServer.friendList = friendList;
    }

    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        if (newSize <= 50) {
            // 如果欲縮小的尺寸過小，就直接定為128
            newSize = 128;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
        Log.d(TAG, text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " + scale + "\nscaled image size = " +
                    srcBitmap.getWidth() + "x" + srcBitmap.getHeight();
            Log.d(TAG, text);
        }
        return srcBitmap;
    }
}
