package com.jx.sleep_dg.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.DeviceLiftAcyivity;
import com.jx.sleep_dg.ui.DeviceNetConfigAcyivity;
import com.jx.sleep_dg.ui.DeviceTempActivity;
import com.jx.sleep_dg.ui.DeviseHardnessActivity;
import com.jx.sleep_dg.view.SegmentControl;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 控制
 * Created by Administrator on 2018/7/20.
 */

public class ControlFragment extends BaseFragment {

    private static final long VIBRATE_TIME = 20;

    private MSPProtocol mspProtocol;
    private Vibrator vibrator;

    private int mLHeadHigh, mLTailHigh, mRHeadHigh, mRTailHigh, mLHardness, mRHardness;
    private boolean isHighR;

    private ScrollView scrollView;
    private SegmentControl scHigh, scHard;
    private TextView tvHeadHigh, tvTailHigh;//床头，床尾高度
    private TextView tvLHardness, tvRHardness;//左，右床位硬度

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_control;
    }

    @Override
    public void onBindView(View view) {
        scrollView = view.findViewById(R.id.scrollView);

        scHigh = view.findViewById(R.id.sc_high);
        scHard = view.findViewById(R.id.sc_hard);

        tvHeadHigh = view.findViewById(R.id.tv_head_high);
        tvTailHigh = view.findViewById(R.id.tv_tail_high);
        tvLHardness = view.findViewById(R.id.tv_l_hardness);
        tvRHardness = view.findViewById(R.id.tv_r_hardness);

        view.findViewById(R.id.ll_hardness).setOnClickListener(this);
        view.findViewById(R.id.ll_lift).setOnClickListener(this);
        view.findViewById(R.id.ll_temp).setOnClickListener(this);
        view.findViewById(R.id.tv_right).setOnClickListener(this);

        scHigh.setOnSegmentChangedListener(new SegmentControl.OnSegmentChangedListener() {
            @Override
            public void onSegmentChanged(int newSelectedIndex) {
                isHighR = newSelectedIndex == 1;
            }
        });
        scHard.setOnSegmentChangedListener(new SegmentControl.OnSegmentChangedListener() {
            @Override
            public void onSegmentChanged(int newSelectedIndex) {
            }
        });
        //控制
        view.findViewById(R.id.iv_height_head_up).setOnClickListener(this);//头部升
        view.findViewById(R.id.iv_height_head_down).setOnClickListener(this);//头部降
        view.findViewById(R.id.iv_foot_height_up).setOnClickListener(this);//脚部升
        view.findViewById(R.id.iv_foot_height_down).setOnClickListener(this);//脚部降
        view.findViewById(R.id.iv_hard_l_add).setOnClickListener(this);//左边加硬
        view.findViewById(R.id.iv_hard_l_reduce).setOnClickListener(this);//左边变软
        view.findViewById(R.id.iv_hard_r_add).setOnClickListener(this);//右边变硬
        view.findViewById(R.id.iv_hard_r_reduce).setOnClickListener(this);//右边变软
        //协议
        mspProtocol = MSPProtocol.getInstance();

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        bindViewData();
    }

    @Override
    protected void notifyUIDataSetChanged(Intent intent) {
        super.notifyUIDataSetChanged(intent);
        bindViewData();
    }

    //绑定UI数据
    private void bindViewData() {
        if (mspProtocol != null) {
            mLHeadHigh = mspProtocol.getHigh1();
            mRHeadHigh = mspProtocol.getHigh3();
            mLTailHigh = mspProtocol.getHigh2();
            mRTailHigh = mspProtocol.getHigh4();
            if (isHighR) {
                tvHeadHigh.setText(String.format("%s", mRHeadHigh = mspProtocol.getHigh3()));
                tvTailHigh.setText(String.format("%s", mRTailHigh = mspProtocol.getHigh4()));
            } else {
                tvHeadHigh.setText(String.format("%s", mLHeadHigh = mspProtocol.getHigh1()));
                tvTailHigh.setText(String.format("%s", mLTailHigh = mspProtocol.getHigh2()));
            }

            tvLHardness.setText(String.format("%s", mLHardness = mspProtocol.getlPresureCurVal()));
            tvRHardness.setText(String.format("%s", mRHardness = mspProtocol.getrPresureCurVal()));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_right:
                startActivity(new Intent(getActivity(), DeviceNetConfigAcyivity.class));
                break;
            case R.id.ll_lift:
                //床位升降
                startActivity(new Intent(getActivity(), DeviceLiftAcyivity.class));
                break;
            case R.id.ll_hardness:
                //床位硬度
                startActivity(new Intent(getActivity(), DeviseHardnessActivity.class));
                break;
            case R.id.ll_temp:
                //床位温度
                startActivity(new Intent(getActivity(), DeviceTempActivity.class));
                break;
            case R.id.iv_height_head_up://头部升
                vibrator.vibrate(VIBRATE_TIME);

                if (isHighR) {
                    mRHeadHigh += 1;
                    if (mRHeadHigh >= 17) mRHeadHigh = 17;
                } else {
                    mLHeadHigh += 1;
                    if (mLHeadHigh >= 17) mLHeadHigh = 17;
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_height_head_down://头部降
                vibrator.vibrate(VIBRATE_TIME);

                if (isHighR) {
                    mRHeadHigh -= 1;
                    if (mRHeadHigh <= 0) mRHeadHigh = 0;
                } else {
                    mLHeadHigh -= 1;
                    if (mLHeadHigh <= 0) mLHeadHigh = 0;
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_foot_height_up://脚部升
                vibrator.vibrate(VIBRATE_TIME);

                if (isHighR) {
                    mRTailHigh += 1;
                    if (mRTailHigh >= 17) mRTailHigh = 17;
                } else {
                    mLTailHigh += 1;
                    if (mLTailHigh >= 17) mLTailHigh = 17;
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_foot_height_down://脚部降
                vibrator.vibrate(VIBRATE_TIME);

                if (isHighR) {
                    mRTailHigh -= 1;
                    if (mRTailHigh <= 0) mRTailHigh = 0;
                } else {
                    mLTailHigh -= 1;
                    if (mLTailHigh <= 0) mLTailHigh = 0;
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_hard_l_add://左边硬
                vibrator.vibrate(VIBRATE_TIME);

                mLHardness += 5;
                if (mLHardness >= 100) mLHardness = 100;
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_l_reduce://左边软
                vibrator.vibrate(VIBRATE_TIME);

                mLHardness -= 5;
                if (mLHardness <= 0) mLHardness = 0;
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_r_add://右边硬
                vibrator.vibrate(VIBRATE_TIME);

                mRHardness += 5;
                if (mRHardness >= 100) mRHardness = 100;
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_r_reduce://右边软
                vibrator.vibrate(VIBRATE_TIME);

                mRHardness -= 5;
                if (mLHardness <= 0) mRHardness = 0;
                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
        }
    }

}
