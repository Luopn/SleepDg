package com.jx.sleep_dg.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.jx.sleep_dg.utils.LogUtil;

/**
 * ble指令通讯
 *
 * @author jzeng
 */
public class BleCommunication {

    /**
     * @param mBLE
     * @param bcWrite
     * @param swtich      发热模式开关
     * @param gear        档位
     * @param temperature 温度
     */
    public static void setDetail(BluetoothLeClass mBLE, BluetoothGattCharacteristic bcWrite, String swtich, String gear,
                                 String temperature) {
        sendData(mBLE, bcWrite, "");
    }

    /**
     * 设置时间
     *
     * @param mBLE
     * @param bcWrite
     * @param time    时间
     */
    public static void setTime(BluetoothLeClass mBLE, BluetoothGattCharacteristic bcWrite, String time) {

    }

    /**
     * 设置场景模式
     *
     * @param mBLE
     * @param bcWrite
     * @param mode    模式
     */
    public static void setMode(BluetoothLeClass mBLE, BluetoothGattCharacteristic bcWrite, String mode) {

    }

    /**
     * 发送数据公用方法
     *
     * @param mBLE
     * @param bcWrite
     * @param data    发送的数据
     */
    public static void sendData(BluetoothLeClass mBLE, BluetoothGattCharacteristic bcWrite, String data) {
        if (bcWrite != null) {
            String str = new String(data.getBytes());
            LogUtil.e("连接:" + data+"   :"+str);
//            bcWrite.setValue(hexStringToBytes(data));
            bcWrite.setValue(data.getBytes());
//            LogUtil.e("输出的数据:" + hexStringToBytes(data));
//            byte[] bytes = hexStringToBytes(data);
//            for (int i = 0; i < bytes.length; i++) {
//                LogUtil.e("ss" + bytes[i]);
//            }
            mBLE.writeCharacteristic(bcWrite);
        }
    }
    /**
     * 发送数据公用方法
     *
     * @param mBLE
     * @param bcWrite
     * @param data    发送的数据
     */
    public static void sendData2(BluetoothLeClass mBLE, BluetoothGattCharacteristic bcWrite, byte[] data) {
        if (bcWrite != null) {
            String str = new String(data);
            LogUtil.e("连接成功:");
//            bcWrite.setValue(hexStringToBytes(data));
            bcWrite.setValue(data);

//            LogUtil.e("输出的数据:" + hexStringToBytes(data));
//            byte[] bytes = hexStringToBytes(data);
//            for (int i = 0; i < bytes.length; i++) {
//                LogUtil.e("ss" + bytes[i]);
//            }
            mBLE.writeCharacteristic(bcWrite);
        }
    }



    public static byte[] hexStringToBytes(String hexString) {
        LogUtil.e("发送的数据:" + hexString);
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
