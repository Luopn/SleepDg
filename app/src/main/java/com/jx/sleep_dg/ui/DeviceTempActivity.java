package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleComUtils;
import com.jx.sleep_dg.utils.LogUtil;

/**
 * 床位温度
 */

public class DeviceTempActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvGear;
    private ImageView ivChuang;
    private ImageView ivGear;

    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_temp);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.temperature);
        tvGear = findViewById(R.id.tv_gear);
        ivChuang = findViewById(R.id.iv_chuang);
        ivGear = findViewById(R.id.iv_gear);
        findViewById(R.id.iv_jian).setOnClickListener(this);
        findViewById(R.id.iv_jia).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_jian:
                //if (index == 0) {
                //    ToastUtil.showMessage("请先打开开关");
                //    return;
                //}
                if (index > 0) {
                    index--;
                    switch (index) {
                        case 0:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_0);
                            ivGear.setBackgroundResource(R.mipmap.control_heat0);
                            tvGear.setText(R.string.off);

                            break;
                        case 1:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_1);
                            ivGear.setBackgroundResource(R.mipmap.control_heat1);
                            tvGear.setText(R.string.low);

                            break;
                        case 2:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_2);
                            ivGear.setBackgroundResource(R.mipmap.control_heat2);
                            tvGear.setText(R.string.low);

                            break;
                        case 3:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_3);
                            ivGear.setBackgroundResource(R.mipmap.control_heat3);
                            tvGear.setText(R.string.mid);

                            break;
                        case 4:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_4);
                            ivGear.setBackgroundResource(R.mipmap.control_heat4);
                            tvGear.setText(R.string.high);

                            break;
                        case 5:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_5);
                            ivGear.setBackgroundResource(R.mipmap.control_heat5);
                            tvGear.setText(R.string.high);

                            break;
                    }
                    BleComUtils.sendJiare(index + "");
                    LogUtil.e("index:" + index);

                }
                break;
            case R.id.iv_jia:
//                if (index == 0) {
//                    ToastUtil.showMessage("请先打开开关");
//                    return;
//                }
                if (index < 6) {
                    index++;
                    switch (index) {
                        case 1:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_1);
                            ivGear.setBackgroundResource(R.mipmap.control_heat1);
                            tvGear.setText(R.string.low);
                            break;
                        case 2:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_2);
                            ivGear.setBackgroundResource(R.mipmap.control_heat2);
                            tvGear.setText(R.string.low);
                            break;
                        case 3:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_3);
                            ivGear.setBackgroundResource(R.mipmap.control_heat3);
                            tvGear.setText(R.string.mid);
                            break;
                        case 4:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_4);
                            ivGear.setBackgroundResource(R.mipmap.control_heat4);
                            tvGear.setText(R.string.high);
                            break;
                        case 5:
                            ivChuang.setBackgroundResource(R.mipmap.warmbed_5);
                            ivGear.setBackgroundResource(R.mipmap.control_heat5);
                            tvGear.setText(R.string.high);
                            break;
                    }
                    LogUtil.e("index:" + index);
                    BleComUtils.sendJiare(index + "");
                }

                break;
        }
    }
}
