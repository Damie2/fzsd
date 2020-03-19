package com.smh.fzsd.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.smh.fzsd.MMInterface;
import com.smh.fzsd.MainEntrance;


public class MMAccService extends AccessibilityService {
    private static final String TAG = "MMAccService";
    MMInterface mmInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mmInterface = new MainEntrance();
        mmInterface.onAccServiceCreate(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mmInterface.onAccServiceDestroy(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mmInterface.onAccServiceStartCommand(this, intent, flags, startId, getApplicationContext());
        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mmInterface.onAccEvent(this, event);
    }

    @Override
    public void onInterrupt() {
        mmInterface.onAccServiceInterrupt(this);
    }



    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    protected void onServiceConnected() {
        mmInterface.onAccServiceConnected(this);
    }
}
