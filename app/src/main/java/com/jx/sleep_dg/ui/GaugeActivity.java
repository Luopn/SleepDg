package com.jx.sleep_dg.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;
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
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.view.bar.AnimatedProgressBar;
import com.jx.sleep_dg.view.NumberRollingView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GaugeActivity extends BaseActivity {

    private static final String TAG = "GaugeActivity";

    private int gauge_res = 65;
    private int time;

    private Button btn_start_gauge;
    private TextView tvHint, tv_gauge_val;
    private AnimatedProgressBar pb_result;
    private NumberRollingView gaugeRes;
    private EditText etHeight, etWeight, etYear, etMonth, etDay, etGender;

    private ObjectAnimator rotate;
    private Timer sendTimer;
    //蓝牙协议
    private MSPProtocol mspProtocol;

    //定义软硬命令发送步骤
    private static final int STEP1 = 1;
    private static final int STEP2 = 2;
    private static final int STEP3 = 3;
    private static final int STEP4 = 4;
    private static final int STEP5 = 5;

    @IntDef({STEP1, STEP2, STEP3, STEP4, STEP5})
    @Retention(RetentionPolicy.SOURCE)
    @interface CmdStep {
    }

    @CmdStep
    private int cmdStep = STEP1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_gauge);
        setToolbarTitle("一键检测");
        mspProtocol = MSPProtocol.getInstance();
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
                setDuration(1500);
        rotate.setRepeatCount(ValueAnimator.INFINITE);
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

                //执行一键检测，首先硬度加到100
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("100")
                        + BleUtils.convertDecimalToBinary("100"));
                time = 0;
                sendTimer = new Timer();
                sendTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        time += 1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_gauge_val.setText(String.format(Locale.getDefault(), "%ds", time));
                            }
                        });
                        int curLP = mspProtocol.getlPresureCurVal();
                        int curRP = mspProtocol.getrPresureCurVal();
                        Log.i(TAG, "run: curlP=" + curLP + "curRP=" + curRP + ";cmdStep=" + cmdStep);
                        //硬度加到100则设为40
                        switch (cmdStep) {
                            case STEP1:
                                if (curRP >= 98) {
                                    BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("20")
                                            + BleUtils.convertDecimalToBinary("20"));
                                    cmdStep = STEP2;
                                }

                                break;
                            case STEP2:
                                if (curRP >= 20 & curRP <= 25) {
                                    BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("80")
                                            + BleUtils.convertDecimalToBinary("80"));
                                    cmdStep = STEP3;
                                }
                                break;
                            case STEP3:
                                if (curRP >= 80 & curRP <= 85) {
                                    BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("30")
                                            + BleUtils.convertDecimalToBinary("30"));
                                    cmdStep = STEP4;
                                }
                                break;
                            case STEP4:
                                if (curRP >= 30 & curRP <= 35) {
                                    BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(String.valueOf(gauge_res))
                                            + BleUtils.convertDecimalToBinary(String.valueOf(gauge_res)));
                                    cmdStep = STEP5;
                                }
                                break;
                            case STEP5://最后显示结果
                                if (curRP == gauge_res) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rotate.end();
                                            gaugeRes.startNumAnim(String.valueOf(gauge_res));
                                            pb_result.setProgress(gauge_res);
                                            cmdStep = STEP1;
                                        }
                                    });
                                }
                                break;
                        }
                    }
                }, 0, 1000);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setEditTextEnable(true);
                tvHint.setVisibility(View.GONE);

                btn_start_gauge.setAlpha(1.0f);
                btn_start_gauge.setEnabled(true);

                if (sendTimer != null) {
                    sendTimer.cancel();
                    sendTimer = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setEditTextEnable(true);
                tvHint.setVisibility(View.GONE);

                btn_start_gauge.setAlpha(1.0f);
                btn_start_gauge.setEnabled(true);

                if (sendTimer != null) {
                    sendTimer.cancel();
                    sendTimer = null;
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
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
        if (TextUtils.isEmpty(weightStr)) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sendTimer != null) {
            sendTimer.cancel();
            sendTimer = null;
        }
    }
}
