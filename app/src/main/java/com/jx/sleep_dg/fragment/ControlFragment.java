package com.jx.sleep_dg.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
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
    private TextView tvHeadHigh, tvTailHigh;//床头，床尾高度
    private TextView tvLHardness, tvRHardness;//左，右床位硬度

    private boolean isHighR;//切换显示床位高度的左右
    private boolean isNeedRefreshHigh, isNeedRefreshHard;//是否更新床位升降数据，床位软硬的数据

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_control;
    }

    @Override
    public void onBindView(View view) {
        isNeedRefreshHigh = isNeedRefreshHard = true;

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        SegmentControl scHigh = view.findViewById(R.id.sc_high);
        SegmentControl scHard = view.findViewById(R.id.sc_hard);
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
                if (isHighR) {
                    tvHeadHigh.setText(String.format("%s", mRHeadHigh));
                    tvTailHigh.setText(String.format("%s", mRTailHigh));
                } else {
                    tvHeadHigh.setText(String.format("%s", mLHeadHigh));
                    tvTailHigh.setText(String.format("%s", mLTailHigh));
                }
            }
        });
        //控制
        ImageView iv_height_head_up = view.findViewById(R.id.iv_height_head_up);
        ImageView iv_height_head_down = view.findViewById(R.id.iv_height_head_down);
        ImageView iv_foot_height_up = view.findViewById(R.id.iv_foot_height_up);
        ImageView iv_foot_height_down = view.findViewById(R.id.iv_foot_height_down);
        ImageView iv_hard_l_reduce = view.findViewById(R.id.iv_hard_l_reduce);
        ImageView iv_hard_l_add = view.findViewById(R.id.iv_hard_l_add);
        ImageView iv_hard_r_add = view.findViewById(R.id.iv_hard_r_add);
        ImageView iv_hard_r_reduce = view.findViewById(R.id.iv_hard_r_reduce);
        iv_height_head_up.setOnClickListener(this);//头部升
        iv_height_head_down.setOnClickListener(this);//头部降
        iv_foot_height_up.setOnClickListener(this);//脚部升
        iv_foot_height_down.setOnClickListener(this);//脚部降
        iv_hard_l_add.setOnClickListener(this);//左边加硬
        iv_hard_l_reduce.setOnClickListener(this);//左边变软
        iv_hard_r_add.setOnClickListener(this);//右边变硬
        iv_hard_r_reduce.setOnClickListener(this);//右边变软
        //协议
        mspProtocol = MSPProtocol.getInstance();

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        bindViewData();
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }

    //绑定UI数据
    private void bindViewData() {
        if (mspProtocol != null) {
            mLHeadHigh = mspProtocol.getHigh1();
            mRHeadHigh = mspProtocol.getHigh3();
            mLTailHigh = mspProtocol.getHigh2();
            mRTailHigh = mspProtocol.getHigh4();
            if (isNeedRefreshHigh) {
                if (isHighR) {
                    tvHeadHigh.setText(String.format("%s", mRHeadHigh));
                    tvTailHigh.setText(String.format("%s", mRTailHigh));
                } else {
                    tvHeadHigh.setText(String.format("%s", mLHeadHigh));
                    tvTailHigh.setText(String.format("%s", mLTailHigh));
                }
            }
            mLHardness = mspProtocol.getlPresureCurVal();
            mRHardness = mspProtocol.getrPresureCurVal();
            if (isNeedRefreshHard) {
                tvLHardness.setText(String.format("%s", mLHardness));
                tvRHardness.setText(String.format("%s", mRHardness));
            }
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
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                if (isHighR) {
                    mRHeadHigh += 1;
                    if (mRHeadHigh >= 17) mRHeadHigh = 17;
                    tvHeadHigh.setText(String.format("%s", mRHeadHigh));
                } else {
                    mLHeadHigh += 1;
                    if (mLHeadHigh >= 17) mLHeadHigh = 17;
                    tvHeadHigh.setText(String.format("%s", mLHeadHigh));
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_height_head_down://头部降
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                if (isHighR) {
                    mRHeadHigh -= 1;
                    if (mRHeadHigh <= 0) mRHeadHigh = 0;
                    tvHeadHigh.setText(String.format("%s", mRHeadHigh));
                } else {
                    mLHeadHigh -= 1;
                    if (mLHeadHigh <= 0) mLHeadHigh = 0;
                    tvHeadHigh.setText(String.format("%s", mLHeadHigh));
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_foot_height_up://脚部升
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                if (isHighR) {
                    mRTailHigh += 1;
                    if (mRTailHigh >= 17) mRTailHigh = 17;
                    tvTailHigh.setText(String.format("%s", mRTailHigh));
                } else {
                    mLTailHigh += 1;
                    if (mLTailHigh >= 17) mLTailHigh = 17;
                    tvTailHigh.setText(String.format("%s", mLTailHigh));
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_foot_height_down://脚部降
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                if (isHighR) {
                    mRTailHigh -= 1;
                    if (mRTailHigh <= 0) mRTailHigh = 0;
                    tvTailHigh.setText(String.format("%s", mRTailHigh));
                } else {
                    mLTailHigh -= 1;
                    if (mLTailHigh <= 0) mLTailHigh = 0;
                    tvTailHigh.setText(String.format("%s", mLTailHigh));
                }
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mRHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mRTailHigh + ""));
                break;
            case R.id.iv_hard_l_add://左边硬
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mLHardness += 5;
                if (mLHardness >= 100) mLHardness = 100;
                tvLHardness.setText(String.format("%s", mLHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_l_reduce://左边软
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mLHardness -= 5;
                if (mLHardness <= 0) mLHardness = 0;
                tvLHardness.setText(String.format("%s", mLHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_r_add://右边硬
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mRHardness += 5;
                if (mRHardness >= 100) mRHardness = 100;
                tvRHardness.setText(String.format("%s", mRHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_r_reduce://右边软
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mRHardness -= 5;
                if (mRHardness <= 0) mRHardness = 0;
                tvRHardness.setText(String.format("%s", mRHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
        }
    }
}
