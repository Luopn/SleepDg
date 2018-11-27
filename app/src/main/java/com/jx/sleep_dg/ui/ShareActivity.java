package com.jx.sleep_dg.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.view.NumberRollingView;

public class ShareActivity extends Activity implements View.OnClickListener{

    public static final String KEY_SLEEP_SCORE = "com.sleepdg.sleepscores";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        bindView();
    }

    public void bindView() {
        ConstraintLayout container = findViewById(R.id.container);
        container.setOnClickListener(this);
        NumberRollingView tvSleepScore = findViewById(R.id.tv_sleep_score);
        tvSleepScore.startNumAnim(getIntent().getStringExtra(KEY_SLEEP_SCORE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.container:
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
