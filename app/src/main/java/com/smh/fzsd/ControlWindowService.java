package com.smh.fzsd;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.smh.fzsd.Rx.databus.RegisterRxBus;
import com.smh.fzsd.Rx.databus.RxBus;
import com.smh.fzsd.service.MMAccService;
import com.smh.fzsd.utils.L;


public class ControlWindowService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private Button button;

    private boolean type = true;
    Context context;

    contralListener listener;


    public void setCallback(contralListener listener, Context context) {//注意这里以单个回调为例  如果是向多个activity传送数据 可以定义一个回调集合 在此处进行集合的添加
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxBus.getInstance().register(this);
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 200;
        layoutParams.height = 200;
        layoutParams.x = 0;
        layoutParams.y = 600;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    private MyBinder mybinder = new MyBinder();


    public class MyBinder extends Binder {
        public ControlWindowService getService() {
            return ControlWindowService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            button = new Button(getApplicationContext());
            button.setText("已关闭");
            button.setBackgroundColor(Color.RED);
            button.setTextColor(Color.WHITE);
//            button.setVisibility(View.GONE);
            windowManager.addView(button, layoutParams);
            button.setOnTouchListener(new FloatingOnTouchListener());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click();
                }
            });
        }
    }

    public void click() {
        if (type) {
            L.e("1");
            type = false;
            button.setText("已开启");
            listener.start();
            button.setBackgroundColor(Color.BLUE);
        } else {
            stop("",0);
        }
    }


    @RegisterRxBus(1)
    private void stop( String s, int tag){
        L.e("收到RXbus，关闭");
        type = true;
        button.setText("已关闭");
        listener.stop();
        button.setBackgroundColor(Color.RED);
    }


    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unRegister(this);
    }

    public interface contralListener {
        void start();

        void stop();
    }
}
