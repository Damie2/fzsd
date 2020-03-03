package com.smh.fzsd;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.os.Build;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.smh.fzsd.utils.AccessibilityHelper;
import com.smh.fzsd.utils.L;
import com.smh.fzsd.utils.ScreenUtils;

import java.util.List;

public class MainTaskFlow implements Runnable {
    AccessibilityService accService;
    Context context;

    public MainTaskFlow(AccessibilityService accService, Context context) {
        this.accService = accService;
        this.context = context;
    }

    /**
     * 主业务逻辑线程
     */
    @Override
    public void run() {
        AccessibilityNodeInfo rootInActiveWindow = accService.getRootInActiveWindow();
        if (rootInActiveWindow != null) {
            //判断是不是在购买标签，下面那四个
            List<AccessibilityNodeInfo> infos = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.ao.miao:id/navigation_buy_z");
            if (infos != null && infos.size() > 0) {
                AccessibilityNodeInfo buy = infos.get(0);
                if (!buy.isSelected()) {
                    L.e("购买标签没有选中");
                    return;
                }
            }

            //发现有下拉刷新界面的时候，才执行
            if (AccessibilityHelper.hasNodeForId(rootInActiveWindow, "com.ao.miao:id/refresh", "android.view.ViewGroup")) {
                slideVertical(accService);
            }
        }
    }

    //下拉刷新方法
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void slideVertical(final AccessibilityService accService) {
        int screenHeight = ScreenUtils.getScreenHeight(context);
        int screenWidth = ScreenUtils.getScreenWidth(context);
        Path path = new Path();

        int start = screenHeight / 5;
        int stop = screenHeight / 2;

        path.moveTo(screenWidth / 2, start);//如果只是设置moveTo就是点击
        path.lineTo(screenWidth / 2, stop);//如果设置这句就是滑动

        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.
                        StrokeDescription(path,
                        200,
                        299))
                .build();

        accService.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                L.w("滑动结束");
                AccessibilityNodeInfo rootInActiveWindow = accService.getRootInActiveWindow();
                //滑动结束后，需要重新获取界面所有元素
                if (rootInActiveWindow != null) {
                    //获取所有购买按钮，并点击第一个
                    List<AccessibilityNodeInfo> infos =
                            rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.ao.miao:id/btn_buy_ys_selling_order");//获取全部的购买按钮
                    if (infos != null && infos.size() > 0) {
                        if (infos.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)) {//点击第一个购买按钮
//                        if (infos.get(infos.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK)) {//点击最后一个
                            L.e("最后一个点击成功");
                            clickBuy(accService);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                L.w("滑动取消");
            }
        }, null);
    }

    private void clickBuy(AccessibilityService accService) {
        L.e("点击dialog里购买按钮");
        SystemClock.sleep(200);
        AccessibilityNodeInfo rootInActiveWindow = accService.getRootInActiveWindow();
        if (rootInActiveWindow != null) {
            if (AccessibilityHelper.clickById(rootInActiveWindow, "com.ao.miao:id/tv_sure")) {
                L.e("购买成功");
            }
        }
    }
}
