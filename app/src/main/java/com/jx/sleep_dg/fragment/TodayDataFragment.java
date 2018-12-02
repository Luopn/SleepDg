package com.jx.sleep_dg.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;

import java.util.ArrayList;

/**
 * Created by dingt on 2018/7/25.
 */

public class TodayDataFragment extends BaseMainFragment {

    private LineChart mChart1;
    private LineChart mChart2;
    private LineChart mChart3;
    private LineChart mChart4;

    public TodayDataFragment() {
    }

    public static TodayDataFragment newInstance() {
        TodayDataFragment fragment = new TodayDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {

        return R.layout.fragment_today;

    }

    @Override
    public void onBindView(View view) {

        mChart1 = view.findViewById(R.id.chart1);
        mChart2 = view.findViewById(R.id.chart2);
        mChart3 = view.findViewById(R.id.chart3);
        mChart4 = view.findViewById(R.id.chart4);

        bindview1();
        bindview2();
        bindview3();
        bindview4();
    }

    private void bindview1() {
        mChart1.setDrawGridBackground(false);
        mChart1.getDescription().setEnabled(false);
        mChart1.setTouchEnabled(true);
        mChart1.setDragEnabled(true);
        mChart1.setScaleEnabled(true);
        mChart1.setPinchZoom(true);
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart1.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置

        mChart1.getAxisLeft().setDrawAxisLine(true);
        mChart1.getAxisRight().setDrawAxisLine(true);
        setData1(20, 100);
        mChart1.animateX(2500);
        Legend l = mChart1.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    public void bindview2() {
        mChart2.setDrawGridBackground(false);
        mChart2.getDescription().setEnabled(false);
        mChart2.setTouchEnabled(true);
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.setPinchZoom(true);
        mChart2.getLegend().setEnabled(false);
        mChart2.getAxisRight().setEnabled(false);


        // set an alternative background color
//        mChart2.setBackgroundColor(Color.LTGRAY);

        // add data
        setData2(15, 100);

        mChart2.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart2.getLegend();

        // modify the legend ...
//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = mChart2.getXAxis();
//        xAxis.setTypeface(tf);
//        xAxis.setTextSize(11f);
//        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标

        YAxis leftAxis = mChart2.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        xAxis.setDrawAxisLine(false);

        leftAxis.setLabelCount(4, false);
        leftAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标


//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setTypeface(mTfLight);
//        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaximum(200);
//        rightAxis.setAxisMinimum(000);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawZeroLine(false);
//        rightAxis.setGranularityEnabled(false);
//        rightAxis.setDrawAxisLine(false);//边线
//
//        rightAxis.setLabelCount(4, false);
//        rightAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标


        //去掉纵向网格线和顶部边线：
//        mChart.getXaxis().setDrawAxisLine(false);
//        mChart.getXaxis().setDrawGridLines(false);
        //去掉左右边线：
//        mChart.getAxisLeft().setDrawAxisLine(false);
//        mChart.getAxisRight().setDrawAxisLine(false);
    }

    public void bindview3() {
        mChart3.setDrawGridBackground(false);
        mChart3.getDescription().setEnabled(false);
        mChart3.setTouchEnabled(true);
        mChart3.setDragEnabled(true);
        mChart3.setScaleEnabled(true);
        mChart3.setPinchZoom(true);
        mChart3.getLegend().setEnabled(false);
        mChart3.getAxisRight().setEnabled(false);


        // set an alternative background color
//        mChart3.setBackgroundColor(Color.LTGRAY);

        // add data
        setData3(20, 140);

        mChart3.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart3.getLegend();

        // modify the legend ...
//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = mChart3.getXAxis();
//        xAxis.setTypeface(tf);
//        xAxis.setTextSize(11f);
//        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标

        YAxis leftAxis = mChart3.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        xAxis.setDrawAxisLine(false);

        leftAxis.setLabelCount(4, false);
        leftAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标


//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setTypeface(mTfLight);
//        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaximum(200);
//        rightAxis.setAxisMinimum(000);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawZeroLine(false);
//        rightAxis.setGranularityEnabled(false);
//        rightAxis.setDrawAxisLine(false);//边线
//
//        rightAxis.setLabelCount(4, false);
//        rightAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标


        //去掉纵向网格线和顶部边线：
//        mChart.getXaxis().setDrawAxisLine(false);
//        mChart.getXaxis().setDrawGridLines(false);
        //去掉左右边线：
//        mChart.getAxisLeft().setDrawAxisLine(false);
//        mChart.getAxisRight().setDrawAxisLine(false);
    }

    public void bindview4() {
        mChart4.setDrawGridBackground(false);
        mChart4.getDescription().setEnabled(false);
        mChart4.setTouchEnabled(true);
        mChart4.setDragEnabled(true);
        mChart4.setScaleEnabled(true);
        mChart4.setPinchZoom(true);
        mChart4.getLegend().setEnabled(false);
        mChart4.getAxisRight().setEnabled(false);


        // set an alternative background color
//        mChart4.setBackgroundColor(Color.LTGRAY);

        // add data
        setData4(30, 60);

        mChart4.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart4.getLegend();

        // modify the legend ...
//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = mChart4.getXAxis();
//        xAxis.setTypeface(tf);
//        xAxis.setTextSize(11f);
//        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标

        YAxis leftAxis = mChart4.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        xAxis.setDrawAxisLine(false);

        leftAxis.setLabelCount(4, false);
        leftAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标


//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setTypeface(mTfLight);
//        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaximum(200);
//        rightAxis.setAxisMinimum(000);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawZeroLine(false);
//        rightAxis.setGranularityEnabled(false);
//        rightAxis.setDrawAxisLine(false);//边线
//
//        rightAxis.setLabelCount(4, false);
//        rightAxis.enableGridDashedLine(40, 10, 0); //虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标


        //去掉纵向网格线和顶部边线：
//        mChart.getXaxis().setDrawAxisLine(false);
//        mChart.getXaxis().setDrawGridLines(false);
        //去掉左右边线：
//        mChart.getAxisLeft().setDrawAxisLine(false);
//        mChart.getAxisRight().setDrawAxisLine(false);
    }


    private void setData1(int count, float range) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 10;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (mChart1.getData() != null &&
                mChart1.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart1.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart1.getData().notifyDataChanged();
            mChart1.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "DataSet 1");
            set1.setDrawIcons(false);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);//设置是否实心
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setFillColor(Color.parseColor("#9b9a77"));
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            LineData data = new LineData(dataSets);
            mChart1.setData(data);
        }
    }
    private void setData2(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
//
        for (int i = 0; i < count - 1; i++) {
            xVals.add("");
        }
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;
            yVals1.add(new Entry(i, val));
        }

