package com.example.myapplication.service;

import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.util.utils;
import com.example.myapplication.websocket.JWebSocketClient;

public class MyThread extends Thread{
    JWebSocketClient jWebSocketClient = null;
    private String jobID = "";

    public MyThread(JWebSocketClient jWebSocketClient, String jobID){
        this.jWebSocketClient = jWebSocketClient;
        this.jobID = jobID;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!jobID.equals(MainActivity.curJobID))
            return;

        if (jWebSocketClient != null){
            if (jWebSocketClient.isOpen()){
                Log.i("myThread", "timeUp");
                jWebSocketClient.send("timeUp");
                utils.setWorking(false, "", "");
                MainActivity.curJobID = "";
            }
        }
    }
}
