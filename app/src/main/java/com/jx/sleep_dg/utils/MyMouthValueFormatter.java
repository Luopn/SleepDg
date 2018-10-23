package com.jx.sleep_dg.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.jx.sleep_dg.Bean.HeartRateBean;

import java.util.List;

/**
 * Created by 覃微 on 2018/5/22.
 */

public class MyMouthValueFormatter implements IAxisValueFormatter {
    protected List<HeartRateBean> mMouth;
//            = new String[]{
//            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
//    };

    public MyMouthValueFormatter(List<HeartRateBean> labels) {
        this.mMouth = labels;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
//        float percent = value / axis.mAxisRange;
//        LogUtil.e("value:" + value + ";size:" + mMouth.size());
//        return mMouth.get((int) value % mMouth.size());
        int index = (int) value;
        if (index < 0 || index >= mMouth.size()) {
            return "";
        } else {
            return mMouth.get(index).getTime();
        }
    }
}