//        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < count-1; i++) {
//            float mult = range;
//            float val = (float) (Math.random() * mult) + 450;
//            yVals2.add(new Entry(i, val));
////            if(i == 10) {
////                yVals2.add(new Entry(i, val + 50));
////            }
//        }

//        ArrayList<Entry> yVals3 = new ArrayList<Entry>();
//
//        for (int i = 0; i < count; i++) {
//            float mult = range;
//            float val = (float) (Math.random() * mult) + 500;
//            yVals3.add(new Entry(i, val));
//        }

//        LineDataSet set1, set2, set3;
        LineDataSet set1;

        if (mChart2.getData() != null &&
                mChart2.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart2.getData().getDataSetByIndex(0);
//            set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
//            set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
//            set2.setValues(yVals2);
//            set3.setValues(yVals3);
            mChart2.getData().notifyDataChanged();
            mChart2.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
//            setDrawValues
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
//            set2 = new LineDataSet(yVals2, "DataSet 2");
//            set2.setAxisDependency(AxisDependency.RIGHT);
//            set2.setColor(Color.RED);
//            set2.setCircleColor(Color.WHITE);
//            set2.setLineWidth(2f);
//            set2.setCircleRadius(3f);
//            set2.setFillAlpha(65);
//            set2.setFillColor(Color.RED);
//            set2.setDrawCircleHole(false);
//            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

//            set3 = new LineDataSet(yVals3, "DataSet 3");
//            set3.setAxisDependency(AxisDependency.RIGHT);
//            set3.setColor(Color.YELLOW);
//            set3.setCircleColor(Color.WHITE);
//            set3.setLineWidth(2f);
//            set3.setCircleRadius(3f);
//            set3.setFillAlpha(65);
//            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
//            set3.setDrawCircleHole(false);
//            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the datasets
//            LineData data = new LineData(set1, set2, set3);
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            mChart2.setData(data);
        }
    }
    private void setData3(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
//
        for (int i = 0; i < count - 1; i++) {
            xVals.add("");
        }
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;
            yVals1.add(new Entry(i, val));
        }

