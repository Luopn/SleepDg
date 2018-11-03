package com.jx.sleep_dg;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.jx.sleep_dg.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：  王静波
 * 日期：  2018/2/27
 * 注明：
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private List<Activity> activities;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        activities = new ArrayList<Activity>();
        //相机7.0新特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public static Context getContext() {
        synchronized (MyApplication.class) {
            return instance.getApplicationContext();
        }
    }

    public static MyApplication getInstance() {
        if (instance == null) {
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public void extiApp() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    public void extiLoginApp() {
        for (Activity activity : activities) {
            LogUtil.e("名称:" + activity.getClass().getName());
            if (!activity.getClass().getName().equals("com.jx.hitommovement.ui.LauncherActivity")) {
                activity.finish();
            }
        }
    }
}
