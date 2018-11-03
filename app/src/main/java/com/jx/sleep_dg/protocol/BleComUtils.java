package com.jx.sleep_dg.protocol;


import android.text.TextUtils;

import com.jx.sleep_dg.ble.BleCommunication;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.ui.LauncherActivity;
import com.jx.sleep_dg.utils.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 作者：丁涛
 * 日期：2018/7/13.
 */

public class BleComUtils {

    /**
     * 发送wifi
     *
     * @param name
     * @param pwd
     */
    //public static void sendWifi(String name, String pwd) {
    //    BleCommunication.sendData(LauncherActivity.mBLE, LauncherActivity.bcWrite, "AT+CWJAP_DEF=" + "\"" + name + "\"" + ",\"" + pwd + "\"" + "\r\n");
    //}

    /**
     * 发送ip 端口
     */
    //public static void sendIP(String ipAddress, String port) {
    //    BleCommunication.sendData(LauncherActivity.mBLE, LauncherActivity.bcWrite, "AT+CIPSTART=\"TCP\",\"" + ipAddress + "\"," + port + "\r\n");
    //}

    /**
     * WiFi SSID
     *
     * @param ssid wifi名
     * @return 两包数据
     */
    public static ArrayList<byte[]> a2c_SendSSID(String ssid) {

        ArrayList<byte[]> list = new ArrayList<>(2);

        byte[] srcBytes = ssid.getBytes();
        byte[] data1 = new byte[20];
        data1[0] = (byte) 0xAA;
        data1[1] = (byte) 0x14;
        data1[2] = (byte) 0xC1;
        data1[3] = (byte) 0x01;
        if (srcBytes.length <= 15)
            System.arraycopy(srcBytes, 0, data1, 4, srcBytes.length);
        else
            System.arraycopy(srcBytes, 0, data1, 4, 15);
        data1[19] = checkSum(data1);
        list.add(data1);

        byte[] data2 = new byte[20];
        data2[0] = (byte) 0xAA;
        data2[1] = (byte) 0x14;
        data2[2] = (byte) 0xC1;
        data2[3] = (byte) 0x02;
        if (srcBytes.length > 15 && srcBytes.length <= 30)
            System.arraycopy(srcBytes, 15, data2, 4, srcBytes.length - 15);
        data2[19] = checkSum(data2);
        list.add(data2);

        return list;
    }


    /**
     * WiFi 密码
     *
     * @param wifiPwd wifi密码
     * @return 两包数据
     */
    public static ArrayList<byte[]> a2c_SendWiFiPwd(String wifiPwd) {

        ArrayList<byte[]> list = new ArrayList<>(2);

        byte[] srcBytes = wifiPwd.getBytes();
        byte[] data1 = new byte[20];
        data1[0] = (byte) 0xAA;
        data1[1] = (byte) 0x14;
        data1[2] = (byte) 0xC2;
        data1[3] = (byte) 0x01;
        if (srcBytes.length <= 15)
            System.arraycopy(srcBytes, 0, data1, 4, srcBytes.length);
        else
            System.arraycopy(srcBytes, 0, data1, 4, 15);
        data1[19] = checkSum(data1);
        list.add(data1);

        byte[] data2 = new byte[20];
        data2[0] = (byte) 0xAA;
        data2[1] = (byte) 0x14;
        data2[2] = (byte) 0xC2;
        data2[3] = (byte) 0x02;
        if (srcBytes.length > 15 && srcBytes.length <= 30)
            System.arraycopy(srcBytes, 15, data2, 4, srcBytes.length - 15);
        data2[19] = checkSum(data2);
        list.add(data2);

        return list;
    }


    /**
     * 发送数据
     */
    public static void sendJiare(String data) {
        LogUtil.e("data:" + data);
        String yihuo = BleUtils.XORAnd("05C401");
        // String data = "aa05c401";
        // byte[] data = new byte[4];
        // data[0] = (byte) 0xaa;
        // data[1] = (byte) 0x05;
        // data[2] = (byte) 0xC4;
        // data[3] = (byte) 0x01;
        // BleCommunication.sendData2(LauncherActivity.mBLE, LauncherActivity.bcWrite, data);
        BluetoothLeService.mThis.writeCMD(toByteArray("aa05c4" + data));
    }

    public static void sendChongqi(String data) {
        // String yihuo = BleUtils.XORAnd("06C50A0A");
        // String data = "aa06a50a0a";
        // BleCommunication.sendData(LauncherActivity.mBLE, LauncherActivity.bcWrite, data);
        // byte[] a = new byte[5];
        // a[0] = (byte) 0xaa;
        // a[1] = (byte) 0x06;
        // a[2] = (byte) 0xC6;
        // a[3] = (byte) 0x01;
        // a[4] = (byte) 0x01;
        BluetoothLeService.mThis.writeCMD(toByteArray("aa06c5" + data));
    }

