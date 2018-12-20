package com.jx.sleep_dg.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
public class DeviseHardnessFragment extends BaseMainFragment implements View.OnClickListener {

    private boolean isInitSeekbarVal;

    private MySeekBar leftSeekbar;
    private MySeekBar rightSeekbar;

    private TextView tvCurL, tvCurR;

    private ImageView ivLAdd, ivLDecrease;
    private ImageView ivRAdd, ivRDecrease;

    private BorderButton tvLCurHardless;
    private BorderButton tvRCurHardness;
    private LinearLayout llChongqiL,llChongqiR;

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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isInitSeekbarVal = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bindView(View view) {
        ScrollView mScrollView = view.findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
        tvLCurHardless = view.findViewById(R.id.tv_lcur_hardless);
        tvRCurHardness = view.findViewById(R.id.tv_rcur_hardness);

        tvCurL = view.findViewById(R.id.tv_cur_l);
        tvCurR = view.findViewById(R.id.tv_cur_r);

        llChongqiL = view.findViewById(R.id.ll_chongqi_l);
        llChongqiR = view.findViewById(R.id.ll_chongqi_r);
        ivLAdd = view.findViewById(R.id.iv_jia_l);
        ivLDecrease = view.findViewById(R.id.iv_jian_l);
        ivRAdd = view.findViewById(R.id.iv_jia_r);
        ivRDecrease = view.findViewById(R.id.iv_jian_r);
        leftSeekbar = view.findViewById(R.id.left_seekbar);
        rightSeekbar = view.findViewById(R.id.right_seekbar);

        ivLAdd.setOnClickListener(this);
        ivLDecrease.setOnClickListener(this);
        ivRAdd.setOnClickListener(this);
        ivRDecrease.setOnClickListener(this);

        leftSeekbar.setSeekBarClickListener(new MySeekBar.onSeekBarClickListener() {
            @Override
            public void onSeekBarClick(int position) {
                leftIndex = position;
                shanshuo(true);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "")
                        + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                LogUtil.i("rightIndex:" + rightIndex + "leftIndex:" + leftIndex);
            }
        });
        rightSeekbar.setSeekBarClickListener(new MySeekBar.onSeekBarClickListener() {
            @Override
            public void onSeekBarClick(int position) {
                rightIndex = position;
                shanshuo(false);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "")
                        + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                LogUtil.i("rightIndex:" + rightIndex + "leftIndex:" + leftIndex);
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
        tvCurL.setText(String.format(Locale.getDefault(), "%d", lPresureCurVal));
        tvCurR.setText(String.format(Locale.getDefault(), "%d", rPresureCurVal));

        leftIndex = (int) Math.ceil((double) lPresureCurVal / 5);
        leftIndex = leftIndex < 1 ? 1 : leftIndex > 20 ? 20 : leftIndex;

        rightIndex = (int) Math.ceil((double) rPresureCurVal / 5);
        rightIndex = rightIndex < 1 ? 1 : rightIndex > 20 ? 20 : rightIndex;

        if (!isInitSeekbarVal) {
            isInitSeekbarVal = true;
            leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
            rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_jian_l:
                if (leftIndex > 1) {
                    leftIndex--;
                    leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
                }
                shanshuo(true);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
            case R.id.iv_jia_l:
                if (leftIndex < 20) {
                    leftIndex++;
                    leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
                }
                shanshuo(true);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
            case R.id.iv_jian_r:
                if (rightIndex > 1) {
                    rightIndex--;
                    rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                }
                shanshuo(false);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
            case R.id.iv_jia_r:
                if (rightIndex < 20) {
                    rightIndex++;
                    rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                }
                shanshuo(false);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
        }
        LogUtil.i("rightIndex:" + rightIndex + "leftIndex:" + leftIndex);
    }

    private void shanshuo(boolean isLeft) {
        Animation alphaAnimation = new AlphaAnimation(1, 0.1f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(2);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        if(isLeft) {
            llChongqiL.startAnimation(alphaAnimation);
        }else{
            llChongqiR.startAnimation(alphaAnimation);
        }
    }
}
