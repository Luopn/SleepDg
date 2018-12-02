package com.jx.sleep_dg.fragment;

import android.os.Bundle;
import android.view.View;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;

/**
 * Created by dingt on 2018/7/25.
 */

public class WeeklyDataFragment extends BaseMainFragment {
    public WeeklyDataFragment() {
    }

    public static WeeklyDataFragment newInstance() {
        WeeklyDataFragment fragment = new WeeklyDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weeky;
    }

    @Override
    public void onBindView(View view) {

    }
}
