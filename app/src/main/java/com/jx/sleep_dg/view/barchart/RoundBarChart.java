package com.jx.sleep_dg.view.barchart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.renderer.BarChartRenderer;

// Created by luopl on 2018/11/11.
// Copyright (c) 2018 Yunzhimian All rights reserved.
// 圆角的BarChart
public class RoundBarChart extends BarChart {
    public RoundBarChart(Context context) {
        super(context);
    }

    public RoundBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new RoundBarChartRenderer(this, mAnimator, mViewPortHandler);
    }
}
