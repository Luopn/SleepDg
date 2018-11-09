package com.jx.sleep_dg.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.BarChartManager;
import com.jx.sleep_dg.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 睡眠统计
 * Created by Administrator on 2018/7/20.
 */

public class StatisticsFragment extends BaseFragment {

    private BarChart barChart;
    private TextView tvLeftHeartbeat, tvRightHeartbeat;//心率
    private TextView tvLeftBreath, tvRightBreath;//呼吸率
    private TextView tvLeftBodyMove, tvRightBodyMove;//翻身次数（分钟）
    //蓝牙协议
    private MSPProtocol mspProtocol;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_statistics;
    }

    @Override
    public void onBindView(View view) {
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        barChart = view.findViewById(R.id.bar_chat);
        tvLeftHeartbeat = view.findViewById(R.id.tv_xinlv_left);
        tvRightHeartbeat = view.findViewById(R.id.tv_xinlv_right);
        tvLeftBreath = view.findViewById(R.id.tv_huxi_left);
        tvRightBreath = view.findViewById(R.id.tv_huxi_right);
        tvLeftBodyMove = view.findViewById(R.id.tv_fanshen_left);
        tvRightBodyMove = view.findViewById(R.id.tv_fanshen_right);

        //柱状图
        BarChartManager barChartManager = new BarChartManager(barChart);
        List<BarEntry> yVals = new ArrayList<>();
        yVals.add(new BarEntry(1f, 80f));
        yVals.add(new BarEntry(2f, 50f));
        yVals.add(new BarEntry(3f, 60f));
        yVals.add(new BarEntry(4f, 60f));
        yVals.add(new BarEntry(5f, 70f));
        yVals.add(new BarEntry(6f, 80f));
        String label = "";
        barChartManager.showBarChart(yVals, label, Color.parseColor("#233454"));

        TextView tvTitleDeepSleep = view.findViewById(R.id.tv_title_deep_sleep);
        TextView tvTitleShallowSleep = view.findViewById(R.id.tv_title_shallow_sleep);
        TextView tvTitleClearSleep = view.findViewById(R.id.tv_title_clear_sleep);
        TextView tvTitleTimeSleep = view.findViewById(R.id.tv_title_time_sleep);
        //染色,兼容Android23以下版本
        CommonUtil.drawableTint(getActivity(), tvTitleDeepSleep, ContextCompat.getColor(getActivity(), R.color.mediumblue));
        CommonUtil.drawableTint(getActivity(), tvTitleShallowSleep, ContextCompat.getColor(getActivity(), R.color.textAccentColor));
        CommonUtil.drawableTint(getActivity(), tvTitleClearSleep, ContextCompat.getColor(getActivity(), R.color.default_blue_light));
        CommonUtil.drawableTint(getActivity(), tvTitleTimeSleep, ContextCompat.getColor(getActivity(), R.color.textTitleColor));

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);

        mspProtocol = MSPProtocol.getInstance();
        bindViewData();
    }

    private void bindViewData() {
        if (mspProtocol == null) return;
        tvLeftHeartbeat.setText(String.format("左 %s", mspProtocol.getlHeartBeat()));
        tvRightHeartbeat.setText(String.format("右 %s", mspProtocol.getlHeartBeat()));
        tvLeftBreath.setText(String.format("左 %s", mspProtocol.getlBreathFreq()));
        tvRightBreath.setText(String.format("右 %s", mspProtocol.getrBreathFreq()));
        tvLeftBodyMove.setText(String.format("左 %s", mspProtocol.getlBodyMoveVal()));
        tvRightBodyMove.setText(String.format("右 %s", mspProtocol.getrBodyMoveVal()));
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }
}
