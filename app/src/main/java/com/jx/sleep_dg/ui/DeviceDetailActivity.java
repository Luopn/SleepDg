package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jx.sleep_dg.R;

/**
 * 设备信息
 * Created by Administrator on 2018/7/21.
 */

public class DeviceDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_device_detail);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.mattress_info);
    }
}
