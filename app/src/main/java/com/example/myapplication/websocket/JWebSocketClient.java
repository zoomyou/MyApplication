package com.example.myapplication.websocket;

import android.util.Log;

import com.example.myapplication.util.utils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClient extends WebSocketClient {
    private boolean isWorking = false;
    public JWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("JWebSocketClient", "onOpen()");

        utils.setOpen(true);
    }

    @Override
    public void onMessage(String message) {
        Log.i("JWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("JWebSocketClient", "onClose()");

        utils.setOpen(false);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("JWebSClient: onError()", ex.getStackTrace().toString());
//        Log.e("JWebSClient: onError()", ex.getCause().getMessage());
        Log.e("JWebSClient: onError()", ex.getMessage());
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }
}
