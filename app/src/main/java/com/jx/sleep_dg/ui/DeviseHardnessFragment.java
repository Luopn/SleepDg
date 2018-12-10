package com.jx.sleep_dg.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.BorderButton;
import com.jx.sleep_dg.view.bar.MySeekBar;

import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 设备硬度
 */
public class DeviseHardnessFragment extends BaseMainFragment implements View.OnClickListener, View.OnTouchListener {
    private boolean isSwitch = true;

    private boolean isNeedRefreshLevel = true;//更新档位数据，防止不停刷新

    private MySeekBar leftSeekbar;
    private MySeekBar rightSeekbar;
    private ToggleButton togglebutton;

    private ImageView ivAdd, ivDecrease;

    private BorderButton tvLCurHardless;
    private BorderButton tvRCurHardness;
    private TextView tvGear;
    private LinearLayout llChongqi;

    private int rightIndex = 1;
    private int leftIndex = 1;

    private MSPProtocol mspProtocol;

    public static DeviseHardnessFragment newInstance() {
        Bundle args = new Bundle();
        DeviseHardnessFragment fragment = new DeviseHardnessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hardness;
    }

    @Override
    public void onBindView(View view) {
        bindView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mspProtocol = MSPProtocol.getInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bindView(View view) {
        ScrollView mScrollView = view.findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
        tvLCurHardless = view.findViewById(R.id.tv_lcur_hardless);
        tvRCurHardness = view.findViewById(R.id.tv_rcur_hardness);
        tvGear = view.findViewById(R.id.tv_gear);
        llChongqi = view.findViewById(R.id.ll_chongqi);
        ivAdd = view.findViewById(R.id.iv_jia);
        ivDecrease = view.findViewById(R.id.iv_jian);
        togglebutton = view.findViewById(R.id.togglebutton);
        leftSeekbar = view.findViewById(R.id.left_seekbar);
        rightSeekbar = view.findViewById(R.id.right_seekbar);

        ivAdd.setOnClickListener(this);
        ivDecrease.setOnClickListener(this);
        togglebutton.setOnClickListener(this);
        //监听触摸，已停止档位更新
        ivAdd.setOnTouchListener(this);
        ivDecrease.setOnTouchListener(this);

        leftSeekbar.setSeekBarClickListener(new MySeekBar.onSeekBarClickListener() {
            @Override
            public void onSeekBarClick(int position) {
                isNeedRefreshLevel = false;//停止档位数据更新
                leftIndex = position;
                tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), position * 5));
                shanshuo();
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "")
                        + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
            }
        });
        rightSeekbar.setSeekBarClickListener(new MySeekBar.onSeekBarClickListener() {
            @Override
            public void onSeekBarClick(int position) {
                isNeedRefreshLevel = false;//停止档位数据更新
                rightIndex = position;
                tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), position * 5));
                shanshuo();
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "")
                        + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
            }
        });
        bindViewData();
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }

    //绑定数据
    private void bindViewData() {
        if (mspProtocol == null) return;
        int lPresureCurVal = mspProtocol.getlPresureCurVal() & 0xff;
        int rPresureCurVal = mspProtocol.getrPresureCurVal() & 0xff;
        tvLCurHardless.setText(String.format(Locale.getDefault(), "%d", lPresureCurVal));
        tvRCurHardness.setText(String.format(Locale.getDefault(), "%d", rPresureCurVal));

        if (isSwitch) {
            leftIndex = (int) Math.ceil((double) lPresureCurVal / 5);
            leftIndex = leftIndex < 1 ? 1 : leftIndex > 20 ? 20 : leftIndex;
            if (isNeedRefreshLevel) {
                leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
                tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), leftIndex * 5));
            }
        } else {
            rightIndex = (int) Math.ceil((double) rPresureCurVal / 5);
            rightIndex = rightIndex < 1 ? 1 : rightIndex > 20 ? 20 : rightIndex;
            if (isNeedRefreshLevel) {
                rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), rightIndex * 5));
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.iv_jia:
            case R.id.iv_jian:
                isNeedRefreshLevel = false;//停止档位数据更新
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.togglebutton:
                isNeedRefreshLevel = true;//档位数据更新
                if (isSwitch) {
                    isSwitch = false;
                    togglebutton.setChecked(true);
                    leftSeekbar.setVisibility(View.GONE);
                    rightSeekbar.setVisibility(View.VISIBLE);
                } else {
                    isSwitch = true;
                    leftSeekbar.setVisibility(View.VISIBLE);
                    rightSeekbar.setVisibility(View.GONE);
                    togglebutton.setChecked(false);
                }
                bindViewData();
                break;
            case R.id.iv_jian:
                if (isSwitch) {
                    if (leftIndex > 1) {
                        leftIndex--;
                        leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
                        tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), leftIndex * 5));
                    }
                } else {
                    if (rightIndex > 1) {
                        rightIndex--;
                        rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                        tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), rightIndex * 5));
                    }
                }
                LogUtil.e("rightIndex:" + rightIndex + "leftIndex:" + leftIndex);
                shanshuo();
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
            case R.id.iv_jia:
                if (isSwitch) {
                    if (leftIndex < 20) {
                        leftIndex++;
                        leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
                        tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), leftIndex * 5));
                    }
                } else {
                    if (rightIndex < 20) {
                        rightIndex++;
                        rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                        tvGear.setText(String.format(Locale.getDefault(), getResources().getString(R.string.gear_val), rightIndex * 5));
                    }
                }
                shanshuo();
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
        }
    }

    public void shanshuo() {
        Animation alphaAnimation = new AlphaAnimation(1, 0.4f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        llChongqi.startAnimation(alphaAnimation);
    }
}
