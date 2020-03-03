package com.smh.fzsd.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class AccessibilityHelper {

    private boolean isSameServiceInfo(AccessibilityServiceInfo a, AccessibilityServiceInfo b) {
        if (a.packageNames.length != b.packageNames.length)
            return false;
        for (int i = 0; i < a.packageNames.length; i++) {
            if (!TextUtils.equals(a.packageNames[i], b.packageNames[i]))
                return false;
        }
        return true;
    }

    public static AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo rootInActiveWindow, String id) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null)
                    return info;
            }
        }
        return null;
    }

    public static boolean hasNodeForText(AccessibilityNodeInfo rootInActiveWindow, String text, String className) {
        if (rootInActiveWindow == null)
            return false;
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && TextUtils.equals(info.getClassName(), className)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasNodeForId(AccessibilityNodeInfo rootInActiveWindow, String id, String className) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && TextUtils.equals(info.getClassName(), className)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean clickByText(AccessibilityNodeInfo rootInActiveWindow, String text, String className) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && TextUtils.equals(info.getClassName(), className)) {
                    if (info.isClickable())
                        return info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        return false;
    }

    public static boolean clickByText(AccessibilityNodeInfo rootInActiveWindow, String text) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null) {
                    if (info.isClickable())
                        return info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        return false;
    }

    public static boolean clickAllByText(AccessibilityNodeInfo rootInActiveWindow, String text, String className) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        boolean isClicked = false;
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && TextUtils.equals(info.getClassName(), className)) {
                    if (info.isClickable()) {
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        isClicked = true;
                    }
                }
            }
            return isClicked;
        }
        return false;
    }

    public static boolean clickByDescription(AccessibilityNodeInfo rootInActiveWindow, String description, String className) {

        for (int i = 0; i < rootInActiveWindow.getChildCount(); i++) {
            AccessibilityNodeInfo info = rootInActiveWindow.getChild(i);
            if (info != null) {
                if (TextUtils.equals(info.getClassName(), className)) {
                    if (TextUtils.equals(info.getContentDescription(), description)) {
                        if (info.isClickable())
                            return info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
                if (info.getChildCount() > 0) {
                    if (clickByDescription(info, description, className))
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean clickParentByDescription(AccessibilityNodeInfo rootInActiveWindow, String description, String className) {

        for (int i = 0; i < rootInActiveWindow.getChildCount(); i++) {
            AccessibilityNodeInfo info = rootInActiveWindow.getChild(i);
            if (info != null) {
                if (TextUtils.equals(info.getClassName(), className)) {
                    if (TextUtils.equals(info.getContentDescription(), description)) {
                        return clickParent(info);
                    }
                }
                if (info.getChildCount() > 0) {
                    if (clickParentByDescription(info, description, className))
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean clickById(AccessibilityNodeInfo rootInActiveWindow, String id) {
        if (rootInActiveWindow == null)
            return false;
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && info.isClickable())
                    return info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        return false;
    }

    public static boolean clickAllById(AccessibilityNodeInfo rootInActiveWindow, String id) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && info.isClickable())
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            return true;
        }
        return false;
    }

    public static boolean clickAllById(AccessibilityNodeInfo rootInActiveWindow, String id, int count) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo info = infoList.get(i);
                if (info != null && info.isClickable())
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            return true;
        }
        return false;
    }

    public static boolean clickLastOneById(AccessibilityNodeInfo rootInActiveWindow, String id) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            AccessibilityNodeInfo info = infoList.get(infoList.size() - 1);
            if (info != null && info.isClickable())
                return info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return false;
    }

    public static boolean clickLastOneParentById(AccessibilityNodeInfo rootInActiveWindow, String id) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            AccessibilityNodeInfo info = infoList.get(infoList.size() - 1);
            if (info != null)
                return clickParent(info);
        }
        return false;
    }

    public static boolean longClickParentByText(AccessibilityNodeInfo rootInActiveWindow, String text) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && info.getText() != null && info.getText().toString().equals(text)) {
                    longClickParent(info);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean longClickParentById(AccessibilityNodeInfo rootInActiveWindow, String id) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null) {
                    longClickParent(info);
                    return true;
                }
            }
        }
        return false;
    }

    public static void longClickParent(AccessibilityNodeInfo info) {
        AccessibilityNodeInfo parent = info.getParent();
        if (parent != null) {
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            } else
                longClickParent(parent);
        }
    }

    public static boolean clickParentById(AccessibilityNodeInfo rootInActiveWindow, String id) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        for (AccessibilityNodeInfo info : infoList) {
            if (info != null)
                return clickParent(info);
        }
        return false;
    }

    public static boolean performActionById(AccessibilityNodeInfo rootInActiveWindow, String id, int action) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null)
                    return info.performAction(action);
            }
        }
        return false;
    }

    public static boolean clickParentByText(AccessibilityNodeInfo rootInActiveWindow, String text) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null)
                    return clickParent(info);
            }
        }
        return false;
    }

    public static boolean clickParentByText(AccessibilityNodeInfo rootInActiveWindow, String text, String className) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && TextUtils.equals(info.getClassName(), className))
                    return clickParent(info);
            }
        }
        return false;
    }

    public static boolean clickParent(AccessibilityNodeInfo info) {
        if (info == null)
            return false;

        AccessibilityNodeInfo parent = info.getParent();
        if (parent != null) {
            if (parent.isClickable()) {
                return parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                return clickParent(parent);
            }
        }
        return false;
    }

    public static boolean clickRadioButtonParent(AccessibilityNodeInfo rootInActiveWindow, String text) {
        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByText(text);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info.isChecked()) {
                    return true;
                } else {
                    return info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        return false;
    }

    public static boolean pasteTextById(Context context, AccessibilityNodeInfo rootInActiveWindow, String id, String className, String text) {

        List<AccessibilityNodeInfo> infoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        if (infoList != null && infoList.size() > 0) {
            for (AccessibilityNodeInfo info : infoList) {
                if (info != null && TextUtils.equals(info.getClassName(), className)) {
                    return pasteTextByInfo(context, info, text);
                }
            }
        }
        return false;
    }


    public static boolean pasteTextByInfo(Context context, AccessibilityNodeInfo info, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData textCd = ClipData.newPlainText("copy", text);
        clipboard.setPrimaryClip(textCd);
        info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        return info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }
}
