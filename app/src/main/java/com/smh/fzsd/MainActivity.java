package com.smh.fzsd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smh.fzsd.service.MMAccService;
import com.smh.fzsd.utils.L;


/**
 * 界面，就俩个控制按钮和一个输入框
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton button;
    AppCompatButton btnClose;
    EditText editText;
    CountDownTimer timer;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);
        btnClose = findViewById(R.id.btn_close);
        editText = findViewById(R.id.et_time);
        tvStatus = findViewById(R.id.tv_status);
        button.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {
            if (isAccessibilitySettingsOn(this, MMAccService.class)) {
                L.d("辅助功能已经开启，启动服务");
                String time = editText.getText().toString();
                if (!TextUtils.isEmpty(time)) {
                    long intervalTime = Long.parseLong(time);
                    if (intervalTime >= 300) {
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
        } else if (v.getId() == R.id.btn_close) {
            tvStatus.setText("服务已关闭");
            timer.cancel();
            timer.onFinish();
            button.setClickable(true);
            L.e("关闭服务");
        }
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
    }
}
