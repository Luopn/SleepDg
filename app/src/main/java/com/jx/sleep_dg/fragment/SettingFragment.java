package com.jx.sleep_dg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Switch;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.ui.AssociatedActivity;
import com.jx.sleep_dg.ui.DeviceDetailActivity;
import com.jx.sleep_dg.ui.GaugeActivity;
import com.jx.sleep_dg.ui.InflationActivity;
import com.jx.sleep_dg.ui.MainActivity;
import com.jx.sleep_dg.utils.LanguageUtil;
import com.jx.sleep_dg.utils.PreferenceUtils;

import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 设置
 * Created by Administrator on 2018/7/20.
 */

public class SettingFragment extends BaseMainFragment implements CompoundButton.OnCheckedChangeListener {

    private Switch swYunfu, swErTong, swSiRen, swZhiHan;
    private RadioButton rbZhSimple, rbZhTradion, rbEn;
    private ScrollView scrollView;

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onBindView(View view) {
        view.findViewById(R.id.ll_detail).setOnClickListener(this);
        view.findViewById(R.id.ll_guanzhu).setOnClickListener(this);
        view.findViewById(R.id.tv_gauge).setOnClickListener(this);
        view.findViewById(R.id.tv_auto_chongqi).setOnClickListener(this);
        swYunfu = view.findViewById(R.id.sw_yunfu);
        swYunfu.setOnCheckedChangeListener(this);
        swErTong = view.findViewById(R.id.sw_ertong);
        swErTong.setOnCheckedChangeListener(this);
        swSiRen = view.findViewById(R.id.sw_siren);
        swSiRen.setOnCheckedChangeListener(this);
        swZhiHan = view.findViewById(R.id.sw_zhihan);
        swZhiHan.setOnCheckedChangeListener(this);

        rbZhSimple = view.findViewById(R.id.rb_ch_simple);
        rbZhTradion = view.findViewById(R.id.rb_ch_tradition);
        rbEn = view.findViewById(R.id.rb_en);
        rbZhSimple.setOnClickListener(this);
        rbZhTradion.setOnClickListener(this);
        rbEn.setOnClickListener(this);
        //初始化语言选项
        Locale locale = (Locale) PreferenceUtils.deSerialization(PreferenceUtils.getString(LanguageUtil.LANGUAGE));
        if (locale != null) {
            if (locale.getCountry().equals(Locale.SIMPLIFIED_CHINESE.getCountry())) {
                rbZhSimple.setChecked(true);
            }
            if (locale.getCountry().equals(Locale.TRADITIONAL_CHINESE.getCountry())) {
                rbZhTradion.setChecked(true);
            }
            if (locale.getCountry().equals(Locale.US.getCountry())) {
                rbEn.setChecked(true);
            }
        }

        scrollView = view.findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_detail:
                Intent intent = new Intent();
                intent.setClass(_mActivity, DeviceDetailActivity.class);
                _mActivity.startActivity(intent);
                break;
            case R.id.ll_guanzhu:
                startActivity(new Intent(_mActivity, AssociatedActivity.class));
                break;
            case R.id.tv_gauge:
                startActivity(new Intent(_mActivity, GaugeActivity.class));
                break;
            case R.id.tv_auto_chongqi:
                startActivity(new Intent(_mActivity, InflationActivity.class));
                break;
            case R.id.rb_ch_simple:
                LanguageUtil.changeAppLanguage(_mActivity, Locale.SIMPLIFIED_CHINESE, true);
                restartApplication();
                break;
            case R.id.rb_ch_tradition:
                LanguageUtil.changeAppLanguage(_mActivity, Locale.TRADITIONAL_CHINESE, true);
                restartApplication();
                break;
            case R.id.rb_en:
                LanguageUtil.changeAppLanguage(_mActivity, Locale.US, true);
                restartApplication();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_zhihan:
                if (isChecked) {
                    BleComUtils.sendMoShi("01");
                } else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_ertong:
                if (isChecked) {
                    BleComUtils.sendMoShi("02");
                } else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_siren:
                if (isChecked) {
                    BleComUtils.sendMoShi("03");
                } else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_yunfu:
                if (isChecked) {
                    BleComUtils.sendMoShi("04");
                } else {
                    BleComUtils.sendMoShi("00");
                }
                break;
        }
    }

    private void restartApplication() {
        //切换语言信息，需要重启 Activity 才能实现
        Intent intent = new Intent(_mActivity, MainActivity.class);
        intent.putExtra(MainActivity.KEY_FRAGMENT, 4);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

