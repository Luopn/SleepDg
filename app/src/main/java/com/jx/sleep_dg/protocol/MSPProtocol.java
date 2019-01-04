package com.jx.sleep_dg.protocol;

import com.jx.sleep_dg.event.ConfigureResEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//
// Created by mac(1004633425@qq.com) on 2018/10/20.
// Copyright (c) YunZhiMian All rights reserved.
//
public class MSPProtocol {

    private static final String TAG = "MSPProtocol";
    private static final MSPProtocol mSPProtocol = new MSPProtocol();

    private List<Byte> dataList = Collections.synchronizedList(new LinkedList<Byte>());
    private Thread handleDataTrd;
    private boolean isDataTrdRun;

    private byte heatLevel;//加热档位

    private byte lPresureSetVal;//左床气压设置值
    private byte rPresureSetVal;//右床气压设置值

    private byte lPresureMemVal;//左床气压记忆值
    private byte rPresureMemVal;//右床气压记忆值

    private byte lPresureCurVal;//左床气压实时值
    private byte rPresureCurVal;//右床气压实时值

    private byte isWiFiConnSign;//WiFi联网标志

    private byte lHeartBeat;//左心率
    private byte lBreathFreq;//左呼吸频率

    private byte lSnoreSign;//左打鼾标志
    private byte lBodyMoveByMinuteSign;//左分钟体动标识位
    private byte lBodyMoveBySecondSign;//左秒体动标识位
    private int lBodyMoveValH, lBodyMoveValL;//左体动值高，低位值

    private byte rHeartBeat;//右心率
    private byte rBreathFreq;//右呼吸频率

    private byte rSnoreSign;//右打鼾标志
    private byte rBodyMoveByMinuteSign;//右分钟体动标识位
    private byte rBodyMoveBySecondSign;//右秒体动标识位
    private int rBodyMoveValH, rBodyMoveValL;//右体动值高，低位值

    private byte high1 = 0;//1路升降机高度
    private byte high2 = 0;//2路升降机高度
    private byte high3 = 0;//3路升降机高度
    private byte high4 = 0;//4路升降机高度

    private byte mode;//模式

    private byte tire_hour;//自动补气时
    private byte tire_minute;//自动补气分

    public static MSPProtocol getInstance() {
        return mSPProtocol;
    }

    private MSPProtocol() {
        //开启数据解析线程
        if (handleDataTrd == null || !handleDataTrd.isAlive()) {
            dataList.clear();
            isDataTrdRun = true;
            handleDataTrd = new Thread(mHandleDataRunnable);
            handleDataTrd.start();
        }
    }

    //接收数据
    public void setRawBytes(byte[] rawBytes) {
        for (byte rawByte : rawBytes) {
            dataList.add(rawByte);
        }
    }

