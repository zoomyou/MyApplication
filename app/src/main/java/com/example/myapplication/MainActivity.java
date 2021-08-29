package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.service.MyThread;
import com.example.myapplication.util.utils;
import com.example.myapplication.websocket.JWebSocketClient;

import org.java_websocket.WebSocket;

import java.net.URI;
import java.util.HashSet;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static boolean isWorking = false;
//    final String WS_URL = "ws://10.62.141.109:8080/socket";
    final String WS_URL = "ws://121.5.113.98:80/socket/mini";
    // 图片显示区
    public static ImageView imageView = null;
    // 拖动条
    public static SeekBar rotateBar = null;
    // 文本框
    public static EditText editText = null;
    // 开始键、提交键、放弃键
    public static Button openOrClose = null;
    public static Button submit = null;
    public static Button abandon = null;
//    private CountDownTimer countDownTimer = null;

    private int curAngle = 0;

    private JWebSocketClient jWebSocketClient;

    public static HashSet<String> jobIDs = new HashSet<>();
    public static String curJobID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        curAngle = 0;

        // 初始化全局的组件
        imageView = findViewById(R.id.captchaView);
        rotateBar = findViewById(R.id.rotate);
        editText = findViewById(R.id.inputData);
        openOrClose = findViewById(R.id.openClose);
        submit = findViewById(R.id.submit);
        abandon = findViewById(R.id.abandon);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Log.i("point", (int)motionEvent.getX() + "," + (int)motionEvent.getY());
                }

                if (jWebSocketClient == null)
                    return false;

                if (!jWebSocketClient.isOpen())
                    return false;

                if (!isWorking)
                    return false;

                int curX = (int) motionEvent.getX();
                int curY = (int) motionEvent.getY();

                String result = String.valueOf(editText.getText());
                editText.setText(result + "" + curX + "," + curY + " ");

                return false;
            }
        });

        rotateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("change", (int) (i * 3.6) +"");

                if (jWebSocketClient == null)
                    return;

                if (!jWebSocketClient.isOpen())
                    return;

                if (!isWorking)
                    return;

                int angle = (int) (i * 3.6);

                imageView.animate().rotation(angle);
                setCurAngle(angle);

                editText.setText(angle+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        imageView.animate().rotation(0);
    }

    public void openOrClose(View view){
        URI uri = URI.create(WS_URL);
        if (jWebSocketClient == null){
            jWebSocketClient = new JWebSocketClient(uri){
                @Override
                public void onMessage(String message){
                    Log.i("JWebSocketService", "onMessage()");

                    String jobID = UUID.randomUUID().toString();
                    curJobID = jobID;
                    jobIDs.add(jobID);

                    MyThread myThread = new MyThread(jWebSocketClient, jobID);
                    myThread.start();

                    editText.setText("");
                    rotateBar.setProgress(0);

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
        editText.setText("");
        imageView.animate().rotation(-curAngle);
        curAngle = 0;
        rotateBar.setProgress(0);
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
        editText.setText("");
        rotateBar.setProgress(0);
    }

    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public int getCurAngle() {
        return curAngle;
    }

    public void setCurAngle(int curAngle) {
        this.curAngle = curAngle;
    }

}