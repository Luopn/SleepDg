package com.jx.sleep_dg.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.DeviceNetConfigAcyivity;
import com.jx.sleep_dg.ui.SearchActivity;
import com.jx.sleep_dg.ui.StatisticsActivity;
import com.jx.sleep_dg.ui.UserInfoActivity;
import com.jx.sleep_dg.utils.CommonUtil;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.view.ColorArcProgressBar;
import com.jx.sleep_dg.view.EcgView;
import com.jx.sleep_dg.view.HuxiEcgView;
import com.jx.sleep_dg.view.NumberRollingView;

import java.util.Locale;

/**
 * 数据
 * Created by Administrator on 2018/7/20.
 */

public class DataFragment extends BaseMainFragment {

    private static final int SEARCH_DURATION = 1000;
    private static final int SEARCH_REACPEAT_COUNT = 5;

    private ImageView ivRight, ivUserImage;
    private TextView tvMore;
    private NumberRollingView tvSleepScore;
    private TextView tvXinlvRight, tvXinlvLeft, tvHuxiLeft, tvHuxiRight;

    private ColorArcProgressBar ivSleepProgress;

    private EcgView lecgXinlv;
    private HuxiEcgView recgXinlv;

    private MSPProtocol mspProtocol;
    private ObjectAnimator rotate;

    private boolean isrXinlv;
    private boolean islXinlv;

    public static DataFragment newInstance() {
        Bundle args = new Bundle();
        DataFragment fragment = new DataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_data;
    }

    @Override
    public void onBindView(View view) {

        ivRight = view.findViewById(R.id.iv_ble);
        ivRight.setOnClickListener(this);
        tvMore = view.findViewById(R.id.tv_more);
        tvMore.setOnClickListener(this);
        ivUserImage = view.findViewById(R.id.iv_user_image);
        ivUserImage.setOnClickListener(this);
        if (_mActivity.getApplication().getApplicationInfo().packageName.equals(Constance.QM)) {
            ivUserImage.setVisibility(View.INVISIBLE);
            ivRight.setVisibility(View.INVISIBLE);
        }else{
            tvMore.setVisibility(View.GONE);
        }

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
        CommonUtil.drawableTint(_mActivity, tvTitleDeepSleep, ContextCompat.getColor(_mActivity, R.color.mediumblue));
        CommonUtil.drawableTint(_mActivity, tvTitleShallowSleep, ContextCompat.getColor(_mActivity, R.color.textAccentColor));
        CommonUtil.drawableTint(_mActivity, tvTitleClearSleep, ContextCompat.getColor(_mActivity, R.color.default_blue_light));
        CommonUtil.drawableTint(_mActivity, tvTitleTimeSleep, ContextCompat.getColor(_mActivity, R.color.textTitleColor));

        //dummyProgress();
        ivSleepProgress.setCurrentValues(90);
        tvSleepScore.startNumAnim(90 + "");

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
            case R.id.iv_ble: {
                Intent intent = new Intent();
                intent.setClass(_mActivity, SearchActivity.class);
                _mActivity.startActivity(intent);
            }
            break;
            case R.id.tv_more: {
                //PopupMenu menu = new PopupMenu(_mActivity, ivMore);
                //menu.getMenuInflater().inflate(R.menu.menu_more, menu.getMenu());
                //menu.show();
                //menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                //    @Override
                //    public boolean onMenuItemClick(MenuItem item) {
                //        Intent intent = new Intent();
                //        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //        switch (item.getItemId()) {
                //            case R.id.action_sleep_statistic:
                //                intent.setClass(_mActivity, StatisticsActivity.class);
                //                _mActivity.startActivity(intent);
                //                break;
                //            case R.id.action_config_net:
                //                intent.setClass(_mActivity, DeviceNetConfigAcyivity.class);
                //                _mActivity.startActivity(intent);
                //                break;
                //        }
                //        return true;
                //    }
                //});
                Intent intent = new Intent(_mActivity, StatisticsActivity.class);
                _mActivity.startActivity(intent);
            }
            break;
            case R.id.iv_user_image:
                startActivity(new Intent(_mActivity, UserInfoActivity.class));
                //MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
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
                tvSleepScore.startNumAnim(90 + "");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                ivSleepProgress.setRotation(0);
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
        if (rotate != null)
            rotate.cancel();
    }
}
