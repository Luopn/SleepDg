package com.jx.sleep_dg.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jx.sleep_dg.MyApplication;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.ble.BluetoothLeClass;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.ble.LeDeviceListAdapter;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.PreferenceUtils;
import com.jx.sleep_dg.utils.ToastUtil;

/**
 * Created by 覃微 on 2018/7/3.
 */

public class SearchActivity extends BaseActivity {

    private boolean isAutoFinish = false;//判断是否点击连接后自动关闭页面
    private String activityFlag;
    private String macAddress;

    protected ListView deviceList;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private static final int FRAGMENT_CITY_MANAGE = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeClass mBLE;

    private Handler mHandler = new Handler();
    //private List<DeviceBean> deviceBeans;
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_search);
        bindView();
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        deviceList.setAdapter(mLeDeviceListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    /**
     * 开始和停止扫描设备
     *
     * @param enable 是否开启
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            //清除设备列表
            mLeDeviceListAdapter.clear();
            //清除保存的MAC
            PreferenceUtils.putString(Constance.MAC, "");
            //断开蓝牙连接
            disConnectBle();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.扫描结果回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.getName() != null) {
                        mLeDeviceListAdapter.addDevice(device);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    @Override
    public void bindView() {
        //deviceBeans = new ArrayList<>();
        activityFlag = getIntent().getStringExtra("flag");//用来区分是从登陆页面跳转过来还是从设备管理页面跳转过来.

        setToolbarTitle(R.string.devie_scan);

        deviceList = (ListView) findViewById(R.id.deviceList);
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        deviceList.setAdapter(mLeDeviceListAdapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isAutoFinish = true;
                macAddress = mLeDeviceListAdapter.getmLeDevices().get(position).getAddress();
                BluetoothLeService.mThis.connect(macAddress);
                showLoadingDialog(R.string.connecting);
            }
        });
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_open_fail, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_open_fail, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // 开启蓝牙
        mBluetoothAdapter.enable();
        mBLE = new BluetoothLeClass(this);
        if (!mBLE.initialize()) {
            finish();
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_DESCRIPOR);
        intentFilter.addAction(BluetoothLeService.ACTION_RSSI_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_RSSI_VALUE);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                ToastUtil.showMessage(R.string.connect_success);

                PreferenceUtils.putString(Constance.MAC, macAddress);

                if (isAutoFinish)
                    finish();
                //new InputDialog(SearchActivity.this, new DialogCallk() {
                //    @Override
                //    public void getDtata(String data) {
                //        name = data;
                //        String token = PreferenceUtils.getString(Constance.TOKEN);
                //        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(name)) {
                //            Map<String, String> map = new HashMap<>();
                //            map.put("name", name);
                //            map.put("url", macAddress);
                //            doHeadPost(InterfaceMethod.BINDDEVICE, map, token);
                //        }
                //    }
                //}, getString(R.string.input_hint), getString(R.string.input_hint));
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.action_do).setTitle(R.string.scan);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_do:
                scanLeDevice(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //断开蓝牙连接
    private void disConnectBle() {
        BluetoothLeService.mThis.disconnect(PreferenceUtils.getString(Constance.MAC));
        BluetoothLeService.mThis.disconnectAll();
        BluetoothLeService.mThis.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && "Login".equals(activityFlag))) {
            LauncherActivity.mBLE.disconnect();
            MyApplication.getInstance().extiApp();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, FRAGMENT_CITY_MANAGE);
    }
}
