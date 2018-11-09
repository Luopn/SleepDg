package com.jx.sleep_dg.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.MainActivity;
import com.jx.sleep_dg.ui.SearchActivity;
import com.jx.sleep_dg.utils.CommonUtil;
import com.jx.sleep_dg.view.EcgView;
import com.jx.sleep_dg.view.HuxiEcgView;
import com.jx.sleep_dg.view.NumberRollingView;

import java.util.Locale;

/**
 * 数据
 * Created by Administrator on 2018/7/20.
 */

public class DataFragment extends BaseFragment {

    private static final int SEARCH_DURATION = 1000;
    private static final int SEARCH_REACPEAT_COUNT = 5;

    private NumberRollingView tvSleepScore;
    private TextView tvXinlvRight, tvXinlvLeft, tvHuxiLeft, tvHuxiRight;

    private ImageView ivUserImage, ivSleepProgress;

    private EcgView lecgXinlv;
    private HuxiEcgView recgXinlv;

    private MSPProtocol mspProtocol;
    private ObjectAnimator rotate;

    private boolean isrXinlv;
    private boolean islXinlv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_data;
    }

    @Override
    public void onBindView(View view) {

        view.findViewById(R.id.iv_right).setOnClickListener(this);
        ivUserImage = view.findViewById(R.id.iv_user_image);
        ivUserImage.setOnClickListener(this);
        ivSleepProgress = view.findViewById(R.id.iv_circle_progress);
        tvSleepScore = view.findViewById(R.id.tv_sleep_score);

        //心电数据
        tvXinlvRight = view.findViewById(R.id.tv_xinlv_right);
        tvXinlvLeft = view.findViewById(R.id.tv_xinlv_left);
        tvHuxiLeft = view.findViewById(R.id.tv_huxi_left);
        tvHuxiRight = view.findViewById(R.id.tv_huxi_right);

        lecgXinlv = view.findViewById(R.id.l_ecg_xinlv);
        recgXinlv = view.findViewById(R.id.r_ecg_xinlv);

        TextView tvTitleDeepSleep = view.findViewById(R.id.tv_title_deep_sleep);
        TextView tvTitleShallowSleep = view.findViewById(R.id.tv_title_shallow_sleep);
        TextView tvTitleClearSleep = view.findViewById(R.id.tv_title_clear_sleep);
        TextView tvTitleTimeSleep = view.findViewById(R.id.tv_title_time_sleep);
        //染色,兼容Android23以下版本
        CommonUtil.drawableTint(getActivity(), tvTitleDeepSleep, ContextCompat.getColor(getActivity(), R.color.mediumblue));
        CommonUtil.drawableTint(getActivity(), tvTitleShallowSleep, ContextCompat.getColor(getActivity(), R.color.textAccentColor));
        CommonUtil.drawableTint(getActivity(), tvTitleClearSleep, ContextCompat.getColor(getActivity(), R.color.default_blue_light));
        CommonUtil.drawableTint(getActivity(), tvTitleTimeSleep, ContextCompat.getColor(getActivity(), R.color.textTitleColor));

        dummyProgress();

        mspProtocol = MSPProtocol.getInstance();
        bindViewData();
    }

    //绑定数据更新
    private void bindViewData() {
        if (mspProtocol != null) {
            int leftBreathFreq = mspProtocol.getlBreathFreq() & 0xff;
            int rightBreathFreq = mspProtocol.getrBreathFreq() & 0xff;
            int leftHeartBeat = mspProtocol.getlHeartBeat() & 0xff;
            int rightHeartBeat = mspProtocol.getrHeartBeat() & 0xff;

            tvHuxiLeft.setText(String.format(Locale.getDefault(), getResources().getString(R.string.breath_rate_value), leftBreathFreq));
            tvHuxiRight.setText(String.format(Locale.getDefault(), getResources().getString(R.string.breath_rate_value), rightBreathFreq));
            tvXinlvLeft.setText(String.format(Locale.getDefault(), getResources().getString(R.string.heart_rate_value), leftHeartBeat));
            tvXinlvRight.setText(String.format(Locale.getDefault(), getResources().getString(R.string.heart_rate_value), rightHeartBeat));

            if (leftBreathFreq > 0) {
                if (!islXinlv) {
                    lecgXinlv.startDraw();
                    islXinlv = true;
                }
            } else {
                lecgXinlv.stop();
                islXinlv = false;
            }

            if (rightHeartBeat > 0) {
                if (!isrXinlv) {
                    recgXinlv.startDraw();
                    isrXinlv = true;
                }
            } else {
                recgXinlv.stop();
                isrXinlv = false;
            }
        }
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }

    @Override
    protected void notifyDeviceDisconnected() {
        super.notifyDeviceDisconnected();
        lecgXinlv.stop();
        recgXinlv.stop();
        isrXinlv = false;
        islXinlv = false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_right:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.iv_user_image:
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    private void dummyProgress() {
        rotate = ObjectAnimator.ofFloat(ivSleepProgress, "rotation", 0, 359).
                setDuration(SEARCH_DURATION);
        rotate.setRepeatCount(SEARCH_REACPEAT_COUNT);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatMode(ObjectAnimator.RESTART);
        rotate.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                final int res = 90;
                tvSleepScore.startNumAnim(90 + "");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ivSleepProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                rotate.start();
            }
        }, 100);
    }

    @Override
    public void onStop() {
        super.onStop();
        rotate.cancel();
    }
}
