package com.jx.sleep_dg.ui;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.ble.BluetoothLeClass;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.utils.LanguageUtil;
import com.jx.sleep_dg.utils.PreferenceUtils;

import java.util.Locale;

/**
 * 启动界面
 */
public class LauncherActivity extends BaseActivity {

    private boolean isOpened;
    private ImageView ivMain;
    private Runnable mOpenRunnable;
    /**
     * 读写BLE终端
     */
    public static BluetoothLeClass mBLE;
    public static BluetoothGattCharacteristic bcWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleLayoutVisiable(false);
        bindView();
        isOpened = false;
        //设置语言
        Locale locale = (Locale) PreferenceUtils.deSerialization(PreferenceUtils.getString(LanguageUtil.LANGUAGE));
        if (locale != null) {
            LanguageUtil.changeAppLanguage(this, locale, true);
        }
    }

    @Override
    public void bindView() {
        ivMain = new ImageView(this);
        ivMain.setImageResource(R.mipmap.ic_launch);
        ivMain.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(ivMain);
        startService(new Intent(this, BluetoothLeService.class));

        ivMain.postDelayed(mOpenRunnable = new Runnable() {
            @Override
            public void run() {
                openNextPage();
            }
        }, 1500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ivMain.removeCallbacks(mOpenRunnable);
        openNextPage();
    }

    //打开下一个页面
    private void openNextPage() {
        if (isOpened) return;
        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        isOpened = true;
    }
}
