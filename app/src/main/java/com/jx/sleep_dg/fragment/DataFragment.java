package com.jx.sleep_dg.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
import com.jx.sleep_dg.Bean.EveXinlvBean;
import com.jx.sleep_dg.Bean.HeartRateBean;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.HistoryDateActivity;
import com.jx.sleep_dg.ui.MainActivity;
import com.jx.sleep_dg.ui.SearchActivity;
import com.jx.sleep_dg.utils.EveBusUtil;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.utils.MyMouthValueFormatter;
import com.jx.sleep_dg.utils.ToastUtil;
import com.jx.sleep_dg.view.EcgView;
import com.jx.sleep_dg.view.HuxiEcgView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 数据
 * Created by Administrator on 2018/7/20.
 */

public class DataFragment extends BaseFragment {

    private TextView tvXinlvRight;
    private TextView tvXinlvLeft;
    private TextView tvHuxiLeft;
    private TextView tvHuxiRight;
    private ImageView ivUserImage;

    private MSPProtocol mspProtocol;
    private boolean isSwitch = true;

    private EcgView ecgXinlv;
    private HuxiEcgView ecgHuxi;

    private boolean isXinlv;
    private boolean isHuxi;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_data;
    }

    @Override
    public void onBindView(View view) {

        view.findViewById(R.id.iv_right).setOnClickListener(this);
        tvXinlvRight = view.findViewById(R.id.tv_xinlv_right);
        tvXinlvLeft = view.findViewById(R.id.tv_xinlv_left);
        tvHuxiLeft = view.findViewById(R.id.tv_huxi_left);
        tvHuxiRight = view.findViewById(R.id.tv_huxi_right);

        ivUserImage = view.findViewById(R.id.iv_user_image);
        ivUserImage.setOnClickListener(this);
        ecgXinlv = view.findViewById(R.id.ecg_xinlv);
        ecgHuxi = view.findViewById(R.id.ecg_huxi);

        mspProtocol = MSPProtocol.getInstance();
        bindViewData();
    }

    private void bindViewData() {
        if (mspProtocol != null) {
            int leftBreathFreq = mspProtocol.getlBreathFreq() & 0xff;
            int rightBreathFreq = mspProtocol.getrBreathFreq() & 0xff;
            int leftHeartBeat = mspProtocol.getlBreathFreq() & 0xff;
            int rightHeartBeat = mspProtocol.getrBreathFreq() & 0xff;

            tvHuxiLeft.setText(String.format("%s次/分钟", leftBreathFreq));
            tvHuxiRight.setText(String.format("%s次/分钟", rightBreathFreq));
            tvXinlvLeft.setText(String.format("%s次/分钟", leftHeartBeat));
            tvXinlvRight.setText(String.format("%s次/分钟", rightHeartBeat));

            if (leftBreathFreq > 0) {
                if (!isHuxi) {
                    ecgHuxi.startDraw();
                    isHuxi = true;
                }
            } else {
                ecgHuxi.stop();
                isHuxi = false;
            }
            if (rightHeartBeat > 0) {
                if (!isXinlv) {
                    ecgXinlv.startDraw();
                    isXinlv = true;
                }
            } else {
                ecgXinlv.stop();
                isXinlv = false;
            }
        }
    }

    @Override
    protected void notifyUIDataSetChanged() {
        super.notifyUIDataSetChanged();
        bindViewData();
    }

    @Override
    protected void notifyDeviceDisconnected() {
        super.notifyDeviceDisconnected();
        ecgXinlv.stop();
        ecgHuxi.stop();
        isXinlv = false;
        isHuxi = false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_right:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.iv_user_image:
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }
}
