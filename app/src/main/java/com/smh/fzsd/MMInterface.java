package com.smh.fzsd;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;


public interface MMInterface {

    void onAccServiceCreate(AccessibilityService service);

    void onAccServiceDestroy(AccessibilityService service);

    int onAccServiceStartCommand(AccessibilityService service, Intent intent, int flags, int startId,Context context);

    void onAccEvent(AccessibilityService service, AccessibilityEvent event);

    void onAccServiceInterrupt(AccessibilityService service);

    void onAccServiceConnected(AccessibilityService service);

}
