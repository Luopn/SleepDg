package com.jx.sleep_dg.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.view.bar.AnimatedProgressBar;
import com.jx.sleep_dg.view.NumberRollingView;

import java.util.Locale;

public class GaugeActivity extends BaseActivity {

    private static final String TAG = "GaugeActivity";
    private static final int SEARCH_DURATION = 2000;
    private static final int SEARCH_REACPEAT_COUNT = 30;

    private int gauge_res = 65;

    private Button btn_start_gauge;
    private TextView tvHint, tv_gauge_val;
    private AnimatedProgressBar pb_result;
    private NumberRollingView gaugeRes;
    private EditText etHeight, etWeight, etYear, etMonth, etDay, etGender;

    private ObjectAnimator rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_gauge);
        setToolbarTitle("一键检测");
        bindView();
    }

    @Override
    public void bindView() {
        ImageView iv_gauge = findViewById(R.id.iv_gauge);
        tvHint = findViewById(R.id.tv_gauge_hint);
        tvHint.setVisibility(View.GONE);

        btn_start_gauge = findViewById(R.id.btn_start_gauge);
        tv_gauge_val = findViewById(R.id.tv_gauge_val);
        pb_result = findViewById(R.id.pb_result);

        gaugeRes = findViewById(R.id.nrv_gauge);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etYear = findViewById(R.id.et_year);
        etMonth = findViewById(R.id.et_month);
        etDay = findViewById(R.id.et_day);
        etGender = findViewById(R.id.et_gender);
        etGender.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    btn_start_gauge.performClick();
                }
                return false;
            }
        });

        btn_start_gauge.setOnClickListener(this);

        rotate = ObjectAnimator.ofFloat(iv_gauge, "rotation", 0, 359).
                setDuration(SEARCH_DURATION);
        rotate.setRepeatCount(SEARCH_REACPEAT_COUNT);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatMode(ObjectAnimator.RESTART);
        rotate.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                pb_result.setProgress(0);
                gaugeRes.startNumAnim("0");

                setEditTextEnable(false);
                tvHint.setVisibility(View.VISIBLE);

                btn_start_gauge.setAlpha(0.5f);
                btn_start_gauge.setEnabled(false);
                new CountDownTimer(SEARCH_DURATION * SEARCH_REACPEAT_COUNT, 600) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int progress = (int) ((SEARCH_DURATION * SEARCH_REACPEAT_COUNT - millisUntilFinished) / 600);
                        tv_gauge_val.setText(String.format(Locale.getDefault(), "%d", progress));

                        updateUI(progress);
                    }

                    @Override
                    public void onFinish() {
                        rotate.end();
                        gaugeRes.startNumAnim(String.valueOf(gauge_res));
                        pb_result.setProgress(gauge_res);
                        cancel();
                    }
                }.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setEditTextEnable(true);
                tvHint.setVisibility(View.GONE);

                btn_start_gauge.setAlpha(1.0f);
                btn_start_gauge.setEnabled(true);
                tv_gauge_val.setText(String.format(Locale.getDefault(), "%d", 100));
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setEditTextEnable(true);
                tvHint.setVisibility(View.GONE);

                btn_start_gauge.setAlpha(1.0f);
                btn_start_gauge.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void updateUI(int progress) {
        if (progress == 5) {
            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("100")
                    + BleUtils.convertDecimalToBinary("100"));

        } else if (progress == 30) {

            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("40")
                    + BleUtils.convertDecimalToBinary("40"));

        } else if (progress == 50) {

            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("80")
                    + BleUtils.convertDecimalToBinary("80"));

        } else if (progress == 70) {

            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(String.valueOf(gauge_res))
                    + BleUtils.convertDecimalToBinary(String.valueOf(gauge_res)));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_start_gauge:
                boolean res = caculateLevel(etGender.getText().toString(), etWeight.getText().toString());
                if (res)
                    rotate.start();
                break;
        }
    }

    private boolean caculateLevel(String gender, String weightStr) {
        if (TextUtils.isEmpty(gender)) {
            etWeight.requestFocus();
            etWeight.setError("请填写您的体重");
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            etGender.requestFocus();
            etGender.setError("请填写您的性别");
            return false;
        }
        int weight = Integer.valueOf(weightStr);
        if (gender.equals("女")) {
            if (weight >= 30 & weight < 50) {
                gauge_res = 60;
            } else if (weight >= 50 & weight < 60) {
                gauge_res = 65;
            } else if (weight >= 60 & weight < 70) {
                gauge_res = 70;
            } else if (weight >= 70 & weight < 80) {
                gauge_res = 75;
            } else if (weight >= 80) {
                gauge_res = 80;
            }
        } else {
            if (weight >= 50 & weight < 60) {
                gauge_res = 70;
            } else if (weight >= 60 & weight < 70) {
                gauge_res = 75;
            } else if (weight >= 70 & weight < 80) {
                gauge_res = 80;
            } else if (weight >= 80 & weight < 90) {
                gauge_res = 85;
            } else if (weight >= 90) {
                gauge_res = 90;
            }
        }
        return true;
    }

    private void setEditTextEnable(boolean enable) {
        etHeight.setEnabled(enable);
        etWeight.setEnabled(enable);
        etYear.setEnabled(enable);
        etMonth.setEnabled(enable);
        etDay.setEnabled(enable);
        etGender.setEnabled(enable);
    }
}
