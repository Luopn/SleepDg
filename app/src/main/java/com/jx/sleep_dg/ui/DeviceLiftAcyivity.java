package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.SegmentControl;
import com.jx.sleep_dg.view.WheelView;
import com.jx.sleep_dg.view.bar.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备升降
 */

public class DeviceLiftAcyivity extends BaseActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, SegmentControl.OnSegmentChangedListener {

    private MSPProtocol mspProtocol;

    private SegmentControl togglebutton;
    private SeekBar seebLeftTou, seebRightTou, seebLeftJiao, seebRightJiao;
    private TextView tvZuoTou, tvZuoJiao, tvYouTou, tvYouJiao;
    private ImageView ivZuoChuang, ivYouChuang;

    private boolean isSwitch = false;
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
        togglebutton.setOnSegmentChangedListener(this);

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

        seebLeftTou.setOnSeekBarChangeListener(this);
        seebRightTou.setOnSeekBarChangeListener(this);
        seebLeftJiao.setOnSeekBarChangeListener(this);
        seebRightJiao.setOnSeekBarChangeListener(this);

        initWheelView();

        bindViewData();
    }

    private void bindViewData() {
        if (mspProtocol != null) {
            tvZuoTou.setText(String.format(getResources().getString(R.string.left_value), mspProtocol.getHigh2() & 0xff));
            tvYouTou.setText(String.format(getResources().getString(R.string.right_value), mspProtocol.getHigh2() & 0xff));
            tvZuoJiao.setText(String.format(getResources().getString(R.string.left_value), mspProtocol.getHigh1() & 0xff));
            tvYouJiao.setText(String.format(getResources().getString(R.string.right_value), mspProtocol.getHigh1() & 0xff));
        }
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }

    @Override
    public void onSegmentChanged(int newSelectedIndex) {
        isSwitch = newSelectedIndex == 1;
        if (isSwitch) {
            seebRightJiao.setVisibility(View.VISIBLE);
            seebRightTou.setVisibility(View.VISIBLE);
            ivYouChuang.setVisibility(View.VISIBLE);

            seebLeftJiao.setVisibility(View.GONE);
            seebLeftTou.setVisibility(View.GONE);
            ivZuoChuang.setVisibility(View.GONE);
        } else {
            seebLeftJiao.setVisibility(View.VISIBLE);
            seebLeftTou.setVisibility(View.VISIBLE);
            ivZuoChuang.setVisibility(View.VISIBLE);

            seebRightJiao.setVisibility(View.GONE);
            seebRightTou.setVisibility(View.GONE);
            ivYouChuang.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
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
            //右
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
        } else {
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
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (seekBar.getId() == R.id.seeb_left_tou) {
            leftTouIndex = progress;
            LogUtil.e("leftTouIndex:" + leftTouIndex);
        } else if (seekBar.getId() == R.id.seeb_left_jiao) {
            leftJiaoIndex = progress;
            LogUtil.e("leftJiaoIndex:" + leftJiaoIndex);

        } else if (seekBar.getId() == R.id.seeb_right_tou) {
            rightTouIndex = progress;
            LogUtil.e("rightTouIndex:" + rightTouIndex);

        } else if (seekBar.getId() == R.id.seeb_right_jiao) {
            rightJiaoIndex = progress;
            LogUtil.e("rightJiaoIndex:" + rightJiaoIndex);

        }
        chang();
        BleComUtils.senddianji(BleUtils.convertDecimalToBinary((leftTouIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((leftJiaoIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((rightTouIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((rightJiaoIndex + 1) + ""));
    }

    private void initWheelView(){
        WheelView mWheelView3 = findViewById(R.id.wheelview3);
        List<String> items3 = new ArrayList<>();
        items3.add("1");
        items3.add("2");
        items3.add("3");
        items3.add("4");
        items3.add("5");
        items3.add("5");
        items3.add("6");
        items3.add("7");
        items3.add("8");
        items3.add("9");
        items3.add("10");
        items3.add("11");
        items3.add("12");
        items3.add("13");
        items3.add("14");
        items3.add("15");
        items3.add("16");
        items3.add("17");
        items3.add("18");

        mWheelView3.setItems(items3);
        mWheelView3.setAdditionCenterMark("档");
    }
}
