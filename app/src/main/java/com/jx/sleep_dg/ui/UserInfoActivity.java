package com.jx.sleep_dg.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.utils.QMUIStatusBarHelper;
import com.jx.sleep_dg.utils.StatusBarUtil;

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
        setTitleCol(Color.WHITE);
        setToolbarBackColor(ContextCompat.getColor(this, R.color.textTitleColor));
        StatusBarUtil.setColor(this,ContextCompat.getColor(this, R.color.textTitleColor),255);
    }
}
