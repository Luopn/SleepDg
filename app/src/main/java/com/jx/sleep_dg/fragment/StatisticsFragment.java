package com.jx.sleep_dg.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.MSPProtocol;

/**
 * 睡眠统计
 * Created by Administrator on 2018/7/20.
 */

public class StatisticsFragment extends BaseFragment {

    private TextView tvLeftHeartbeat, tvRightHeartbeat;//心率
    private TextView tvLeftBreath, tvRightBreath;//呼吸率
    private TextView tvLeftBodyMove, tvRightBodyMove;//翻身次数（分钟）

    private MSPProtocol mspProtocol;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep;
    }

    @Override
    public void onBindView(View view) {
        tvLeftHeartbeat = view.findViewById(R.id.tv_xinlv_left);
        tvRightHeartbeat = view.findViewById(R.id.tv_xinlv_right);
        tvLeftBreath = view.findViewById(R.id.tv_huxi_left);
        tvRightBreath = view.findViewById(R.id.tv_huxi_right);
        tvLeftBodyMove = view.findViewById(R.id.tv_fanshen_left);
        tvRightBodyMove = view.findViewById(R.id.tv_fanshen_right);

        mspProtocol = MSPProtocol.getInstance();
        bindViewData();
    }

    private void bindViewData() {
        if (mspProtocol == null) return;
        tvLeftHeartbeat.setText(String.format("左 %s", mspProtocol.getlHeartBeat()));
        tvRightHeartbeat.setText(String.format("右 %s", mspProtocol.getlHeartBeat()));
        tvLeftBreath.setText(String.format("左 %s", mspProtocol.getlBreathFreq()));
        tvRightBreath.setText(String.format("右 %s", mspProtocol.getrBreathFreq()));
        tvLeftBodyMove.setText(String.format("左 %s", mspProtocol.getlBodyMoveVal()));
        tvRightBodyMove.setText(String.format("右 %s", mspProtocol.getrBodyMoveVal()));
    }

    @Override
    protected void notifyUIDataSetChanged(Intent intent) {
        super.notifyUIDataSetChanged(intent);
        bindViewData();
    }
}
