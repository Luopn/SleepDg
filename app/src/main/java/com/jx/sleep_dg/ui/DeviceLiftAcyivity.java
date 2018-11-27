package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.Ruler;
import com.jx.sleep_dg.view.bar.VerticalSeekBar;

/**
 * 设备升降
 */

public class DeviceLiftAcyivity extends BaseActivity implements View.OnClickListener, VerticalSeekBar.SlideChangeListener {

    private MSPProtocol mspProtocol;

    private VerticalSeekBar seebLeftTou;
    private VerticalSeekBar seebLeftJiao;
    private Ruler tvTou, tvJiao;
    private ImageView ivZuoChuang;

    private int touIndex = 0;
    private int jiaoIndex = 0;

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

        findViewById(R.id.iv_tou_jia).setOnClickListener(this);
        findViewById(R.id.iv_tou_jian).setOnClickListener(this);
        findViewById(R.id.iv_jiao_jia).setOnClickListener(this);
        findViewById(R.id.iv_jiao_jian).setOnClickListener(this);

        tvTou = findViewById(R.id.ruler_tou);
        tvJiao = findViewById(R.id.ruler_jiao);

        seebLeftTou = findViewById(R.id.seeb_left_tou);
        seebLeftJiao = findViewById(R.id.seeb_left_jiao);

        ivZuoChuang = findViewById(R.id.iv_zuo_chuang);

        seebLeftTou.setThumbSizePx(56, 56);
        seebLeftTou.setOnSlideChangeListener(this);
        seebLeftJiao.setThumbSizePx(56, 56);
        seebLeftJiao.setOnSlideChangeListener(this);

        bindViewData();
    }

    private void bindViewData() {
        if (mspProtocol != null) {
            tvTou.setValue((int) Math.ceil((mspProtocol.getHigh2() & 0xff) / 18 * 45.0f));
            tvJiao.setValue((int) Math.ceil((mspProtocol.getHigh1() & 0xff) / 18 * 45.0f));
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
            case R.id.iv_tou_jia:
                if (touIndex < 17) {
                    touIndex++;
                    seebLeftTou.setProgress(touIndex);
                    LogUtil.e("左  leftTouIndex:" + touIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
            case R.id.iv_tou_jian:
                if (touIndex > 0) {
                    touIndex--;
                    seebLeftTou.setProgress(touIndex);
                    LogUtil.e("左  leftJiaoIndex:" + touIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
            case R.id.iv_jiao_jia:
                //左
                if (jiaoIndex < 17) {
                    jiaoIndex++;
                    seebLeftJiao.setProgress(jiaoIndex);
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
            case R.id.iv_jiao_jian:
                //左
                if (jiaoIndex > 0) {
                    jiaoIndex--;
                    seebLeftJiao.setProgress(jiaoIndex);
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
        }
    }

    public void chang() {
        //左
        if (touIndex < 6) {
            if (jiaoIndex < 6) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot1);
            } else if (jiaoIndex < 12) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot3);

            } else if (jiaoIndex <= 17) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot4);

            }
        }
        if (6 <= touIndex && touIndex < 12) {
            if (jiaoIndex < 6) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot1);
            } else if (6 <= jiaoIndex && jiaoIndex < 12) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot3);

            } else if (12 <= jiaoIndex && jiaoIndex <= 17) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot4);

            }
        } else if (12 <= touIndex && touIndex <= 17) {
            if (jiaoIndex < 6) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot1);
            } else if (6 <= jiaoIndex && jiaoIndex < 12) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot3);

            } else if (12 <= jiaoIndex && jiaoIndex <= 17) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot4);
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
            touIndex = progress;
            LogUtil.e("leftTouIndex:" + touIndex);
        } else if (slideView.getId() == R.id.seeb_left_jiao) {
            jiaoIndex = progress;
            LogUtil.e("leftJiaoIndex:" + jiaoIndex);

        }
        chang();
        BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

    }
}
