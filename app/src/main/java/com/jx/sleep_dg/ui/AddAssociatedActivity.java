package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jx.sleep_dg.R;

/**
 * 添加关联
 */

public class AddAssociatedActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_add_associated);
        bindView();
    }

    @Override
    public void bindView() {
        setTitleLayoutVisiable(false);

    }
}
