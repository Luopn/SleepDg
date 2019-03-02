package com.jx.sleep_dg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.AssociatedActivity;
import com.jx.sleep_dg.ui.DeviceDetailActivity;
import com.jx.sleep_dg.ui.GaugeActivity;
import com.jx.sleep_dg.ui.InflationActivity;
import com.jx.sleep_dg.ui.MainActivity;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.LanguageUtil;
import com.jx.sleep_dg.utils.PreferenceUtils;

import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 设置
 * Created by Administrator on 2018/7/20.
 */

public class SettingFragment extends BaseMainFragment {

    private boolean isInitialDatas;
    private MSPProtocol mspProtocol;

    private Switch swYunfu, swErTong, swSiRen, swZhiHan;
    private RadioButton rbZhSimple, rbZhTradion, rbEn;
    private ScrollView scrollView;
    private TextView tvChongqi;

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
        isInitialDatas = false;
        mspProtocol = MSPProtocol.getInstance();

        view.findViewById(R.id.ll_detail).setOnClickListener(this);
        view.findViewById(R.id.ll_guanzhu).setOnClickListener(this);
        view.findViewById(R.id.tv_gauge).setOnClickListener(this);
        view.findViewById(R.id.ll_auto_chongqi).setOnClickListener(this);

        tvChongqi = view.findViewById(R.id.tv_auto_chongqi);
        swYunfu = view.findViewById(R.id.sw_yunfu);
        swYunfu.setOnClickListener(this);
        swErTong = view.findViewById(R.id.sw_ertong);
        swErTong.setOnClickListener(this);
        swSiRen = view.findViewById(R.id.sw_siren);
        swSiRen.setOnClickListener(this);
        swZhiHan = view.findViewById(R.id.sw_zhihan);
        swZhiHan.setOnClickListener(this);

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
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);

        if (tvChongqi != null) {
            tvChongqi.setText(String.format("%s %s:%s",
                    _mActivity.getResources().getString(R.string.auto_inflation),
                    String.format(Locale.getDefault(), "%02d", mspProtocol.getTire_hour() & 0xff),
                    String.format(Locale.getDefault(), "%02d", mspProtocol.getTire_minute() & 0xff))
            );
        }

        if (mspProtocol != null && !isInitialDatas) {
            if (swZhiHan == null || swSiRen == null || swErTong == null || swYunfu == null) {
                return;
            }
            swZhiHan.setChecked(false);
            swSiRen.setChecked(false);
            swErTong.setChecked(false);
            swYunfu.setChecked(false);
            switch (mspProtocol.getMode() & 0xff) {
                case 1:
                    swZhiHan.setChecked(true);
                    break;
                case 2:
                    swSiRen.setChecked(true);
                    break;
                case 3:
                    swErTong.setChecked(true);
                    break;
                case 4:
                    swYunfu.setChecked(true);
                    break;
            }
            isInitialDatas = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int hour = PreferenceUtils.getInt(Constance.KEY_INFLATION_HOUR, 0);
        int minute = PreferenceUtils.getInt(Constance.KEY_INFLATION_MINUTE, 0);
        tvChongqi.setText(String.format("%s %s:%s",
                _mActivity.getResources().getString(R.string.auto_inflation),
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute))
        );
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
            case R.id.ll_auto_chongqi:
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
            case R.id.sw_zhihan:
                isInitialDatas = false;
                if (swZhiHan.isChecked()) {
                    BleComUtils.sendMoShi("01");
                } else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_ertong:
                isInitialDatas = false;
                if (swErTong.isChecked()) {
                    BleComUtils.sendMoShi("02");
                } else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_siren:
                isInitialDatas = false;
                if (swSiRen.isChecked()) {
                    BleComUtils.sendMoShi("03");
                } else {
                    BleComUtils.sendMoShi("00");
                }
                break;
            case R.id.sw_yunfu:
                isInitialDatas = false;
                if (swYunfu.isChecked()) {
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

