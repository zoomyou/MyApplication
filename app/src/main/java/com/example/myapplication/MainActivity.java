package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.util.utils;
import com.example.myapplication.websocket.JWebSocketClient;

import org.java_websocket.WebSocket;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    public static boolean isWorking = false;
//    final String WS_URL = "ws://10.62.141.109:8080/socket";
    final String WS_URL = "ws://121.5.113.98:80/socket/mini";
    // 图片显示区
    public static ImageView imageView = null;
    // 文本框
    public static EditText editText = null;
    // 开始键、提交键、放弃键
    public static Button openOrClose = null;
    public static Button submit = null;
    public static Button abandon = null;

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
                @Override
                public void onMessage(String message){
                    Log.i("JWebSocketService", "onMessage()");

                    // 设置状态解析数据，向前端展示
                    JSONObject job = JSONObject.parseObject(message);
                    utils.setWorking(true, job.getString("src_type"), job.getString("show_data"));
                }
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

    public void submit(View view){
        if (jWebSocketClient == null || !jWebSocketClient.isOpen()){
            showToast("任务接收未开启！");
            return;
        }

        if (!isWorking){
            showToast("还未收到任务！");
            return;
        }

        String result = editText.getText().toString().trim();
        if (result.equals("")){
            showToast("结果为空！");
            return;
        }
        jWebSocketClient.send(result);
        utils.setWorking(false, "", "");
    }

    public void abandon(View view){
        if (jWebSocketClient == null || !jWebSocketClient.isOpen()){
            showToast("任务接收未开启！");
            return;
        }

        if (!isWorking){
            showToast("还未收到任务！");
            return;
        }
        jWebSocketClient.send("giveup");
        utils.setWorking(false, "", "");
    }

    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}