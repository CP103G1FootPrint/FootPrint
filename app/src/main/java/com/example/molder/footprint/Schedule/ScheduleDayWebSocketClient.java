package com.example.molder.footprint.Schedule;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Locale;

public class ScheduleDayWebSocketClient extends WebSocketClient{
    private static final String TAG = "ScheduleDayWebSocketClient";
    private LocalBroadcastManager broadcastManager;
    private Gson gson;

    ScheduleDayWebSocketClient(URI serverURI, Context context) {
        // Draft_17是連接協議，就是標準的RFC 6455（JSR256）
        super(serverURI, new Draft_17());
        broadcastManager = LocalBroadcastManager.getInstance(context);
        gson = new Gson();
    }

    //使用者連結伺服器成功會被呼叫
    @Override
    public void onOpen(ServerHandshake handshakeData) {
        String text = String.format(Locale.getDefault(),
                "onOpen: Http status code = %d; status message = %s",
                handshakeData.getHttpStatus(),
                handshakeData.getHttpStatusMessage());
//        Log.d(TAG, "onOpen: " + text);
    }

    //收訊息 (發訊息是在伺服器那端onMessage)
    @Override
    public void onMessage(String message) {
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
        // type: 訊息種類，有open(有user連線), close(有user離線), chat(其他user傳送來的聊天訊息)
        String type = jsonObject.get("type").getAsString();
        //發送廣播
        sendMessageBroadcast(type, message);
//        Log.d(TAG, "onMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        String text = String.format(Locale.getDefault(),
                "code = %d, reason = %s, remote = %b",
                code, reason, remote);
//        Log.d(TAG, "onClose: " + text);
    }

    @Override
    public void onError(Exception ex) {
//        Log.d(TAG, "onError: exception = " + ex.toString());
    }

    private void sendMessageBroadcast(String messageType, String message) {
        Intent intent = new Intent(messageType);
        intent.putExtra("message", message);
        broadcastManager.sendBroadcast(intent);
    }
}
