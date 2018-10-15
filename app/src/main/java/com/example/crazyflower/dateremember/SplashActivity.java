package com.example.crazyflower.dateremember;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.crazyflower.dateremember.Data.DatabaseManager;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private SplashActivityHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new SplashActivityHandler(this);
        handler.updateDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void finishTask() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }

    static private class SplashActivityHandler extends Handler {

        SplashActivity splashActivity;

        DatabaseManager databaseManager;

        SplashActivityHandler(SplashActivity splashActivity) {
            this.splashActivity = splashActivity;
            this.databaseManager = new DatabaseManager(splashActivity);
        }

        void updateDatabase() {
            databaseManager.updateDatabase(this);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DatabaseManager.UPDATE_OK:
                    splashActivity.finishTask();
                    break;
            }
        }
    }

}
