package com.jx.sleep_dg.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.DeviceNetConfigAcyivity;
import com.jx.sleep_dg.ui.SearchActivity;
import com.jx.sleep_dg.ui.StatisticsActivity;
import com.jx.sleep_dg.ui.UserInfoActivity;
import com.jx.sleep_dg.utils.Constance;
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

    private ImageView ivHardness;
    private TextView tvCurL, tvCurR;
    private ImageView ivUserImage, ivBle, ivWiFi;
    private ImageView ivLAdd, ivLDecrease;
    private ImageView ivRAdd, ivRDecrease;

    private BorderButton tvLCurHardless;
    private BorderButton tvRCurHardness;

    private AnimationDrawable animationDrawableL, animationDrawableR;
    private CountDownTimer countDownTimer;

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

        ivHardness = view.findViewById(R.id.iv_hardness);
        ivUserImage = view.findViewById(R.id.iv_user_image);
        ivBle = view.findViewById(R.id.iv_ble);
        ivWiFi = view.findViewById(R.id.iv_wifi);
        if (!_mActivity.getApplication().getApplicationInfo().packageName.equals(Constance.QM)) {
            ivUserImage.setVisibility(View.INVISIBLE);
            ivBle.setVisibility(View.INVISIBLE);
            ivWiFi.setVisibility(View.INVISIBLE);
        }
        ivUserImage.setOnClickListener(this);
        ivBle.setOnClickListener(this);
        ivWiFi.setOnClickListener(this);

        tvLCurHardless = view.findViewById(R.id.tv_lcur_hardless);
        tvRCurHardness = view.findViewById(R.id.tv_rcur_hardness);

        tvCurL = view.findViewById(R.id.tv_cur_l);
        tvCurR = view.findViewById(R.id.tv_cur_r);

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
                tvCurL.setText(String.format(Locale.getDefault(), "%d", leftIndex * 5));

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
                tvCurR.setText(String.format(Locale.getDefault(), "%d", rightIndex * 5));

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

        leftIndex = (int) Math.ceil((double) lPresureCurVal / 5);
        leftIndex = leftIndex < 1 ? 1 : leftIndex > 20 ? 20 : leftIndex;

        rightIndex = (int) Math.ceil((double) rPresureCurVal / 5);
        rightIndex = rightIndex < 1 ? 1 : rightIndex > 20 ? 20 : rightIndex;

        if (!isInitSeekbarVal) {
            isInitSeekbarVal = true;
            leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
            rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));

            tvCurL.setText(String.format(Locale.getDefault(), "%d", lPresureCurVal));
            tvCurR.setText(String.format(Locale.getDefault(), "%d", rPresureCurVal));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int lSetIndex,rSetIndex;
        switch (view.getId()) {
            case R.id.iv_user_image:
                _mActivity.startActivity(new Intent(_mActivity, UserInfoActivity.class));
                break;
            case R.id.iv_ble:
                _mActivity.startActivity(new Intent(_mActivity, SearchActivity.class));
                break;
            case R.id.iv_wifi:
                _mActivity.startActivity(new Intent(_mActivity, DeviceNetConfigAcyivity.class));
                break;
            case R.id.iv_jian_l:
                if (leftIndex > 1) {
                    leftIndex--;
                    leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
                    tvCurL.setText(String.format(Locale.getDefault(), "%d", leftIndex * 5));
                }
                shanshuo(true);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
            case R.id.iv_jia_l:
                if (leftIndex < 20) {
                    leftIndex++;
                    leftSeekbar.setProgress(Double.valueOf(leftIndex + ""));
                    tvCurL.setText(String.format(Locale.getDefault(), "%d", leftIndex * 5));
                }
                shanshuo(true);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
            case R.id.iv_jian_r:
                if (rightIndex > 1) {
                    rightIndex--;
                    rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                    tvCurR.setText(String.format(Locale.getDefault(), "%d", rightIndex * 5));
                }
                shanshuo(false);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
            case R.id.iv_jia_r:
                if (rightIndex < 20) {
                    rightIndex++;
                    rightSeekbar.setProgress(Double.valueOf(rightIndex + ""));
                    tvCurR.setText(String.format(Locale.getDefault(), "%d", rightIndex * 5));
                }
                shanshuo(false);
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(leftIndex * 5 + "") + BleUtils.convertDecimalToBinary(rightIndex * 5 + ""));
                break;
        }
        LogUtil.i("rightIndex:" + rightIndex + "leftIndex:" + leftIndex);
    }

    private void shanshuo(boolean isLeft) {
        if (isLeft) {
            onLHardness();
        } else {
            onRHardness();
        }
        stopShan();
    }

    //左边软硬变化
    private void onLHardness() {
        ivHardness.setImageResource(R.drawable.anim_hardness_l);
        animationDrawableL = (AnimationDrawable) ivHardness.getDrawable();
        animationDrawableL.start();
    }

    //右边软硬变化
    private void onRHardness() {
        ivHardness.setImageResource(R.drawable.anim_hardness_r);
        animationDrawableR = (AnimationDrawable) ivHardness.getDrawable();
        animationDrawableR.start();
    }

    //一段时间后停止闪烁
    private void stopShan() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                ivHardness.setImageResource(R.mipmap.pic_bed);
                countDownTimer.cancel();
            }
        };
        countDownTimer.start();
    }
}
