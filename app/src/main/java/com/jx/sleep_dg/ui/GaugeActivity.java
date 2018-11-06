package com.jx.sleep_dg.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.view.bar.AnimatedProgressBar;
import com.jx.sleep_dg.view.NumberRollingView;

import java.util.Locale;

public class GaugeActivity extends BaseActivity {

    private static final String TAG = "GaugeActivity";
    private static final int SEARCH_DURATION = 2000;
    private static final int SEARCH_REACPEAT_COUNT = 5;

    private Button btn_start_gauge;
    private TextView tv_gauge_val;
    private LinearLayout ll_res;
    private AnimatedProgressBar pb_weight, pb_height, pb_heartbeat, pb_breath, pb_bodyTem, pb_roomTem, pb_result;
    private TextView tv_weight, tv_height, tv_heartbeat, tv_breath, tv_bodyTem, tv_roomTem, tv__result;
    private NumberRollingView tv_weight_per, tv_height_per, tv_heartbeat_per, tv_breath_per, tv_bodyTem_per, tv_roomTem_per, tv__result_per;

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
        btn_start_gauge = findViewById(R.id.btn_start_gauge);
        tv_gauge_val = findViewById(R.id.tv_gauge_val);
        ll_res = findViewById(R.id.ll_res);
        pb_weight = findViewById(R.id.pb_weight);
        pb_height = findViewById(R.id.pb_height);
        pb_heartbeat = findViewById(R.id.pb_heartbeat);
        pb_breath = findViewById(R.id.pb_breath);
        pb_bodyTem = findViewById(R.id.pb_bodyTem);
        pb_roomTem = findViewById(R.id.pb_roomTem);
        pb_result = findViewById(R.id.pb_result);

        tv_weight = findViewById(R.id.tv_weight);
        tv_height = findViewById(R.id.tv_height);
        tv_heartbeat = findViewById(R.id.tv_heartbeat);
        tv_breath = findViewById(R.id.tv_breath);
        tv_bodyTem = findViewById(R.id.tv_bodyTem);
        tv_roomTem = findViewById(R.id.tv_roomTem);
        tv__result = findViewById(R.id.tv_result);

        tv_weight_per = findViewById(R.id.tv_weight_per);
        tv_height_per = findViewById(R.id.tv_height_per);
        tv_heartbeat_per = findViewById(R.id.tv_heartbeat_per);
        tv_breath_per = findViewById(R.id.tv_breath_per);
        tv_bodyTem_per = findViewById(R.id.tv_bodyTem_per);
        tv_roomTem_per = findViewById(R.id.tv_roomTem_per);

        btn_start_gauge.setOnClickListener(this);

        rotate = ObjectAnimator.ofFloat(iv_gauge, "rotation", 0, 359).
                setDuration(SEARCH_DURATION);
        rotate.setRepeatCount(SEARCH_REACPEAT_COUNT);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatMode(ObjectAnimator.RESTART);
        rotate.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                btn_start_gauge.setAlpha(0.5f);
                btn_start_gauge.setEnabled(false);
                new CountDownTimer(SEARCH_DURATION * SEARCH_REACPEAT_COUNT, 100) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int progress = (int) ((SEARCH_DURATION * SEARCH_REACPEAT_COUNT - millisUntilFinished) / 100);
                        tv_gauge_val.setText(String.format(Locale.getDefault(), "%d", progress));

                        if (progress == 5) {
                            tv_weight.setText("62kg");
                            pb_weight.setProgress(100);
                            tv_weight_per.startPercentAnim("100%");
                            tv_height.setText("172cm");
                            pb_height.setProgress(100);
                            tv_height_per.startPercentAnim("100%");
                            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("40")
                                    + BleUtils.convertDecimalToBinary("40"));

                        } else if(progress == 30){

                            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("20")
                                    + BleUtils.convertDecimalToBinary("20"));

                        } else if (progress == 50) {
                            tv_heartbeat.setText("68次/分钟");
                            tv_heartbeat_per.startPercentAnim("100%");
                            pb_heartbeat.setProgress(100);
                            tv_breath.setText("72次/分钟");
                            tv_breath_per.startPercentAnim("100%");
                            pb_breath.setProgress(100);

                            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("100")
                                    + BleUtils.convertDecimalToBinary("100"));

                        } else if (progress == 70) {
                            tv_bodyTem.setText("36°");
                            tv_bodyTem_per.startPercentAnim("100%");
                            pb_bodyTem.setProgress(100);
                            tv_roomTem.setText("28°");
                            tv_roomTem_per.startPercentAnim("100%");
                            pb_roomTem.setProgress(100);

                            BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary("65")
                                    + BleUtils.convertDecimalToBinary("65"));
                        }
                    }

                    @Override
                    public void onFinish() {
                        rotate.end();
                        tv__result.setText("您最适合的硬度:65档");
                        pb_result.setProgress(65);
                        cancel();
                    }
                }.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                btn_start_gauge.setAlpha(1.0f);
                btn_start_gauge.setEnabled(true);
                tv_gauge_val.setText(String.format(Locale.getDefault(), "%d", 100));
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                btn_start_gauge.setAlpha(1.0f);
                btn_start_gauge.setEnabled(true);
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
                rotate.start();
                break;
        }
    }
}
