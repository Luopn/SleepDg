package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.bar.VerticalSeekBar;

/**
 * 设备升降
 */

public class DeviceLiftAcyivity extends BaseActivity implements View.OnClickListener, VerticalSeekBar.SlideChangeListener {

    private MSPProtocol mspProtocol;

    private ToggleButton togglebutton;
    private VerticalSeekBar seebLeftTou;
    private VerticalSeekBar seebRightTou;
    private VerticalSeekBar seebLeftJiao;
    private VerticalSeekBar seebRightJiao;
    private TextView tvZuoTou;
    private TextView tvZuoJiao;
    private TextView tvYouTou;
    private TextView tvYouJiao;
    private ImageView ivZuoChuang;
    private ImageView ivYouChuang;

    private boolean isSwitch = true;
    private int rightTouIndex = 0;
    private int rightJiaoIndex = 0;
    private int leftTouIndex = 0;
    private int leftJiaoIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_device_lift);
        mspProtocol = MSPProtocol.getInstance();
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.lift_ctrl);
        togglebutton = findViewById(R.id.togglebutton);
        togglebutton.setOnClickListener(this);


        findViewById(R.id.iv_tou_jia).setOnClickListener(this);
        findViewById(R.id.iv_tou_jian).setOnClickListener(this);
        findViewById(R.id.iv_jiao_jia).setOnClickListener(this);
        findViewById(R.id.iv_jiao_jian).setOnClickListener(this);

        seebLeftTou = findViewById(R.id.seeb_left_tou);
        seebRightTou = findViewById(R.id.seeb_right_tou);
        seebLeftJiao = findViewById(R.id.seeb_left_jiao);
        seebRightJiao = findViewById(R.id.seeb_right_jiao);
        ivZuoChuang = findViewById(R.id.iv_zuo_chuang);
        ivYouChuang = findViewById(R.id.iv_you_chuang);

        tvZuoTou = findViewById(R.id.tv_zuo_tou);
        tvZuoJiao = findViewById(R.id.tv_zuo_jiao);
        tvYouTou = findViewById(R.id.tv_you_tou);
        tvYouJiao = findViewById(R.id.tv_you_jiao);

        seebLeftTou.setThumbSizePx(56, 56);
        seebLeftTou.setOnSlideChangeListener(this);

        seebRightTou.setThumbSizePx(56, 56);
        seebRightTou.setOnSlideChangeListener(this);

        seebLeftJiao.setThumbSizePx(56, 56);
        seebLeftJiao.setOnSlideChangeListener(this);

        seebRightJiao.setThumbSizePx(56, 56);
        seebRightJiao.setOnSlideChangeListener(this);

        bindViewData();
    }

    private void bindViewData() {
        if (mspProtocol != null) {
            tvZuoTou.setText(String.format(getResources().getString(R.string.head_value), mspProtocol.getHigh2() & 0xff));
            tvYouTou.setText(String.format(getResources().getString(R.string.head_value), mspProtocol.getHigh2() & 0xff));
            tvZuoJiao.setText(String.format(getResources().getString(R.string.feet_value), mspProtocol.getHigh1() & 0xff));
            tvYouJiao.setText(String.format(getResources().getString(R.string.feet_value), mspProtocol.getHigh1() & 0xff));
        }
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.togglebutton:
                if (isSwitch) {
                    isSwitch = false;
                    togglebutton.setChecked(true);
                    seebRightJiao.setVisibility(View.VISIBLE);
                    seebRightTou.setVisibility(View.VISIBLE);
                    ivYouChuang.setVisibility(View.VISIBLE);

                    seebLeftJiao.setVisibility(View.GONE);
                    seebLeftTou.setVisibility(View.GONE);
                    ivZuoChuang.setVisibility(View.GONE);

                } else {
                    isSwitch = true;
                    togglebutton.setChecked(false);
                    seebLeftJiao.setVisibility(View.VISIBLE);
                    seebLeftTou.setVisibility(View.VISIBLE);
                    ivZuoChuang.setVisibility(View.VISIBLE);

                    seebRightJiao.setVisibility(View.GONE);
                    seebRightTou.setVisibility(View.GONE);
                    ivYouChuang.setVisibility(View.GONE);

                }
                break;
            case R.id.iv_tou_jia:
                if (isSwitch) {
                    //左
                    if (leftTouIndex < 17) {
                        leftTouIndex++;
                        seebLeftTou.setProgress(leftTouIndex);
                        LogUtil.e("左  leftTouIndex:" + leftTouIndex);
                    }
                } else {
                    if (rightTouIndex < 17) {
                        rightTouIndex++;
                        seebRightTou.setProgress(rightTouIndex);
                        LogUtil.e("右 rightTouIndex：" + rightTouIndex);
                    }
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((leftTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((leftJiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightJiaoIndex + 1) + ""));

                break;
            case R.id.iv_tou_jian:
                if (isSwitch) {
                    //左
                    if (leftTouIndex > 0) {
                        leftTouIndex--;
                        seebLeftTou.setProgress(leftTouIndex);
                        LogUtil.e("左  leftJiaoIndex:" + leftTouIndex);
                    }
                } else {
                    if (rightTouIndex > 0) {
                        rightTouIndex--;
                        seebRightTou.setProgress(rightTouIndex);
                        LogUtil.e("右  rightTouIndex:" + rightTouIndex);
                    }
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((leftTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((leftJiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightJiaoIndex + 1) + ""));

                break;
            case R.id.iv_jiao_jia:
                if (isSwitch) {
                    //左
                    if (leftJiaoIndex < 17) {
                        leftJiaoIndex++;
                        seebLeftJiao.setProgress(leftJiaoIndex);
                        LogUtil.e("左  leftJiaoIndex:" + leftJiaoIndex);
                    }
                } else {
                    if (rightJiaoIndex < 17) {
                        rightJiaoIndex++;
                        seebRightJiao.setProgress(rightJiaoIndex);
                        LogUtil.e("右 rightJiaoIndex：" + rightJiaoIndex);
                    }
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((leftTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((leftJiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightJiaoIndex + 1) + ""));

                break;
            case R.id.iv_jiao_jian:
                if (isSwitch) {
                    //左
                    if (leftJiaoIndex > 0) {
                        leftJiaoIndex--;
                        seebLeftJiao.setProgress(leftJiaoIndex);
                        LogUtil.e("左  leftJiaoIndex:" + leftJiaoIndex);
                    }
                } else {
                    if (rightJiaoIndex > 0) {
                        rightJiaoIndex--;
                        seebRightJiao.setProgress(rightJiaoIndex);
                        LogUtil.e("右 rightJiaoIndex：" + rightJiaoIndex);
                    }
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((leftTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((leftJiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightTouIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((rightJiaoIndex + 1) + ""));

                break;
        }
    }

    public void chang() {
        if (isSwitch) {
            //左
            if (leftTouIndex < 6) {
                if (leftJiaoIndex < 6) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot1);
                } else if (leftJiaoIndex < 12) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot3);

                } else if (leftJiaoIndex <= 17) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot4);

                }
            }
            if (6 <= leftTouIndex && leftTouIndex < 12) {
                if (leftJiaoIndex < 6) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot1);
                } else if (6 <= leftJiaoIndex && leftJiaoIndex < 12) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot3);

                } else if (12 <= leftJiaoIndex && leftJiaoIndex <= 17) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot4);

                }
            } else if (12 <= leftTouIndex && leftTouIndex <= 17) {
                if (leftJiaoIndex < 6) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot1);
                } else if (6 <= leftJiaoIndex && leftJiaoIndex < 12) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot3);

                } else if (12 <= leftJiaoIndex && leftJiaoIndex <= 17) {
                    ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot4);

                }
            }
        } else {
            if (rightTouIndex < 6) {
                if (rightJiaoIndex < 6) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head1_foot1);
                } else if (rightJiaoIndex < 12) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head1_foot3);

                } else if (rightJiaoIndex <= 17) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head1_foot4);
                }
            }
            if (6 <= rightTouIndex && rightTouIndex < 12) {

                if (rightJiaoIndex < 6) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head3_foot1);
                } else if (6 <= rightJiaoIndex && rightJiaoIndex < 12) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head3_foot3);

                } else if (12 <= rightJiaoIndex && rightJiaoIndex <= 17) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head3_foot4);

                }
            } else if (12 <= rightTouIndex && rightTouIndex <= 17) {

                if (rightJiaoIndex < 6) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head4_foot1);
                } else if (6 <= rightJiaoIndex && rightJiaoIndex < 12) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head4_foot3);

                } else if (12 <= rightJiaoIndex && rightJiaoIndex <= 17) {
                    ivYouChuang.setBackgroundResource(R.mipmap.head4_foot4);
                }
            }
        }
    }


    @Override
    public void onStart(VerticalSeekBar slideView, int progress) {
    }

    @Override
    public void onProgress(VerticalSeekBar slideView, int progress) {

    }

    @Override
    public void onStop(VerticalSeekBar slideView, int progress) {
        if (slideView.getId() == R.id.seeb_left_tou) {
            leftTouIndex = progress;
            LogUtil.e("leftTouIndex:" + leftTouIndex);
        } else if (slideView.getId() == R.id.seeb_left_jiao) {
            leftJiaoIndex = progress;
            LogUtil.e("leftJiaoIndex:" + leftJiaoIndex);

        } else if (slideView.getId() == R.id.seeb_right_tou) {
            rightTouIndex = progress;
            LogUtil.e("rightTouIndex:" + rightTouIndex);

        } else if (slideView.getId() == R.id.seeb_right_jiao) {
            rightJiaoIndex = progress;
            LogUtil.e("rightJiaoIndex:" + rightJiaoIndex);

        }
        chang();
        BleComUtils.senddianji(BleUtils.convertDecimalToBinary((leftTouIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((leftJiaoIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((rightTouIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((rightJiaoIndex + 1) + ""));

    }
}
