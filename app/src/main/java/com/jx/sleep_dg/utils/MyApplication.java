package com.jx.sleep_dg.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

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
        //蓝牙相关配置修改
//        ViseBle.config()
//                .setScanTimeout(10)//扫描超时时间，这里设置为永久扫描
//                .setConnectTimeout(10 * 1000)//连接超时时间
//                .setOperateTimeout(5 * 1000)//设置数据操作超时时间
//                .setConnectRetryCount(3)//设置连接失败重试次数
//                .setConnectRetryInterval(1000)//设置连接失败重试间隔时间
//                .setOperateRetryCount(3)//设置数据操作失败重试次数
//                .setOperateRetryInterval(1000)//设置数据操作失败重试间隔时间
//                .setMaxConnectCount(5);//设置最大连接设备数量
//                    //蓝牙信息初始化，全局唯一，必须在应用初始化时调用
//        ViseBle.getInstance().init(this);
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
