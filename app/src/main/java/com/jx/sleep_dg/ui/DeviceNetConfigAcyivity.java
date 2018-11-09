package com.jx.sleep_dg.ui;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.event.ConfigureResEvent;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.utils.PrintLog;
import com.jx.sleep_dg.utils.ToastUtil;
import com.jx.sleep_dg.view.RippleBackground;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 设备升降
 */

public class DeviceNetConfigAcyivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "DeviceNetConfigAcyivity";

    private WifiManager mWifiManager;

    private RippleBackground rippleBackground;
    private Button sendBtn;
    private AutoCompleteTextView wifiName, wifiPwd;

    private String ssid, pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_device_net_config);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        EventBus.getDefault().register(this);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle("设备配网");
        wifiName = (AutoCompleteTextView) findViewById(R.id.wifi_name);
        wifiPwd = (AutoCompleteTextView) findViewById(R.id.wifi_pwd);
        rippleBackground = (RippleBackground) findViewById(R.id.sendback);
        sendBtn = (Button) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
        wifiName.setText(mWifiManager.getConnectionInfo().getSSID().replace("\"", ""));
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);

    }

    //开始配网
    private void startConfiguration(final String ssid, final String pwd) {

        byte[] cmd = BleComUtils.a2c_SendSSID(ssid).get(0);
        BluetoothLeService.mThis.writeCMD(cmd);//先发送SSID第一个包
        PrintLog.d(cmd, cmd.length);

        rippleBackground.startRippleAnimation();
        sendBtn.setEnabled(false);
        rippleBackground.postDelayed(new Runnable() {
            @Override
            public void run() {
                rippleBackground.stopRippleAnimation();
                sendBtn.setEnabled(true);
            }
        }, 8000);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.send_btn:
                ssid = wifiName.getText().toString();
                pwd = wifiPwd.getText().toString();
                startConfiguration(ssid, pwd);
                break;
        }
    }

    //监听EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigureRes(ConfigureResEvent event) {
        int orderID = event.getOrderID();
        int orderSerial = event.getOrderSerial();
        int orderRes = event.getOrderRes();
        //WiFi SSID发送情况
        if (orderID == 0x21) {
            if (orderSerial == 1) {
                if (orderRes == 1) {
                    byte[] cmd = BleComUtils.a2c_SendSSID(ssid).get(1);
                    BluetoothLeService.mThis.writeCMD(cmd);//发送SSID第二个包
                    PrintLog.d(cmd, cmd.length);
                } else {
                    ToastUtil.showMessage("发送失败，请重试");
                    rippleBackground.stopRippleAnimation();
                    sendBtn.setEnabled(true);
                }
            }
            if (orderSerial == 2) {
                if (orderRes == 1) {
                    byte[] cmd = BleComUtils.a2c_SendWiFiPwd(pwd).get(0);
                    BluetoothLeService.mThis.writeCMD(cmd);//发送密码第一个包
                    PrintLog.d(cmd, cmd.length);
                    BluetoothLeService.mThis.writeCMD(cmd);
                } else {
                    ToastUtil.showMessage("发送失败，请重试");
                    rippleBackground.stopRippleAnimation();
                    sendBtn.setEnabled(true);
                }
            }
        }
        //WiFi 密码发送情况
        if (orderID == 0x22) {
            if (orderSerial == 1) {
                if (orderRes == 1) {
                    byte[] cmd = BleComUtils.a2c_SendWiFiPwd(pwd).get(1);
                    BluetoothLeService.mThis.writeCMD(cmd);//发送密码第二个包
                    PrintLog.d(cmd, cmd.length);
                } else {
                    ToastUtil.showMessage("发送失败，请重试");
                    rippleBackground.stopRippleAnimation();
                    sendBtn.setEnabled(true);
                }
            }
            if (orderSerial == 2) {
                if (orderRes == 1) {
                    ToastUtil.showMessage("发送成功");
                    sendBtn.setEnabled(true);
                    rippleBackground.stopRippleAnimation();
                } else {
                    ToastUtil.showMessage("发送失败，请重试");
                    sendBtn.setEnabled(true);
                    rippleBackground.stopRippleAnimation();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
