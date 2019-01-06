/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jx.sleep_dg.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.PreferenceUtils;
import com.jx.sleep_dg.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {

    private static final String TAG = "BluetoothLeService";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String ACTION_WRITE_DESCRIPOR = "com.example.bluetooth.le.ACTION_WRITE_DESCRIPOR";
    public final static String EXTRA_DATA_ADDRESS = "com.example.bluetooth.le.EXTRA_DATA_ADDRESS";
    public final static String EXTRA_GATT_DEVICES = "com.example.bluetooth.le.EXTRA_DATA_DEVICE";
    public final static String ACTION_RSSI_AVAILABLE = "com.example.bluetooth.le.ACTION_RSSI_AVAILABLE";
    public final static String ACTION_RSSI_VALUE = "com.example.bluetooth.le.ACTION_RSSI_VALUE";
    public static final String ACTION_RESCAN = "com.example.bluetooth.le.RESCAN";

    public static BluetoothLeService mThis = null;
    private final IBinder mBinder = new LocalBinder();
    private Handler handler = new Handler();

    private MSPProtocol mspProtocol = MSPProtocol.getInstance();

    public boolean isBeep = true;//是否发送心跳包数据
    private String lastConnectAddress = "";
    private BluetoothGatt gatts;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothGatt> mBluetoothGatts = new ArrayList<BluetoothGatt>();

    public String getLastDevice() {
        return lastConnectAddress;
    }

    Timer connectTimer = new Timer(true);//定时连接

    TimerTask connectTimerTask = new TimerTask() {
        public void run() {
            lastConnectAddress = PreferenceUtils.getString(Constance.MAC);
            if (!TextUtils.isEmpty(lastConnectAddress))
                connect(lastConnectAddress);
            //心跳包发送逻辑：APP打开，没发送控制命令时，每5秒发一次这命令。发送控制命令后，过5秒再发送
            if (isBeep) {
                BleComUtils.sendBeepData();
            } else {
                isBeep = true;
            }
        }
    };

    // Implements callback methods for GATT events that the app cares about. For
    // example, connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (BluetoothGatt.GATT_SUCCESS == status) {
                String address = gatt.getDevice().getAddress();
                broadcastUpdate(ACTION_WRITE_DESCRIPOR, address);
                Log.i(TAG, address + "onDescriptorWrite");
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                ToastUtil.showMessage("蓝牙已连接");
                intentAction = ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction, gatt.getDevice().getAddress());

                //发送用户数据到设备
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BleComUtils.sendTime("F10100000001");
                    }
                },1000);
                //发送自动补气时间
                int hour = PreferenceUtils.getInt(Constance.KEY_INFLATION_HOUR, -1);
                int minute = PreferenceUtils.getInt(Constance.KEY_INFLATION_MINUTE, -1);
                //if (hour >= 0 && minute >= 0)
                //    BleComUtils.sendInflation(hour, minute);

                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:(" + gatt.getDevice().getAddress() + ")"
                        + gatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                ToastUtil.showMessage("蓝牙断开");
                if (!TextUtils.isEmpty(lastConnectAddress)) {
                    lastConnectAddress = gatt.getDevice().getAddress();
                    broadcastUpdate(ACTION_RSSI_AVAILABLE);
                }
                intentAction = ACTION_GATT_DISCONNECTED;
                broadcastUpdate(intentAction, gatt.getDevice().getAddress());
                Log.i(TAG, "Disconnected from GATT server.");
                gatt.disconnect();
                close(gatt);
                mBluetoothGatts.remove(gatt);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String address = gatt.getDevice().getAddress();
                bindCharas(address, getSupportedGattServices(address));
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, address);
            } else {
                broadcastUpdate(ACTION_GATT_DISCONNECTED, gatt.getDevice().getAddress());
                gatt.disconnect();
                close(gatt);
                mBluetoothGatts.remove(gatt);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, gatt.getDevice().getAddress(), characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, gatt.getDevice().getAddress(), characteristic);
            byte[] value = characteristic.getValue();
            //解析数据
            mspProtocol.setRawBytes(value);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String value = BleUtils.bytesToHexString(characteristic.getValue());
            Log.i(TAG, "onCharacteristicWrite: " + value);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView
    // on the UI.
    private void bindCharas(String address, List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                String uuid_str = gattCharacteristic.getUuid().toString();
                if (uuid_str.equalsIgnoreCase(SampleGattAttributes.SMART_TAG_WRITE_UUID)) {
                    BluetoothGattCharacteristic mWriteCharacteristic = gattCharacteristic;
                }
                if (uuid_str.equalsIgnoreCase(SampleGattAttributes.SMART_TAG_READ_UUID)) {
                    BluetoothGattCharacteristic mReadCharacteristic = gattCharacteristic;
                    setCharacteristicNotification(address, gattCharacteristic, true);
                }
            }
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {

        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        Log.i(TAG, "Connect :" + address);
        if (mBluetoothAdapter == null || TextUtils.isEmpty(address)) {
            Log.i(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        if (mBluetoothGatts != null && mBluetoothGatts.size() > 0) {
            for (BluetoothGatt bg : mBluetoothGatts) {
                if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                    Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
                    if (mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT) == STATE_CONNECTED) {
                        Log.i("TAG", mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT) + "");
                        return true;
                    } else {
                        if (bg.connect()) {
                            ToastUtil.showMessage(getResources().getString(R.string.connect) + ":" + address);
                            return true;
                        } else {
                            bg.disconnect();
                            bg.close();
                            mBluetoothGatts.remove(bg);
                            break;
                        }
                    }
                }
            }
        }

        gatts = device.connectGatt(BluetoothLeService.this, false, mGattCallback);
        if (gatts != null)
            mBluetoothGatts.add(device.connectGatt(BluetoothLeService.this, false, mGattCallback));
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(String address) {
        if (mBluetoothAdapter != null && gatts != null)
            gatts.disconnect();
        if (mBluetoothAdapter == null || mBluetoothGatts == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                bg.disconnect();
            }
        }
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public void close() {
        if (gatts != null)
            gatts.close();
        for (BluetoothGatt bg : mBluetoothGatts) {
            close(bg);
        }
        mBluetoothGatts.clear();
    }

    public void close(BluetoothGatt bg) {
        if (bg == null) {
            return;
        }
        bg.close();
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGatt bg, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || bg == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bg.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(String address, BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {

        BluetoothGatt mBluetoothGatt = null;
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                mBluetoothGatt = bg;
            }
        }

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        if (SampleGattAttributes.SMART_TAG_READ_UUID.equalsIgnoreCase(characteristic.getUuid().toString())) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    // public List<BluetoothGattService> getSupportedGattServices() {
    // if (mBluetoothGatts == null) return null;
    // return mBluetoothGatts.getServices();
    // }
    //
    public List<BluetoothGattService> getSupportedGattServices(String address) {
        BluetoothGatt mBluetoothGatt = null;
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                mBluetoothGatt = bg;
            }
        }
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            broadcastUpdate(ACTION_GATT_DISCONNECTED, address);
            return null;
        }
        if (mBluetoothGatts == null)
            return null;
        return mBluetoothGatt.getServices();
    }

    public void writeCMD(String address, byte[] cmd) {
        final StringBuilder stringBuilder = new StringBuilder(cmd.length);
        for (byte byteChar : cmd)
            stringBuilder.append(String.format("%02X ", byteChar));
        Log.i(TAG, "Write CMD:" + stringBuilder.toString());

        BluetoothGatt mBluetoothGatt = null;
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                mBluetoothGatt = bg;
            }
        }

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            broadcastUpdate(ACTION_GATT_DISCONNECTED, address);
            return;
        }

        BluetoothGattCharacteristic characteristic = getWriteChara(address);
        if (characteristic != null) {
            characteristic.setValue(cmd);
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    public void writeCMD(byte[] cmd) {
        final StringBuilder stringBuilder = new StringBuilder(cmd.length);
        for (byte byteChar : cmd)
            stringBuilder.append(String.format("%02X ", byteChar));
        Log.i(TAG, "Write CMD:" + stringBuilder.toString());
        if (cmd.length >= 3 && cmd[2] != (byte) 0xC9) {
            isBeep = false;
        }
        BluetoothGatt mBluetoothGatt = null;
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(lastConnectAddress)) {
                mBluetoothGatt = bg;
            }
        }

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            broadcastUpdate(ACTION_GATT_DISCONNECTED, lastConnectAddress);
            return;
        }

        BluetoothGattCharacteristic characteristic = getWriteChara(lastConnectAddress);
        if (characteristic != null) {
            boolean write = characteristic.setValue(cmd);
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    //读取数据
    public void readChars() {
        BluetoothGatt mBluetoothGatt = null;
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(lastConnectAddress)) {
                mBluetoothGatt = bg;
            }
        }

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            broadcastUpdate(ACTION_GATT_DISCONNECTED, lastConnectAddress);
            return;
        }

        BluetoothGattCharacteristic characteristic = getReadChara(lastConnectAddress);
        if (characteristic != null) {
            mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public BluetoothGattCharacteristic getReadChara(String address) {
        BluetoothGatt mBluetoothGatt = null;
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                mBluetoothGatt = bg;
            }
        }

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            broadcastUpdate(ACTION_GATT_DISCONNECTED, address);
            return null;
        }

        String uuid = null;
        for (BluetoothGattService gattService : mBluetoothGatt.getServices()) {
            uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equalsIgnoreCase(SampleGattAttributes.SMART_TAG_READ_UUID)) {
                    return gattCharacteristic;
                }
            }
        }
        return null;
    }

    public BluetoothGattCharacteristic getWriteChara(String address) {
        BluetoothGatt mBluetoothGatt = null;
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                mBluetoothGatt = bg;
            }
        }

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            broadcastUpdate(ACTION_GATT_DISCONNECTED, address);
            return null;
        }

        String uuid = null;
        for (BluetoothGattService gattService : mBluetoothGatt.getServices()) {
            uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equalsIgnoreCase(SampleGattAttributes.SMART_TAG_WRITE_UUID)) {
                    return gattCharacteristic;
                }
            }
        }
        return null;
    }

    //各种广播
    private void broadcastUpdate(final String action, int rssi) {
        final Intent intent = new Intent(action);
        intent.putExtra("rssi", rssi);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, String address) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA_ADDRESS, address);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, BluetoothDevice device) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_GATT_DEVICES, device);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String address,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.i(TAG, stringBuilder.toString());
        }
        if (data != null && data.length > 0) {
            intent.putExtra(EXTRA_DATA, data);
            intent.putExtra(EXTRA_DATA_ADDRESS, address);
            sendBroadcast(intent);
        }
    }

    public void getRssi(String address) {
        if (mBluetoothAdapter == null || mBluetoothGatts == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(address)) {
                bg.readRemoteRssi();
            }
        }
    }

    public boolean isConnected(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return false;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg.getDevice().getAddress().equalsIgnoreCase(mac)) {
                return mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT) == STATE_CONNECTED;
            }
        }
        return false;
    }

    public void disconnectAll() {
        if (mBluetoothAdapter == null || mBluetoothGatts == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        for (BluetoothGatt bg : mBluetoothGatts) {
            if (bg != null) {
                bg.disconnect();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connectTimer.schedule(connectTimerTask, 10, 5 * 1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        mThis = this;
        broadcastUpdate(ACTION_RESCAN);// first scan
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mspProtocol.setDataTrdRun(false);

        disconnect(lastConnectAddress);
        disconnectAll();

        if (connectTimer != null)
            connectTimer.cancel();
    }
}
