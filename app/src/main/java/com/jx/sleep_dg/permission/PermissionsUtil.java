package com.jx.sleep_dg.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 作者：  王静波
 * 日期：  2018/2/28
 * 注明：
 */

public class PermissionsUtil {

    public static final String TAG = "PermissionGrantor";
    private static HashMap<String, PermissionListener> listenerMap = new HashMap();

    /**
     * 申请授权，当用户拒绝时，会显示默认一个默认的Dialog提示用户
     *
     * @param activity
     * @param listener
     * @param permission
     *            要申请的权限
     */
    public static void requestPermission(Activity activity, PermissionListener listener, String... permission) {
        requestPermission(activity, listener, permission, true, null);
    }

    /**
     * 申请授权，当用户拒绝时，可以设置是否显示Dialog提示用户，也可以设置提示用户的文本内容
     *
     * @param activity
     * @param listener
     * @param permission
     *            需要申请授权的权限
     * @param showTip
     *            当用户拒绝授权时，是否显示提示
     * @param tip
     *            当用户拒绝时要显示Dialog设置
     */
    public static void requestPermission(Activity activity, PermissionListener listener, String[] permission,
                                         boolean showTip, TipInfo tip) {

        if (listener == null) {
            Log.e(TAG, "listener is null");
            return;
        }

        if (Build.VERSION.SDK_INT < 23) {
            if (PermissionsUtil.hasPermission(activity, permission)) {
                listener.permissionGranted(permission);
            } else {
                listener.permissionDenied(permission);
            }
            Log.e(TAG, "API level : " + Build.VERSION.SDK_INT + "不需要申请动态权限!");
            return;
        }

        String key = String.valueOf(System.currentTimeMillis());
        listenerMap.put(key, listener);
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra("permission", permission);
        intent.putExtra("key", key);
        intent.putExtra("showTip", showTip);
        intent.putExtra("tip", tip);
        activity.startActivity(intent);
    }

    /**
     * 判断权限是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermission(Context context, String... permissions) {
        for (String per : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一组授权结果是否为授权通过
     *
     * @param grantResult
     * @return
     */
    public static boolean isGranted(int... grantResult) {
        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGranted1(Context context, String[]... grantResult) {
        // for (int result : grantResult) {
        // if (result != PackageManager.PERMISSION_GRANTED) {
        // return false;
        // }
        // }
        for (int i = 0; i < grantResult.length; i++) {
            for (String per : grantResult[i]) {
                int result = PermissionChecker.checkSelfPermission(context, per);
                if (result != PermissionChecker.PERMISSION_GRANTED) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 跳转到当前应用对应的设置页面
     *
     * @param context
     */
    public static void gotoSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /**
     * @param key
     * @return
     */
    static PermissionListener fetchListener(String key) {
        return listenerMap.remove(key);
    }

    public static class TipInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        String title;
        String content;
        String cancel; // 取消按钮文本
        String ensure; // 确定按钮文本

        public TipInfo(String title, String content, String cancel, String ensure) {
            this.title = title;
            this.content = content;
            this.cancel = cancel;
            this.ensure = ensure;
        }
    }
}

