package com.jx.sleep_dg.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jx.sleep_dg.ble.BluetoothLeService;

import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

/**
 * Created by 覃微 on 2018/6/6.
 */

public abstract class BaseMainFragment extends SupportFragment implements View.OnClickListener {

    private BroadcastReceiver receiver;//接收蓝牙广播

    protected abstract int getLayoutId();

    public abstract void onBindView(View view);

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

    //更新UI数据
    protected void notifyBleDataChanged(Intent intent) {
    }

    protected void notifyDeviceDisconnected() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (getLayoutId() != 0) {
            view = inflater.inflate(getLayoutId(), container, false);
            onBindView(view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _mActivity.unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {

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
}
