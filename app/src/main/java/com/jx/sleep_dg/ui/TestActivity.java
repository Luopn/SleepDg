package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleComUtils;

/**
 * Created by Administrator on 2018/7/20.
 */

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_test);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle("指令测试");

        findViewById(R.id.tv_wifi_name).setOnClickListener(this);
        findViewById(R.id.tv_wifi_pwd).setOnClickListener(this);
        findViewById(R.id.tv_time).setOnClickListener(this);
        findViewById(R.id.tv_jiare).setOnClickListener(this);
        findViewById(R.id.tv_chongqi).setOnClickListener(this);
        findViewById(R.id.tv_jiyi).setOnClickListener(this);
        findViewById(R.id.tv_dianji).setOnClickListener(this);
        findViewById(R.id.tv_shengji).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_wifi_name:
                BleComUtils.sendwifiName1();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BleComUtils.sendwifiName2();
                    }
                }, 500);
                break;
            case R.id.tv_wifi_pwd:
                BleComUtils.sendwifiPwd1();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BleComUtils.sendwifiPwd2();
                    }
                }, 500);
                break;
            case R.id.tv_time:
                BleComUtils.sendTime();
                break;
            case R.id.tv_jiare:
//                BleComUtils.sendJiare();
                break;
            case R.id.tv_chongqi:
//                BleComUtils.sendChongqi();
                break;
            case R.id.tv_jiyi:
                BleComUtils.sendJiyi();
                break;
            case R.id.tv_dianji:
//                BleComUtils.senddianji();
                break;
            case R.id.tv_shengji:
                BleComUtils.sendShengji();
                break;
        }
    }

}
