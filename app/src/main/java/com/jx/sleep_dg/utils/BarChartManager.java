package com.jx.sleep_dg.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jx.sleep_dg.view.barchart.RoundBarChart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhang on 2018/7/5.
 */

public class BarChartManager {

    private Context context;
    private RoundBarChart mBarChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private DecimalFormat mFormat;

    public BarChartManager(BarChart barChart) {
        this.mBarChart = (RoundBarChart) barChart;
        leftAxis = mBarChart.getAxisLeft();
        rightAxis = mBarChart.getAxisRight();
        xAxis = mBarChart.getXAxis();
    }

    /**
     * 初始化LineChart
     */
    private void initLineChart() {
        mFormat = new DecimalFormat("#,###.##");
        mBarChart.setBackgroundColor(Color.TRANSPARENT);//背景颜色
        leftAxis.setEnabled(false); //隐藏左右坐标轴
        mBarChart.setDrawGridBackground(false);//是否显示网格背景
        //显示每条背景阴影
        mBarChart.setDrawBarShadow(false);
        //设置图标边框的颜色
        mBarChart.setBorderColor(Color.parseColor("#ff0000"));
        // mBarChart.setHighlightFullBarEnabled(false);
        mBarChart.setTouchEnabled(false); // 所有触摸事件,默认true
        mBarChart.setDragEnabled(true);    // 可拖动,默认true
        mBarChart.setScaleEnabled(false);   // 两个轴上的缩放,X,Y分别默认为true
        mBarChart.setScaleXEnabled(false);  // X轴上的缩放,默认true
        mBarChart.setScaleYEnabled(false);  // Y轴上的缩放,默认true
        mBarChart.setPinchZoom(false);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        mBarChart.setDoubleTapToZoomEnabled(false); // 双击缩放,默认true
        mBarChart.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true

        //显示边界
        mBarChart.setDrawBorders(false);
        //设置XY动画效果
        mBarChart.animateY(1000, Easing.EasingOption.Linear);
        mBarChart.animateX(1000, Easing.EasingOption.Linear);
        mBarChart.getDescription().setEnabled(false);//不显示描述信息
        //图例设置
        Legend legend = mBarChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);//不显示图例
        legend.setTextSize(11f);//图例文字的大小
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); //显示位置
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴设置显示位置在底部
        xAxis.setGranularity(1f);// X轴最小间距
        xAxis.setDrawGridLines(false);// 不绘制网格线
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);//X轴字体样式
        xAxis.setCenterAxisLabels(true);//设置X轴文字剧中对齐
        leftAxis.setDrawGridLines(false);// 保证Y轴从0开始，不然会上移一点
        rightAxis.setAxisMinimum(0f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.WHITE);
        rightAxis.setEnabled(false); //右侧Y轴不显示
    }

    /**
     * 展示柱状图(一条)
     */
    public void showBarChart(List<BarEntry> yVals, String[] xValues, String[] yValues, int[] color) {
        initLineChart();

        // 每一个BarDataSet代表一类柱状图
        BarDataSet barDataSet = new BarDataSet(yVals, "");
        barDataSet.setColors(color, context);
        barDataSet.setValueTextColor(Color.WHITE);//文字颜色
        barDataSet.setValueFormatter(new IValueFormatter() {//设置文字格式
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format(Locale.getDefault(), "%d%%", (int) value);
            }
        });
        barDataSet.setDrawValues(true);//是否显示顶部的值
        barDataSet.setValueTextSize(9f); //文字的大小

        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.0f);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        BarData data = new BarData(dataSets);
        //设置宽度
        data.setBarWidth(0.3f);
        xAxis.setLabelCount(yVals.size() + 1, true);
        xAxis.setDrawLabels(true);
        IAxisValueFormatter xAxisFormatter = new XAxisValueFormatter(xValues);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.TRANSPARENT);//设置x轴颜色
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(value) + "%";
            }
        });
        //leftAxis.setLabelCount(yValues.length + 1, false);
        leftAxis.setAxisLineColor(Color.parseColor("#d5d5d5"));
        //设置Y轴的最小值和最大值
        leftAxis.setAxisMaximum(getMax(yValues));
        leftAxis.setAxisMinimum(0f);
        mBarChart.setData(data);
    }

    public class XAxisValueFormatter implements IAxisValueFormatter {

        private String[] xValues;

        public XAxisValueFormatter(String[] xValues) {
            this.xValues = xValues;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if ((int) value < xValues.length)
                return xValues[(int) value];
            else
                return "";
        }

    }

    /**
     * 展示柱状图(多条)
     * +-+
     *
     * @param xAxisValues
     * @param yAxisValues
     * @param labels
     * @param colours
     */
    public void showMoreBarChart(final List<Float> xAxisValues, List<List<Float>> yAxisValues, List<String> labels, List<Integer> colours) {
        initLineChart();
        BarData data = new BarData();
        for (int i = 0; i < yAxisValues.size(); i++) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int j = 0; j < yAxisValues.get(i).size(); j++) {

                entries.add(new BarEntry(xAxisValues.get(j), yAxisValues.get(i).get(j)));
            }
            BarDataSet barDataSet = new BarDataSet(entries, labels.get(i));

            barDataSet.setColor(colours.get(i));
            barDataSet.setValueTextColor(colours.get(i));
            barDataSet.setValueTextSize(10f);
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            data.addDataSet(barDataSet);
        }
        int amount = yAxisValues.size();

        float groupSpace = 0.3f; //柱状图组之间的间距
        float barSpace = (float) ((1 - 0.12) / amount / 10); // x4 DataSet
        float barWidth = (float) ((1 - 0.3) / amount / 10 * 9); // x4 DataSet

        // (0.2 + 0.02) * 4 + 0.08 = 1.00 -> interval per "group"
        xAxis.setLabelCount(xAxisValues.size() - 1, false);
        data.setBarWidth(barWidth);
        final String[] xValues = {"小学", "初中", "高中", "专科", "本科"};
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                for (int i = 0; i < xAxisValues.size(); i++) {
                    if (value == (xAxisValues.get(i) - 1)) {
                        return xValues[i];
                    }
                }
                return "";
            }
        });
        data.groupBars(0, groupSpace, barSpace);
        mBarChart.setData(data);
    }

    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        mBarChart.invalidate();
    }

    /**
     * 设置X轴的值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setXAxis(float max, float min, int labelCount) {
        xAxis.setAxisMaximum(max);
        xAxis.setAxisMinimum(min);
        xAxis.setLabelCount(labelCount, false);

        mBarChart.invalidate();
    }

    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }

    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        mBarChart.setDescription(description);
        mBarChart.invalidate();
    }

    //设置上下文
    public void setContext(Context context) {
        this.context = context;
    }

    private int getMax(String[] srcArray) {
        float[] array = new float[srcArray.length];
        for (int i = 0; i < srcArray.length; i++) {
            array[i] = Float.valueOf(srcArray[i]);
        }
        float max = array[0];
        for (float anArray : array) {
            if (anArray > max) {
                max = anArray;
            }
        }
        return (int) Math.ceil(max);
    }
}