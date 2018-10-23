package com.jx.sleep_dg.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.jx.sleep_dg.Bean.HeartRateBean;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.ble.BluetoothLeClass;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.ble.SampleGattAttributes;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.utils.PreferenceUtils;
import com.jx.sleep_dg.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 启动界面
 */
public class LauncherActivity extends BaseActivity
        implements BluetoothLeClass.OnServiceDiscoverListener, BluetoothLeClass.OnDataAvailableListener,
        BluetoothLeClass.OnConnectListener, BluetoothLeClass.OnDisconnectListener {

    private ImageView ivMain;
    private Runnable mOpenRunnable;

    private int leftXinlv;
    private int leftHuxi;

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
    }

    @Override
    public void bindView() {
        ivMain = new ImageView(this);
        ivMain.setImageResource(R.mipmap.bg_enrol);
        ivMain.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(ivMain);
        startService(new Intent(this, BluetoothLeService.class));

        ivMain.postDelayed(mOpenRunnable = new Runnable() {
            @Override
            public void run() {
                openNextPage();
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ivMain.removeCallbacks(mOpenRunnable);
        openNextPage();
    }

    //打开下一个页面
    private void openNextPage() {
        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void bindBle() {
        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        /*
         * 搜索BLE终端
         */
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "不支持", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // 开启蓝牙
        mBluetoothAdapter.enable();

        mBLE = new BluetoothLeClass(this);
        if (!mBLE.initialize()) {
            LogUtil.e("Unable to initialize Bluetooth");
            finish();
        }
        // 发现BLE终端的Service时回调
        mBLE.setOnServiceDiscoverListener(this);
        // 收到BLE终端数据交互的事件
        mBLE.setOnDataAvailableListener(this);
        mBLE.setOnConnectListener(this);
        mBLE.setOnDisconnectListener(this);
        String macAddress = PreferenceUtils.getString(Constance.MAC);
        if (!TextUtils.isEmpty(macAddress)) {
            mBLE.connect(macAddress);
        }
    }

    @Override
    public void onServiceDiscover(BluetoothGatt gatt) {
        displayGattServices(mBLE.getSupportedGattServices());
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    public static void ConnFect(String address) {
        if (mBLE != null) {
//            mBLE.disconnect();
//            mBLE.close();
            mBLE.connect(address);
            LogUtil.e("正在连接的地址" + address);
        } else {
            LogUtil.e("为空");
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        // 写数据
        String value = BleUtils.bytesToHexString(characteristic.getValue());
//        String time = TimeUtil.hexStringToString(value);
//        LogUtil.e("获取的数据:" + value);
        if (value.substring(4, 6).equals("24")) {
            ToastUtil.showMessage("加热指令发送成功");
        } else if (value.substring(4, 6).equals("25")) {
            ToastUtil.showMessage("充气指令发送成功");
        } else if (value.substring(2, 6).equals("0827")) {
            ToastUtil.showMessage("升降指令发送成功");
        } else if (value.substring(2, 8).equals("123101")) {
//            if (leftIndex < 10) {
//                Calendar cal = Calendar.getInstance();
//                int minute = cal.get(Calendar.MINUTE);
//                int second = cal.get(Calendar.SECOND);


//            HeartRateBean bean = new HeartRateBean();
            leftHuxi = BleUtils.HexToInt(value.substring(26, 28));//左呼吸频率
            leftXinlv = BleUtils.HexToInt(value.substring(24, 26));//左心率
//                bean.setTime(minute + ":" + second);//时间
//                hList.add(bean);
////                LogUtil.e("呼吸：" + value.substring(26, 28) + " 10进制数：" + BleUtils.HexToInt(value.substring(26, 28)) + "  心率：" + value.substring(24, 26) + " 10进制数：" + BleUtils.HexToInt(value.substring(24, 26)));
//                leftIndex++;
//            } else {
//                LogUtil.e(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                leftIndex = 0;
//            broadcastDataUpdate(BleUtils.LEFT_VINLV, bean);
//                EventBus.getDefault().post(new EveXinlvBean(EveBusUtil.LeftXinlv, hList));
//                hList.clear();
//            }

        } else if (value.substring(2, 8).equals("143103")) {
//            if (rightIndex < 10) {
//                Calendar cal = Calendar.getInstance();
//                int minute = cal.get(Calendar.MINUTE);
//                int second = cal.get(Calendar.SECOND);


            HeartRateBean bean = new HeartRateBean();
            bean.setRightHuxi(BleUtils.HexToInt(value.substring(12, 14)));//右呼吸频率
            bean.setRightXinlv(BleUtils.HexToInt(value.substring(10, 12)));//右心率
            bean.setLeftHuxi(leftHuxi);//左呼吸频率
            bean.setLeftXinlv(leftXinlv);//左心率
//                bean.setTime(minute + ":" + second);//时间
//                hList.add(bean);
////                LogUtil.e("呼吸：" + value.substring(26, 28) + " 10进制数：" + BleUtils.HexToInt(value.substring(26, 28)) + "  心率：" + value.substring(24, 26) + " 10进制数：" + BleUtils.HexToInt(value.substring(24, 26)));
//                rightIndex++;
//            } else {
////                LogUtil.e("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//                rightIndex = 0;
            broadcastDataUpdate(BleUtils.LEFT_VINLV, bean);
//            broadcastDataUpdate(BleUtils.RIGHT_VINLV, bean);
            leftHuxi = 0;
            leftXinlv = 0;
//                hList.clear();
//                EventBus.getDefault().post(new EveXinlvBean(EveBusUtil.RightXinlv, hList));
//            }
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;

        for (BluetoothGattService gattService : gattServices) {
            // -----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                // UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
                LogUtil.e("data:" + "--------->" + gattCharacteristic.getUuid().toString());
                if (gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.SMART_TAG_READ_UUID)) {
                    //电量接收
                    BluetoothGattCharacteristic bcRead = gattCharacteristic;
                    mBLE.setCharacteristicNotification(bcRead, true);
                } else if (gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.SMART_TAG_WRITE_UUID)) {
                    LogUtil.e("dataddd:" + "--------->" + gattCharacteristic.getUuid().toString());
                    bcWrite = gattCharacteristic;
                }
            }
        }
    }

    public void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LogUtil.e("发送广播" + action);
        sendBroadcast(intent);
    }

    private void broadcastDataUpdate(final String action, HeartRateBean bean) {
        final Intent intent = new Intent(action);
        intent.putExtra("data", bean);
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            finish();
        }
    }

    @Override
    public void onConnect(BluetoothGatt gatt) {
        broadcastUpdate(BleUtils.DEVICE_CONNET_OK);
        ToastUtil.showMessage(gatt.getDevice().getName() + "已连接");
        LogUtil.e("连接" + gatt.getDevice().getAddress() + "已连接");
    }

    @Override
    public void onDisconnect(BluetoothGatt gatt) {
        broadcastUpdate(BleUtils.DEVICE_CONNET_DISCONNECT);
        ToastUtil.showMessage("断开连接");
        LogUtil.e("连接" + gatt.getDevice().getAddress() + "断开");
    }

    private void updateLange(Locale locale) {
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBLE != null) {
            mBLE.disconnect();
            mBLE.close();
            mBLE = null;
        }
    }
}
