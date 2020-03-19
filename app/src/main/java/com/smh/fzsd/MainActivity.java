package com.smh.fzsd;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smh.fzsd.Rx.databus.RxBus;
import com.smh.fzsd.service.MMAccService;
import com.smh.fzsd.utils.L;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


/**
 * 界面，就俩个控制按钮和一个输入框
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ControlWindowService.contralListener {
    private static final String TAG = "MainActivity";
    AppCompatButton button;
    AppCompatButton btnClose;
    EditText editText;
    CountDownTimer timer;
    TextView tvStatus;
    ControlWindowService mMyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxBus.getInstance().register(this);
        button = findViewById(R.id.btn);
        btnClose = findViewById(R.id.btn_close);
        editText = findViewById(R.id.et_time);
        tvStatus = findViewById(R.id.tv_status);
        button.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        initWindow();
        Intent bindIntent = new Intent(MainActivity.this, ControlWindowService.class);
        bindService(bindIntent, sconnection, Context.BIND_AUTO_CREATE);
        startService();
    }

    private void initWindow() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            startService(new Intent(MainActivity.this, ControlWindowService.class));
        }
    }

    /* 绑定service监听*/
    ServiceConnection sconnection = new ServiceConnection() {
        /*当绑定时执行*/
        public void onServiceConnected(ComponentName name, IBinder service) {  //service的onbind（）中返回值不为null才会触发
            mMyService = ((ControlWindowService.MyBinder) service).getService();//得到该service实例
            mMyService.setCallback(MainActivity.this, MainActivity.this);//把回调对象传送给service
        }

        /*当异常结束service时执行，但调用unbindService()时不会触发改方法 测试的话可以在bind时使用Context.BIND_NOT_FOREGROUND  调用stopservice（）可触发*/
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.e("拿到result");
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                L.e("授权失败");
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                L.e("开启服务");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
//            startService();
        } else if (v.getId() == R.id.btn_close) {
//            stopService();
        }
    }


    private void startService() {
        if (isAccessibilitySettingsOn(this, MMAccService.class)) {
            L.e("辅助功能已经开启，启动服务");
            String time = editText.getText().toString();
            if (!TextUtils.isEmpty(time)) {
                long intervalTime = Long.parseLong(time);
                if (intervalTime >= 20) {
                    L.e("启动服务,时间是:" + intervalTime);
                    tvStatus.setText("服务已开启");
                    timer = new CountDownTimer(Long.MAX_VALUE, intervalTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            button.setClickable(false);
                            startService(new Intent(MainActivity.this, MMAccService.class));
                        }

                        @Override
                        public void onFinish() {

                        }
                    };
                    timer.start();

                } else {
                    Toast.makeText(this, "时间太短", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "请填写刷新间隔时间", Toast.LENGTH_SHORT).show();
            }

        } else {
            try {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
            Toast.makeText(this, "请打开辅助功能", Toast.LENGTH_SHORT).show();
        }
    }


    private void stopService() {
        tvStatus.setText("服务已关闭");
        timer.cancel();
        button.setClickable(true);
        L.e("关闭服务");
    }

    /**
     * 判断辅助功能是否开启
     *
     * @param mContext
     * @param clazz
     * @return
     */
    public static boolean isAccessibilitySettingsOn(Context mContext, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + clazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        RxBus.getInstance().unRegister(this);
        unbindService(sconnection);//解绑下service否则  退出会报错
    }

    //复写onKeyDown事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 仿返回键退出界面,但不销毁，程序仍在后台运行
            moveTaskToBack(false); // 关键的一行代码
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void start() {
        L.e("启动服务");

        if (timer != null) {
            timer.start();

        } else {
            startService();
        }
    }

    @Override
    public void stop() {
        stopService();
    }

}