    private Runnable mHandleDataRunnable = new Runnable() {
        @Override
        public void run() {
            while (isDataTrdRun) {
                if (dataList.size() >= 4) {
                    if ((dataList.get(0) & 0xff) == 0xbb) {//包头
                        int dataLen = dataList.get(1) & 0xff;//数据长度，整个数据包长
                        int commandType = dataList.get(2) & 0xff;//命令
                        if (dataList.size() >= dataLen) {
                            int checkSumVal = dataList.get(dataLen - 1) & 0xff;
                            //if (checkSumVal == checkSum(dataList, dataLen)) {//校验
                            if (commandType == 0x31) {//设备的状态
                                onDeviceStateInfo();
                            }
                            if (commandType == 0x21) {//WiFi SSID发送情况
                                int dataPackOrder = dataList.get(3) & 0xff;//包序
                                int res = dataList.get(4) & 0xff;
                                EventBus.getDefault().post(new ConfigureResEvent(commandType, dataPackOrder, res));
                            }
                            if (commandType == 0x22) {//WiFi 密码发送情况
                                int dataPackOrder = dataList.get(3) & 0xff;//包序
                                int res = dataList.get(4) & 0xff;
                                EventBus.getDefault().post(new ConfigureResEvent(commandType, dataPackOrder, res));
                            }
                            //}
                            for (int i = 0; i < dataLen; i++) {
                                dataList.remove(0);
                            }
                        }
                    } else {
                        dataList.remove(0);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //处理设备上报信息
    private void onDeviceStateInfo() {
        int dataPackOrder = dataList.get(3) & 0xff;//包序
        switch (dataPackOrder) {
            case 0x1://第1包
                heatLevel = dataList.get(4);
                lPresureSetVal = dataList.get(5);
                rPresureSetVal = dataList.get(6);
                lPresureMemVal = dataList.get(7);
                rPresureMemVal = dataList.get(8);
                lPresureCurVal = dataList.get(9);
                rPresureCurVal = dataList.get(10);
                isWiFiConnSign = dataList.get(11);
                lHeartBeat = dataList.get(12);
                lBreathFreq = dataList.get(13);
                lSnoreSign = dataList.get(14);
                lBodyMoveByMinuteSign = dataList.get(15);
                lBodyMoveBySecondSign = dataList.get(16);
                break;
            case 0x2: { //第2包
                List<Byte> byteList = new ArrayList<>();
                //从第5个字节开始，高位左体动值占15个字节
                for (int i = 0; i < 15; i++) {
                    byteList.add(dataList.get(i + 4));
                }
                lBodyMoveValH = bytesToInt(byteList);
                break;
            }
            case 0x3: {//第3包
                List<Byte> lbyteList = new ArrayList<>();
                //从第5个字节开始，低位左体动值占5个字节
                for (int i = 0; i < 5; i++) {
                    lbyteList.add(dataList.get(i + 4));
                }
                lBodyMoveValL = bytesToInt(lbyteList);

                rHeartBeat = dataList.get(9);
                rBreathFreq = dataList.get(10);
                rSnoreSign = dataList.get(11);
                rBodyMoveByMinuteSign = dataList.get(12);
                rBodyMoveBySecondSign = dataList.get(13);

                List<Byte> rbyteList = new ArrayList<>();
                //从15个字节开始，高位右体动值占5个字节
                for (int i = 0; i < 5; i++) {
                    rbyteList.add(dataList.get(i + 14));
                }
                rBodyMoveValH = bytesToInt(rbyteList);
                break;
            }
            case 0x4: {//第4包
                List<Byte> byteList = new ArrayList<>();
                //从第5个字节开始，低位右体动值占15个字节
                for (int i = 0; i < 15; i++) {
                    byteList.add(dataList.get(i + 4));
                }
                rBodyMoveValL = bytesToInt(byteList);
                break;
            }
            case 0x5://第5包
                high1 = dataList.get(4);
                high2 = dataList.get(5);
                high3 = dataList.get(6);
                high4 = dataList.get(7);
                mode = dataList.get(8);
                tire_hour = dataList.get(9);
                tire_minute = dataList.get(10);
                break;
        }
    }

    //高位-->低位
    private int bytesToInt(List<Byte> byteList) {
        int value = 0;
        for (int i = 0; i < byteList.size(); i++) {
            byte val = byteList.get(byteList.size() - i - 1);
            value = (val & 0xff) << (8 * i);
        }
        return value;
    }

    //异或较验值：从第2个字节开始至最后倒数第2个字节，进行异或。如：C5，03，04，05，XX。中XX=03^04^05
    private int checkSum(List<Byte> data, int lenth) {
        byte checksum = 0;
        for (int i = 1; i < lenth - 2; i++) {
            checksum ^= data.get(i);
        }
        return checksum & 0xff;
    }

    public boolean isDataTrdRun() {
        return isDataTrdRun;
    }

    public void setDataTrdRun(boolean dataTrdRun) {
        isDataTrdRun = dataTrdRun;
    }

    public byte getHeatLevel() {
        return heatLevel;
    }

    public byte getlPresureSetVal() {
        return lPresureSetVal;
    }

    public byte getrPresureSetVal() {
        return rPresureSetVal;
    }

    public byte getlPresureMemVal() {
        return lPresureMemVal;
    }

    public byte getrPresureMemVal() {
        return rPresureMemVal;
    }

    public byte getlPresureCurVal() {
        return lPresureCurVal;
    }

    public byte getrPresureCurVal() {
        return rPresureCurVal;
    }

    public byte getIsWiFiConnSign() {
        return isWiFiConnSign;
    }

    public byte getlHeartBeat() {
        return lHeartBeat;
    }

    public byte getlBreathFreq() {
        return lBreathFreq;
    }

    public byte getlSnoreSign() {
        return lSnoreSign;
    }

    public byte getlBodyMoveByMinuteSign() {
        return lBodyMoveByMinuteSign;
    }

    public byte getlBodyMoveBySecondSign() {
        return lBodyMoveBySecondSign;
    }

    //左体动值，共20个，按高低位顺序拼接,低位占5个字节。
    public float getlBodyMoveVal() {
        return (lBodyMoveValH << 5) | lBodyMoveValL;
    }

    public byte getrHeartBeat() {
        return rHeartBeat;
    }

    public byte getrBreathFreq() {
        return rBreathFreq;
    }

    public byte getrSnoreSign() {
        return rSnoreSign;
    }

    public byte getrBodyMoveByMinuteSign() {
        return rBodyMoveByMinuteSign;
    }

    public byte getrBodyMoveBySecondSign() {
        return rBodyMoveBySecondSign;
    }

    //右体动值，共20个，按高低位顺序拼接,低位占15个字节。
    public float getrBodyMoveVal() {
        return (rBodyMoveValH << 15) | rBodyMoveValL;
    }

    public byte getHigh1() {
        return high1;
    }

    public byte getHigh2() {
        return high2;
    }

    public byte getHigh3() {
        return high3;
    }

    public byte getHigh4() {
        return high4;
    }

    public byte getMode() {
        return mode;
    }

    public byte getTire_hour() {
        return tire_hour;
    }

    public byte getTire_minute() {
        return tire_minute;
    }

}
