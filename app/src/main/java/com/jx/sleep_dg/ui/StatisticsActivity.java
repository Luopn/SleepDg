package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarEntry;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.BarChartManager;
import com.jx.sleep_dg.utils.CommonUtil;
import com.jx.sleep_dg.view.NumberRollingView;
import com.jx.sleep_dg.view.barchart.RoundBarChart;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 睡眠统计
 * Created by Administrator on 2018/7/20.
 */

public class StatisticsActivity extends BaseActivity {

    private NumberRollingView tvSleepScore;
    private RoundBarChart barChart;
    private TextView tvLeftHeartbeat, tvRightHeartbeat;//心率
    private TextView tvLeftBreath, tvRightBreath;//呼吸率
    private TextView tvLeftBodyMove, tvRightBodyMove;//翻身次数（分钟）
    //蓝牙协议
    private MSPProtocol mspProtocol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_statistics);
        setToolbarTitle(R.string.statistics_title);
        bindView();
    }

    @Override
    public void bindView() {
        ScrollView scrollView = findViewById(R.id.scrollView);
        tvSleepScore = findViewById(R.id.tv_sleep_score);
        barChart = findViewById(R.id.bar_chat);
        tvLeftHeartbeat = findViewById(R.id.tv_xinlv_left);
        tvRightHeartbeat = findViewById(R.id.tv_xinlv_right);
        tvLeftBreath = findViewById(R.id.tv_huxi_left);
        tvRightBreath = findViewById(R.id.tv_huxi_right);
        tvLeftBodyMove = findViewById(R.id.tv_fanshen_left);
        tvRightBodyMove = findViewById(R.id.tv_fanshen_right);

        tvSleepScore.startNumAnim("90");

        //柱状图
        BarChartManager barChartManager = new BarChartManager(barChart);
        barChartManager.setContext(this);
        List<BarEntry> yVals = new ArrayList<>();
        String[] xValues = new String[]{
                getResources().getString(R.string.sleep_deep),
                getResources().getString(R.string.sleep_shallow),
                getResources().getString(R.string.sleep_clear),
                getResources().getString(R.string.sleep_time),
                getResources().getString(R.string.sleep_not_bed),
        };
        String[] yValues = new String[]{"20", "30", "10", "25", "15"};
        int[] colors = new int[]{
                R.color.mediumblue,
                R.color.textAccentColor,
                R.color.default_blue_light,
                R.color.textTitleColor,
                R.color.textTitleColorLight
        };
        yVals.add(new BarEntry(1f, Float.valueOf(yValues[0])));
        yVals.add(new BarEntry(2f, Float.valueOf(yValues[1])));
        yVals.add(new BarEntry(3f, Float.valueOf(yValues[2])));
        yVals.add(new BarEntry(4f, Float.valueOf(yValues[3])));
        yVals.add(new BarEntry(5f, Float.valueOf(yValues[4])));
        String label = "";
        barChartManager.showBarChart(yVals, xValues, yValues, colors);

        TextView tvTitleDeepSleep = findViewById(R.id.tv_title_deep_sleep);
        TextView tvTitleShallowSleep = findViewById(R.id.tv_title_shallow_sleep);
        TextView tvTitleClearSleep = findViewById(R.id.tv_title_clear_sleep);
        TextView tvTitleTimeSleep = findViewById(R.id.tv_title_time_sleep);
        //染色,兼容Android23以下版本
        CommonUtil.drawableTint(this, tvTitleDeepSleep, ContextCompat.getColor(this, R.color.mediumblue));
        CommonUtil.drawableTint(this, tvTitleShallowSleep, ContextCompat.getColor(this, R.color.textAccentColor));
        CommonUtil.drawableTint(this, tvTitleClearSleep, ContextCompat.getColor(this, R.color.default_blue_light));
        CommonUtil.drawableTint(this, tvTitleTimeSleep, ContextCompat.getColor(this, R.color.textTitleColor));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.action_do).setIcon(R.mipmap.ic_share);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_do:
                Intent intent = new Intent(this, ShareActivity.class);
                intent.putExtra(ShareActivity.KEY_SLEEP_SCORE, tvSleepScore.getText().toString());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
