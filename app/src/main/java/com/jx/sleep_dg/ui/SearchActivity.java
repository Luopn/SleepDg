package com.jx.sleep_dg.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BluetoothLeClass;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.ble.LeDeviceListAdapter;
import com.jx.sleep_dg.http.InterfaceMethod;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.MyApplication;
import com.jx.sleep_dg.utils.PreferenceUtils;
import com.jx.sleep_dg.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * Created by 覃微 on 2018/7/3.
 */

public class SearchActivity extends BaseActivity {

    private boolean isAutoFinish = false;//判断是否点击连接后自动关闭页面
    private String activityFlag;
    private String macAddress;

    protected ListView deviceList;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private RxPermissions permissions;

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
        permissions = new RxPermissions(this);
        Permission();
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
    public void onNetJSONObject(JSONObject jsonObject, String trxcode) {
        super.onNetJSONObject(jsonObject, trxcode);
        if (InterfaceMethod.BINDDEVICE.equals(trxcode)) {
            ToastUtil.showMessage(R.string.bind_success);
            if (activityFlag.equals("Login")) {
                // PreferenceUtils.putString(Constance.ADDRESS, macAddress);
                // PreferenceUtils.putString(Constance.BLE_NAME, name);
                getNetData();
                //PreferenceUtils.putString(Constance.BLE_ID,js.getString("id"));
                //Intent intent = new Intent();
                //intent.setClass(SearchDeviceActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
            } else {
                setResult(200);
                finish();
            }
        }
    }

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

    /**
     * 提交到服务器
     */
    private void getNetData() {
        Map<String, String> map = new HashMap<>();
        String token = PreferenceUtils.getString(Constance.TOKEN);
        if (!TextUtils.isEmpty(token)) {
            doHeadPost(InterfaceMethod.GETALLDEVICE, map, token);
        }
    }

    @Override
    public void onNetJSONArray(JSONArray jsonArray, String trxcode) {
        super.onNetJSONArray(jsonArray, trxcode);
        if (InterfaceMethod.GETALLDEVICE.equals(trxcode)) {
            // if (deviceBeans != null) {
            //     deviceBeans.clear();
            // }
            for (int k = 0; k < jsonArray.length(); k++) {
                try {
                    JSONObject js = jsonArray.getJSONObject(k);
                    //  DeviceBean deviceBean = new DeviceBean();
                    //  deviceBean.setDevicename(js.getString("name"));
                    //  deviceBean.setIsUse(js.getInt("isUse"));
                    //  deviceBean.setId(js.getInt("id"));
                    //  deviceBean.setUserid(js.getInt("user_id"));
                    //  deviceBean.setMacaddress(js.getString("url"));
                    if (macAddress.equals(js.getString("url"))) {
                        PreferenceUtils.putString(Constance.ADDRESS, js.getString("url"));
                        PreferenceUtils.putString(Constance.BLE_NAME, js.getString("name"));
                        PreferenceUtils.putString(Constance.BLE_ID, js.getInt("id") + "");
                        Intent intent = new Intent();
                        intent.setClass(SearchActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    //  deviceBeans.add(deviceBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage(R.string.add_error);
                }
            }
        }
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

    @SuppressLint("CheckResult")
    public void Permission() {
        permissions.request(Manifest.permission.ACCESS_COARSE_LOCATION).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (!aBoolean)
                    showDialogTipUserGoToAppSettting();
            }
        });
    }

    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSettting() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.help)
                .setMessage(R.string.string_help_text)
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, FRAGMENT_CITY_MANAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FRAGMENT_CITY_MANAGE) {
            Permission();
        }
    }
}
