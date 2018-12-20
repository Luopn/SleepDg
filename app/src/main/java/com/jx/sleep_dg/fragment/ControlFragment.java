package com.jx.sleep_dg.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.DeviceNetConfigAcyivity;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 控制
 * Created by Administrator on 2018/7/20.
 */

public class ControlFragment extends BaseMainFragment {

    private static final long VIBRATE_TIME = 20;

    private MSPProtocol mspProtocol;
    private Vibrator vibrator;

    private int mLHeadHigh, mLTailHigh, mLHardness, mRHardness;
    private TextView tvHeadHigh, tvTailHigh;//床头，床尾高度
    private TextView tvLHardness, tvRHardness;//左，右床位硬度

    private boolean isNeedRefreshHigh, isNeedRefreshHard;//是否更新床位升降数据，床位软硬的数据

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_control;
    }

    @Override
    public void onBindView(View view) {
        isNeedRefreshHigh = isNeedRefreshHard = true;

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        tvHeadHigh = view.findViewById(R.id.tv_head_high);
        tvTailHigh = view.findViewById(R.id.tv_tail_high);
        tvLHardness = view.findViewById(R.id.tv_l_hardness);
        tvRHardness = view.findViewById(R.id.tv_r_hardness);

        view.findViewById(R.id.ll_hardness).setOnClickListener(this);
        view.findViewById(R.id.ll_lift).setOnClickListener(this);
        view.findViewById(R.id.ll_temp).setOnClickListener(this);
        view.findViewById(R.id.tv_right).setOnClickListener(this);

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
        vibrator = (Vibrator) _mActivity.getSystemService(Context.VIBRATOR_SERVICE);
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
            mLTailHigh = mspProtocol.getHigh2();
            if (isNeedRefreshHigh) {
                tvHeadHigh.setText(String.format(getResources().getString(R.string.head_value),
                        (int)Math.ceil(mLHeadHigh / 17.0f * 45.0f)));
                tvTailHigh.setText(String.format(getResources().getString(R.string.feet_value),
                        (int)Math.ceil(mLTailHigh / 17.0f * 45.0f)));
            }
            mLHardness = mspProtocol.getlPresureCurVal();
            mRHardness = mspProtocol.getrPresureCurVal();
            if (isNeedRefreshHard) {
                tvLHardness.setText(String.format(getResources().getString(R.string.left_value), mLHardness));
                tvRHardness.setText(String.format(getResources().getString(R.string.right_value), mRHardness));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        switch (v.getId()) {
            case R.id.tv_right:
                intent.setClass(_mActivity, DeviceNetConfigAcyivity.class);
                startActivity(intent);
                break;
            case R.id.ll_lift:
                //床位升降
                intent.setClass(_mActivity, DeviceLiftFragment.class);
                startActivity(intent);
                break;
            case R.id.ll_hardness:
                //床位硬度
                intent.setClass(_mActivity, DeviseHardnessFragment.class);
                startActivity(intent);
                break;
            case R.id.ll_temp:
                //床位温度
                intent.setClass(_mActivity, DeviceTempFragment.class);
                startActivity(intent);
                break;
            case R.id.iv_height_head_up://头部升
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                mLHeadHigh += 1;
                if (mLHeadHigh >= 17) mLHeadHigh = 17;
                tvHeadHigh.setText(String.format(getResources().getString(R.string.head_value), (int)Math.ceil(mLHeadHigh / 17.0f * 45.0f)));

                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + ""));
                break;
            case R.id.iv_height_head_down://头部降
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                mLHeadHigh -= 1;
                if (mLHeadHigh <= 0) mLHeadHigh = 0;
                tvHeadHigh.setText(String.format(getResources().getString(R.string.head_value), (int)Math.ceil(mLHeadHigh / 17.0f * 45.0f)));
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + ""));
                break;
            case R.id.iv_foot_height_up://脚部升
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                mLTailHigh += 1;
                if (mLTailHigh >= 17) mLTailHigh = 17;
                tvTailHigh.setText(String.format(getResources().getString(R.string.feet_value), (int)Math.ceil(mLTailHigh / 17.0f * 45.0f)));

                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + ""));
                break;
            case R.id.iv_foot_height_down://脚部降
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHigh = false;//停止数据更新，防止设置的值被刷新

                mLTailHigh -= 1;
                if (mLTailHigh <= 0) mLTailHigh = 0;
                tvTailHigh.setText(String.format(getResources().getString(R.string.feet_value), (int)Math.ceil(mLTailHigh / 17.0f * 45.0f)));
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + "")
                        + BleUtils.convertDecimalToBinary(mLHeadHigh + "")
                        + BleUtils.convertDecimalToBinary(mLTailHigh + ""));
                break;
            case R.id.iv_hard_l_add://左边硬
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mLHardness += 5;
                if (mLHardness >= 100) mLHardness = 100;
                tvLHardness.setText(String.format(getResources().getString(R.string.left_value), mLHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_l_reduce://左边软
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mLHardness -= 5;
                if (mLHardness <= 0) mLHardness = 0;
                tvLHardness.setText(String.format(getResources().getString(R.string.left_value), mLHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_r_add://右边硬
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mRHardness += 5;
                if (mRHardness >= 100) mRHardness = 100;
                tvRHardness.setText(String.format(getResources().getString(R.string.right_value), mRHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
            case R.id.iv_hard_r_reduce://右边软
                vibrator.vibrate(VIBRATE_TIME);
                isNeedRefreshHard = false;//停止数据更新，防止设置的值被刷新

                mRHardness -= 5;
                if (mRHardness <= 0) mRHardness = 0;
                tvRHardness.setText(String.format(getResources().getString(R.string.right_value), mRHardness));

                BleComUtils.sendChongqi(BleUtils.convertDecimalToBinary(mLHardness + "")
                        + BleUtils.convertDecimalToBinary(mRHardness + ""));
                break;
        }
    }
}
