package com.jx.sleep_dg.Bean;

import java.io.Serializable;

/**
 * Created by dingt on 2018/7/30.
 */

public class HeartRateBean implements Serializable{

    int RightXinlv;
    int LeftXinlv;
    int RightHuxi;
    int LeftHuxi;
    String time;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRightXinlv() {
        return RightXinlv;
    }

    public void setRightXinlv(int rightXinlv) {
        RightXinlv = rightXinlv;
    }

    public int getLeftXinlv() {
        return LeftXinlv;
    }

    public void setLeftXinlv(int leftXinlv) {
        LeftXinlv = leftXinlv;
    }

    public int getRightHuxi() {
        return RightHuxi;
    }

    public void setRightHuxi(int rightHuxi) {
        RightHuxi = rightHuxi;
    }

    public int getLeftHuxi() {
        return LeftHuxi;
    }

    public void setLeftHuxi(int leftHuxi) {
        LeftHuxi = leftHuxi;
    }
}
