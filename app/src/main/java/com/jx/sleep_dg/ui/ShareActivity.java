package com.jx.sleep_dg.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.view.NumberRollingView;

public class ShareActivity extends Activity {

    public static final String KEY_SLEEP_SCORE = "com.sleepdg.sleepscores";
    private NumberRollingView tvSleepScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        bindView();
    }

    public void bindView() {
        tvSleepScore = findViewById(R.id.tv_sleep_score);
        tvSleepScore.startNumAnim(getIntent().getStringExtra(KEY_SLEEP_SCORE));
    }
}
