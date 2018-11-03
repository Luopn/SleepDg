package com.jx.sleep_dg.event;

public class ConfigureResEvent {

    private int orderID;//包的编号
    private int orderSerial;//包序
    private int orderRes;//结果

    public ConfigureResEvent(int orderID, int orderSerial, int orderRes) {
        this.orderID = orderID;
        this.orderSerial = orderSerial;
        this.orderRes = orderRes;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getOrderSerial() {
        return orderSerial;
    }

    public void setOrderSerial(int orderSerial) {
        this.orderSerial = orderSerial;
    }

    public int getOrderRes() {
        return orderRes;
    }

    public void setOrderRes(int orderRes) {
        this.orderRes = orderRes;
    }
}
