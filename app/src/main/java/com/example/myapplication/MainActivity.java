package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapplication.websocket.JWebSocketClient;

import org.java_websocket.WebSocket;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    public static boolean isWorking = false;
    final String WS_URL = "ws://10.62.141.109:8080/socket";
    // 图片显示区
    public ImageView imageView = null;
    // 文本框
    public EditText editText = null;
    // 开始键、提交键、放弃键
    public Button openOrClose = null;
    public Button submit = null;
    public Button abandon = null;

    private JWebSocketClient jWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化全局的组件
        imageView = findViewById(R.id.captchaView);
        editText = findViewById(R.id.inputData);
        openOrClose = findViewById(R.id.openClose);
        submit = findViewById(R.id.submit);
        abandon = findViewById(R.id.abandon);
    }

    public void openOrClose(View view){
        URI uri = URI.create(WS_URL);
        if (jWebSocketClient == null){
            jWebSocketClient = new JWebSocketClient(uri){
//                @Override
//                public void onMessage(String message){
//                    Log.e("JWebSocketService", message);
//                    System.out.println("JWebSocketService"+message);
//                }
            };
        }

        if (!jWebSocketClient.isOpen()){
            if (jWebSocketClient.getReadyState().equals(WebSocket.READYSTATE.NOT_YET_CONNECTED) ||
                    jWebSocketClient.getReadyState().equals(WebSocket.READYSTATE.CLOSED) ||
                    jWebSocketClient.getReadyState().equals(WebSocket.READYSTATE.CLOSING)){
                jWebSocketClient.connect();
            }
        } else {
            jWebSocketClient.close();
            jWebSocketClient = null;
        }
    }

    public void submit(View view){}

    public void abandon(View view){}
}