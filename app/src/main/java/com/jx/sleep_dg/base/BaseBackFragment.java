package com.jx.sleep_dg.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jx.sleep_dg.ble.BluetoothLeService;

import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

public class BaseBackFragment extends SwipeBackFragment {

    private BroadcastReceiver receiver;//接收蓝牙广播

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //监听蓝牙状态
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyBleDataChanged(intent);
                        }
                    });
                } else if (intent.getAction() != null && intent.getAction().equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDeviceDisconnected();
                        }
                    });
                }
            }
        };
        _mActivity.registerReceiver(receiver, makeGattUpdateIntentFilter());
    }

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setParallaxOffset(0.5f);
    }

    //更新UI数据
    protected void notifyBleDataChanged(Intent intent) {
    }

    protected void notifyDeviceDisconnected() {

    }

    //蓝牙数据相关
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _mActivity.unregisterReceiver(receiver);
    }
}
