package com.jx.sleep_dg.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

/**
 * Created by dingt on 2018/7/25.
 */

public class MonthDataFragment extends BaseFragment {
    private LineChart mChart;

    private MyCountDownTimer mTimer;

    private String time[] = {"01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00", "13:00"};


    public MonthDataFragment() {
    }

    public static MonthDataFragment newInstance() {
        MonthDataFragment fragment = new MonthDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mouth_data;
    }

    @Override
    public void onBindView(View view) {
        mChart = view.findViewById(R.id.chart1);
        initChart();
    }

    private void initChart() {
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getLegend().setEnabled(false);

        //自定义设置X轴的值为 => 日期
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return time[(int) value];
            }
        };


        XAxis xl = mChart.getXAxis();
//        xl.setTextColor(Color.parseColor("#539FA4"));
        xl.setTextSize(12);
        xl.setAvoidFirstLastClipping(true);
        xl.setLabelCount(10);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xl.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
//        xl.setGridColor(Color.parseColor("#919ed0"));
        xl.setDrawGridLines(true);
        xl.setAxisMinimum(0f);
        xl.setGranularity(1f);
        xl.setValueFormatter(formatter);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setInverted(false);
//        leftAxis.setValueFormatter(new MyYAxisValueFormatter());
        leftAxis.setAxisMinimum(0f);
//        leftAxis.setLabelCount(10);
        leftAxis.setGranularity(1f);
        leftAxis.setLabelCount(3, false);
        leftAxis.setAxisMaximum(150f);

//        leftAxis.setTextColor(Color.parseColor("#539FA4"));
        leftAxis.setTextSize(12);
//        leftAxis.setValueFormatter(new MyYAxisValueFormatter());
        leftAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
//        leftAxis.setGridColor(Color.parseColor("#919ed0"));
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setInverted(false);
        Legend l = mChart.getLegend();
//        if ("0".equals(PreferenceUtils.getString(Constance.THEME))) {
//            xl.setTextColor(getResources().getColor(R.color.main_color));
//            leftAxis.setTextColor(getResources().getColor(R.color.main_color));
//            leftAxis.setGridColor(getResources().getColor(R.color.main_color));
//            xl.setGridColor(getResources().getColor(R.color.main_color));
//        } else {
//            xl.setTextColor(getResources().getColor(R.color.top_green));
//            leftAxis.setTextColor(getResources().getColor(R.color.top_green));
//            leftAxis.setGridColor(getResources().getColor(R.color.top_green));
//            xl.setGridColor(getResources().getColor(R.color.top_green));
//        }
        l.setForm(Legend.LegendForm.LINE);
//        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
//            tv_month.setText(TimeUtil.getMonth(new Date()) + "月");
//        } else {
//            String english = TimeUtil.englishData(TimeUtil.getMonth(new Date()));
//            tv_month.setText(english);
//        }
        setData();
        mTimer = new MyCountDownTimer(30000, 1000);
        mTimer.start();
    }

    private void setData() {
        mChart.animateX(3000);

        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < 13; i++) {
            float xVal = Float.valueOf(i + "");
            float yVal = new Random().nextInt(140);
            entries.add(new Entry(xVal, yVal));
        }
        // sort by x-value
        Collections.sort(entries, new EntryXComparator());

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(entries, "DataSet 1");
        set1.setDrawValues(false);
//        set1.setCircleColor(Color.parseColor("#67b8b7"));
//        set1.setHighLightColor(Color.parseColor("#67b8b7"));
//        set1.setLineWidth(1.5f);
//        set1.setCircleRadius(4f);

        // create a data object with the datasets
        LineData data = new LineData(set1);

        // set data
        mChart.setData(data);


        if (mChart.getData() != null) {
            mChart.getData().setHighlightEnabled(false);
        }
//        XAxis xl = mChart.getXAxis();
//        xl.setValueFormatter(new MyMouthValueFormatter(xData));
        mChart.invalidate();
    }

    /**
     * 继承 CountDownTimer 防范
     * <p/>
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p/>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p/>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            setData();
            mTimer = new MyCountDownTimer(30000, 1000);
            mTimer.start();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            LogUtil.e("倒计时;" + (millisUntilFinished / 1000) + "s");
        }
    }
}
