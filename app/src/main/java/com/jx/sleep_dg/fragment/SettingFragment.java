package com.jx.sleep_dg.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.ui.AssociatedActivity;
import com.jx.sleep_dg.ui.DeviceDetailActivity;

/**
 * 设置
 * Created by Administrator on 2018/7/20.
 */

public class SettingFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private Switch swYunfu;
    private Switch swErTong;
    private Switch swSiRen;
    private Switch swZhiHan;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onBindView(View view) {
        view.findViewById(R.id.ll_detail).setOnClickListener(this);
        view.findViewById(R.id.ll_guanzhu).setOnClickListener(this);
        swYunfu = view.findViewById(R.id.sw_yunfu);
        swYunfu.setOnCheckedChangeListener(this);
        swErTong = view.findViewById(R.id.sw_ertong);
        swErTong.setOnCheckedChangeListener(this);
        swSiRen = view.findViewById(R.id.sw_siren);
        swSiRen.setOnCheckedChangeListener(this);
        swZhiHan = view.findViewById(R.id.sw_zhihan);
        swZhiHan.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_detail:
                Intent intent = new Intent();
                intent.setClass(getActivity(), DeviceDetailActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.ll_guanzhu:
              startActivity(new Intent(getActivity(), AssociatedActivity.class));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_zhihan:
                if (isChecked){
                    BleComUtils.sendMoShi("01");
                }else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_ertong:
                if (isChecked){
                    BleComUtils.sendMoShi("02");
                }else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_siren:
                if (isChecked){
                    BleComUtils.sendMoShi("03");
                }else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_yunfu:
                if (isChecked){
                    BleComUtils.sendMoShi("04");
                }else {
                    BleComUtils.sendMoShi("00");
                }
                break;
        }
    }
}