//        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < count-1; i++) {
//            float mult = range;
//            float val = (float) (Math.random() * mult) + 450;
//            yVals2.add(new Entry(i, val));
////            if(i == 10) {
////                yVals2.add(new Entry(i, val + 50));
////            }
//        }

//        ArrayList<Entry> yVals3 = new ArrayList<Entry>();
//
//        for (int i = 0; i < count; i++) {
//            float mult = range;
//            float val = (float) (Math.random() * mult) + 500;
//            yVals3.add(new Entry(i, val));
//        }

//        LineDataSet set1, set2, set3;
        LineDataSet set1;

        if (mChart3.getData() != null &&
                mChart3.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart3.getData().getDataSetByIndex(0);
//            set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
//            set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
//            set2.setValues(yVals2);
//            set3.setValues(yVals3);
            mChart3.getData().notifyDataChanged();
            mChart3.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
//            setDrawValues
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
//            set2 = new LineDataSet(yVals2, "DataSet 2");
//            set2.setAxisDependency(AxisDependency.RIGHT);
//            set2.setColor(Color.RED);
//            set2.setCircleColor(Color.WHITE);
//            set2.setLineWidth(2f);
//            set2.setCircleRadius(3f);
//            set2.setFillAlpha(65);
//            set2.setFillColor(Color.RED);
//            set2.setDrawCircleHole(false);
//            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

//            set3 = new LineDataSet(yVals3, "DataSet 3");
//            set3.setAxisDependency(AxisDependency.RIGHT);
//            set3.setColor(Color.YELLOW);
//            set3.setCircleColor(Color.WHITE);
//            set3.setLineWidth(2f);
//            set3.setCircleRadius(3f);
//            set3.setFillAlpha(65);
//            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
//            set3.setDrawCircleHole(false);
//            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the datasets
//            LineData data = new LineData(set1, set2, set3);
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            mChart3.setData(data);
        }
    }
    private void setData4(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
//
        for (int i = 0; i < count - 1; i++) {
            xVals.add("");
        }
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;
            yVals1.add(new Entry(i, val));
        }

//        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < count-1; i++) {
//            float mult = range;
//            float val = (float) (Math.random() * mult) + 450;
//            yVals2.add(new Entry(i, val));
////            if(i == 10) {
////                yVals2.add(new Entry(i, val + 50));
////            }
//        }

//        ArrayList<Entry> yVals3 = new ArrayList<Entry>();
//
//        for (int i = 0; i < count; i++) {
//            float mult = range;
//            float val = (float) (Math.random() * mult) + 500;
//            yVals3.add(new Entry(i, val));
//        }

//        LineDataSet set1, set2, set3;
        LineDataSet set1;

        if (mChart4.getData() != null &&
                mChart4.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart4.getData().getDataSetByIndex(0);
//            set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
//            set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
//            set2.setValues(yVals2);
//            set3.setValues(yVals3);
            mChart4.getData().notifyDataChanged();
            mChart4.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
//            setDrawValues
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
//            set2 = new LineDataSet(yVals2, "DataSet 2");
//            set2.setAxisDependency(AxisDependency.RIGHT);
//            set2.setColor(Color.RED);
//            set2.setCircleColor(Color.WHITE);
//            set2.setLineWidth(2f);
//            set2.setCircleRadius(3f);
//            set2.setFillAlpha(65);
//            set2.setFillColor(Color.RED);
//            set2.setDrawCircleHole(false);
//            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

//            set3 = new LineDataSet(yVals3, "DataSet 3");
//            set3.setAxisDependency(AxisDependency.RIGHT);
//            set3.setColor(Color.YELLOW);
//            set3.setCircleColor(Color.WHITE);
//            set3.setLineWidth(2f);
//            set3.setCircleRadius(3f);
//            set3.setFillAlpha(65);
//            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
//            set3.setDrawCircleHole(false);
//            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the datasets
//            LineData data = new LineData(set1, set2, set3);
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            mChart4.setData(data);
        }
    }

}
