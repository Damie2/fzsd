package com.smh.fzsd;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.smh.fzsd.utils.L;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainEntrance implements MMInterface {

    private ExecutorService threadExecutor = null;

    @Override
    public void onAccServiceCreate(AccessibilityService service) {
        L.e("onAccServiceCreate");
        //线程池，最多只能有一个线程去跑
        threadExecutor = new ThreadPoolExecutor(
                1, 1, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    @Override
    public void onAccServiceDestroy(AccessibilityService service) {
        L.e("onAccServiceDestroy");
    }

    @Override
    public int onAccServiceStartCommand(AccessibilityService service, Intent intent, int flags, int startId, Context context) {
        try {
            //执行线程
//            L.e("执行线程");
            threadExecutor.execute(new MainTaskFlow(service, context));
        } catch (Exception e) {
            L.e(e);
        }
        //START_STICKY 防止服务挂掉
        return Service.START_STICKY;
    }

    @Override
    public void onAccEvent(AccessibilityService service, AccessibilityEvent event) {
        L.e(event.toString());
    }

    @Override
    public void onAccServiceInterrupt(AccessibilityService service) {
    }

    @Override
    public void onAccServiceConnected(AccessibilityService service) {

    }

}
