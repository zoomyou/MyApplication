package com.example.myapplication.service;

import com.example.myapplication.websocket.JWebSocketClient;

public class MyThread implements Runnable{
    JWebSocketClient jWebSocketClient = null;

    public MyThread(JWebSocketClient jWebSocketClient){
        this.jWebSocketClient = jWebSocketClient;
    }

    @Override
    public void run() {
        for (int i = 0; i < 60; i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!jWebSocketClient.isWorking()){
                return;
            }
        }

        if (jWebSocketClient != null){
            if (jWebSocketClient.isOpen()){
                jWebSocketClient.send("timeUp");
            }
        }
    }
}
