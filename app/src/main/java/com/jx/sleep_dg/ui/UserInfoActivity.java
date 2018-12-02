package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;

/**
 * 个人信息
 */

public class UserInfoActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_user_info);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.user_info);
    }
}
