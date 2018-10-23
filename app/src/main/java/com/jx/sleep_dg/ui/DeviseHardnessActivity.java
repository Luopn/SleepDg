package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleComUtils;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.MySeekBar;

import java.util.Locale;

/**
 * 设备硬度
 */
public class DeviseHardnessActivity extends BaseActivity implements View.OnClickListener {
    private boolean isSwitch = true;
    private MySeekBar leftSeekbar;
    private MySeekBar rightSeekbar;
    private ToggleButton togglebutton;

    private TextView tvMemHardless;
    private TextView tvCurHardness;
    private TextView tvGear;
    private LinearLayout llChongqi;

    private int rightIndex = 1;
    private int leftIndex = 1;

    private MSPProtocol mspProtocol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_hardness);
        mspProtocol = MSPProtocol.getInstance();
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle("床位硬度");
        tvMemHardless = findViewById(R.id.tv_mem_hardless);
        tvCurHardness = findViewById(R.id.tv_cur_hardness);
        tvGear = findViewById(R.id.tv_gear);
        llChongqi = findViewById(R.id.ll_chongqi);
        findViewById(R.id.iv_jian).setOnClickListener(this);
        findViewById(R.id.iv_jia).setOnClickListener(this);
        togglebutton = findViewById(R.id.togglebutton);
        togglebutton.setOnClickListener(this);
        leftSeekbar = findViewById(R.id.left_seekbar);
        rightSeekbar = findViewById(R.id.right_seekbar);

        leftSeekbar.setSeekBarClickListener(new MySeekBar.onSeekBarClickListener() {
            @Override
            public void onSeekBarClick(int position) {
                leftIndex = position;
                tvGear.setText(String.format(Locale.getDefault(), "档位%d", position));
                shanshuo();
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "")
                        + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
            }
        });
        rightSeekbar.setSeekBarClickListener(new MySeekBar.onSeekBarClickListener() {
            @Override
            public void onSeekBarClick(int position) {
                rightIndex = position;
                tvGear.setText(String.format(Locale.getDefault(), "档位%d", position));
                shanshuo();
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "")
                        + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
            }
        });
        bindViewData();
    }

    @Override
    protected void notifyUIDataSetChanged() {
        super.notifyUIDataSetChanged();
        bindViewData();
    }

    //绑定数据
    private void bindViewData() {
        if (mspProtocol == null) return;
        if (isSwitch) {
            tvMemHardless.setText(String.format("左床记忆强度：%s", mspProtocol.getlPresureMemVal()));
            tvCurHardness.setText(String.format("左床实时强度：%s", mspProtocol.getlPresureCurVal()));
            tvGear.setText(String.format(Locale.getDefault(), "档位%d", leftIndex));
        } else {
            tvMemHardless.setText(String.format("右床记忆强度：%s", mspProtocol.getrPresureMemVal()));
            tvCurHardness.setText(String.format("右床实时强度：%s", mspProtocol.getrPresureCurVal()));
            tvGear.setText(String.format(Locale.getDefault(), "档位%d", rightIndex));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.togglebutton:
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
                        tvGear.setText(String.format(Locale.getDefault(), "档位%d", leftIndex));
                    }
                } else {
                    if (rightIndex > 1) {
                        rightIndex--;
                        rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                        tvGear.setText(String.format(Locale.getDefault(), "档位%d", rightIndex));
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
                        tvGear.setText(leftIndex + "");
                    }
                } else {
                    if (rightIndex < 20) {
                        rightIndex++;
                        rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                        tvGear.setText(rightIndex + "");
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
