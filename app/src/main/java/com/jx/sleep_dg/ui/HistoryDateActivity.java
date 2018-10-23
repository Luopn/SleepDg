package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.fragment.MonthDataFragment;
import com.jx.sleep_dg.fragment.SupportFragment;
import com.jx.sleep_dg.fragment.TodayDataFragment;
import com.jx.sleep_dg.fragment.WeeklyDataFragment;

/**
 * 历史数据
 */

public class HistoryDateActivity extends BaseActivity implements View.OnClickListener {
    private SupportFragment[] mFragments = new SupportFragment[3];

    private int position = 0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_history_date);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle("睡眠统计");
        TextView tvTitle = findViewById(R.id.tv_history_title);
        tvTitle.setOnClickListener(this);
        findViewById(R.id.fl_history_right).setOnClickListener(this);
        findViewById(R.id.fl_history_back).setOnClickListener(this);
        if (getBundle() == null) {
            mFragments[0] = MonthDataFragment.newInstance();
            mFragments[1] = MonthDataFragment.newInstance();
            mFragments[2] = WeeklyDataFragment.newInstance();
            getSupportDelegate().loadMultipleRootFragment(R.id.fl_content, 0,
                    mFragments[0],
                    mFragments[1],
                    mFragments[2]);
        } else {
            mFragments[0] = findFragment(TodayDataFragment.class);
            mFragments[1] = findFragment(MonthDataFragment.class);
            mFragments[2] = findFragment(WeeklyDataFragment.class);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.fl_history_back:
                finish();
                break;
            case R.id.fl_history_right:
//                position++;
//                if (position == 3) {
//                    position = 0;
                    showHideFragment(mFragments[0]);
//                    tv_data_title.setText(getString(R.string.today_data));
//                } else if (position == 1) {
//                    showHideFragment(mFragments[1]);
////                    tv_data_title.setText(getString(R.string.mouth_week));
//                } else if (position == 2) {
//                    showHideFragment(mFragments[2]);
////                    tv_data_title.setText(getString(R.string.week_data));
//                }
                break;
        }
    }
}
