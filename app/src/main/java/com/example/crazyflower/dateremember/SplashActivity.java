package com.example.crazyflower.dateremember;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.crazyflower.dateremember.Data.DataInMemory;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;

    private static final int GET_DATA_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        handler = new MyHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new InitDataThread();
        thread.start();
    }

    private void readyForMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class InitDataThread extends Thread {
        @Override
        public void run() {
            DataInMemory.getInstance();
            Message message = new Message();
            message.what = SplashActivity.GET_DATA_OK;
            SplashActivity.this.handler.sendMessage(message);
        }
    }

    private static class MyHandler extends Handler {

        WeakReference<SplashActivity> splashActivityWeakReference;

        MyHandler(SplashActivity splashActivity) {
            super();
            this.splashActivityWeakReference = new WeakReference<>(splashActivity);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case GET_DATA_OK:
                    splashActivityWeakReference.get().readyForMainActivity();
                    break;
            }
        }
    }

}