    public static void sendMoShi(String data) {
        LogUtil.e("数据：" + data);
        //String yihuo = BleUtils.XORAnd("06C50A0A");
        //String data = "aa06a50a0a";
        //BleCommunication.sendData(LauncherActivity.mBLE, LauncherActivity.bcWrite, data);

        //byte[] a = new byte[5];
        //a[0] = (byte) 0xaa;
        //a[1] = (byte) 0x06;
        //a[2] = (byte) 0xC6;
        //a[3] = (byte) 0x01;
        //a[4] = (byte) 0x01;
        BluetoothLeService.mThis.writeCMD(toByteArray("aa0ac8" + data + "0000000000"));
        //BleCommunication.sendData2(LauncherActivity.mBLE, LauncherActivity.bcWrite, toByteArray("aa0ac8" + data + "0000000000"));
    }

    public static void sendJiyi() {
        String yihuo = BleUtils.XORAnd("06C60101");
        //String data = "AA06C60101";
        byte[] data = new byte[5];
        data[0] = (byte) 0xaa;
        data[1] = (byte) 0x06;
        data[2] = (byte) 0xc6;
        data[3] = (byte) 0x01;
        data[4] = (byte) 0x01;
        BluetoothLeService.mThis.writeCMD(data);
    }

    public static void senddianji(String data) {

        String yihuo = BleUtils.XORAnd("06C702040608");
        // String data = "AA06C702040608";
        LogUtil.e("data:" + data);
        //byte[] data = new byte[7];
        //data[0] = (byte) 0xaa;
        //data[1] = (byte) 0x06;
        //data[2] = (byte) 0xc7;
        //data[3] = (byte) 0x02;
        //data[4] = (byte) 0x04;
        //data[5] = (byte) 0x06;
        //data[6] = (byte) 0x08;
        //BleCommunication.sendData2(LauncherActivity.mBLE, LauncherActivity.bcWrite, toByteArray("aa08c7" + data));
        BluetoothLeService.mThis.writeCMD(toByteArray("aa08c7" + data));
    }

    public static void sendShengji() {
        String yihuo = BleUtils.XORAnd("06ca0100");
        //String data = "aa06ac0100";

        byte[] data = new byte[5];
        data[0] = (byte) 0xaa;
        data[1] = (byte) 0x06;
        data[2] = (byte) 0xac;
        data[3] = (byte) 0x01;
        data[4] = (byte) 0x00;
        BleCommunication.sendData2(LauncherActivity.mBLE, LauncherActivity.bcWrite, data);
    }

    /**
     * 获取校验
     */
    private static byte checkSum(byte[] data) {
        byte checksum = 0;
        for (byte aData : data) {
            checksum ^= aData;
        }
        return checksum;
    }

    /**
     * 发送时间
     */
    public static void sendTime() {

        Calendar now = Calendar.getInstance();
        System.out.println("年: " + now.get(Calendar.YEAR));
        System.out.println("月: " + (now.get(Calendar.MONTH) + 1) + "");
        System.out.println("日: " + now.get(Calendar.DAY_OF_MONTH));
        System.out.println("时: " + now.get(Calendar.HOUR_OF_DAY));
        System.out.println("分: " + now.get(Calendar.MINUTE));
        System.out.println("秒: " + now.get(Calendar.SECOND));

        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        // String zone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        // Calendar c = Calendar.getInstance();
        // c.setTimeZone(TimeZone.getTimeZone(zone));
        // String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        // if ("1".equals(mWay)) {
        //     mWay = "7";
        // } else if ("2".equals(mWay)) {
        //     mWay = "1";
        // } else if ("3".equals(mWay)) {
        //     mWay = "2";
        // } else if ("4".equals(mWay)) {
        //     mWay = "3";
        // } else if ("5".equals(mWay)) {
        //     mWay = "4";
        // } else if ("6".equals(mWay)) {
        //     mWay = "5";
        // } else if ("7".equals(mWay)) {
        //     mWay = "6";
        // }
        // String data = "AT+TIME=" + df.format(new Date()) + "," + zone.substring(3, 6) + "," + mWay;
        // + BleUtils.XORAnd("0AC3100101010101")
        // String data = "AA0AC3100101010101";

        byte[] data = new byte[8];
        data[0] = (byte) 0xaa;
        data[1] = (byte) 0x0a;
        data[2] = (byte) 0xC3;
        data[3] = (byte) 0x10;
        data[4] = (byte) 0x01;
        data[5] = (byte) 0x01;
        data[6] = (byte) 0x01;
        data[7] = (byte) 0x01;
        BleCommunication.sendData2(LauncherActivity.mBLE, LauncherActivity.bcWrite, data);
    }

    public static byte[] toByteArray(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            throw new IllegalArgumentException("this hexString must not be empty");
            //byte[] byteArray = new byte[1];
            //byteArray[0]= (byte) 0xaa;
            //return byteArray;
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }
}
