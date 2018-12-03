package com.jx.sleep_dg.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.BorderButton;

/**
 * 床位温度
 */

public class DeviceTempFragment extends BaseMainFragment implements View.OnClickListener {
    private ImageView ivChuang;
    private BorderButton mLevel1;
    private BorderButton mLevel2;
    private BorderButton mLevel3;
    private BorderButton mLevel4;
    private BorderButton mLevel5;

    private int index = 0;
    private View view;

    public static DeviceTempFragment newInstance() {
        Bundle args = new Bundle();
        DeviceTempFragment fragment = new DeviceTempFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_temp;
    }

    @Override
    public void onBindView(View view) {
        ivChuang = view.findViewById(R.id.iv_chuang);
        mLevel1 = (BorderButton) view.findViewById(R.id.level_1);
        mLevel2 = (BorderButton) view.findViewById(R.id.level_2);
        mLevel3 = (BorderButton) view.findViewById(R.id.level_3);
        mLevel4 = (BorderButton) view.findViewById(R.id.level_4);
        mLevel5 = (BorderButton) view.findViewById(R.id.level_5);

        mLevel1.setOnClickListener(this);
        mLevel2.setOnClickListener(this);
        mLevel3.setOnClickListener(this);
        mLevel4.setOnClickListener(this);
        mLevel5.setOnClickListener(this);

        view.findViewById(R.id.btn_jian).setOnClickListener(this);
        view.findViewById(R.id.btn_add).setOnClickListener(this);

        onIndexChange(index);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_jian:
                if (index > 0) {
                    index--;
                }
                break;
            case R.id.btn_add:
                if (index < 5) {
                    index++;
                }
                break;
            case R.id.level_1:
                index = 1;
                BleComUtils.sendJiare("1");
                break;
            case R.id.level_2:
                index = 2;
                BleComUtils.sendJiare("2");
                break;
            case R.id.level_3:
                index = 3;
                BleComUtils.sendJiare("3");
                break;
            case R.id.level_4:
                index = 4;
                BleComUtils.sendJiare("4");
                break;
            case R.id.level_5:
                index = 5;
                BleComUtils.sendJiare("5");
                break;
        }
        LogUtil.e("index:" + index);
        onIndexChange(index);
        BleComUtils.sendJiare(index + "");
    }

    //床的变化和按钮变化
    private void onIndexChange(int index) {
        mLevel1.setNormalColor(Color.TRANSPARENT);
        mLevel2.setNormalColor(Color.TRANSPARENT);
        mLevel3.setNormalColor(Color.TRANSPARENT);
        mLevel4.setNormalColor(Color.TRANSPARENT);
        mLevel5.setNormalColor(Color.TRANSPARENT);
        switch (index) {
            case 0:
                ivChuang.setImageResource(R.mipmap.warmbed_0);
                break;
            case 1:
                ivChuang.setImageResource(R.mipmap.warmbed_1);
                mLevel1.setNormalColor(Color.parseColor("#6e6fff"));
                break;
            case 2:
                ivChuang.setImageResource(R.mipmap.warmbed_2);
                mLevel1.setNormalColor(Color.parseColor("#6e6fff"));
                mLevel2.setNormalColor(Color.parseColor("#8d52be"));
                break;
            case 3:
                ivChuang.setImageResource(R.mipmap.warmbed_3);
                mLevel1.setNormalColor(Color.parseColor("#6e6fff"));
                mLevel2.setNormalColor(Color.parseColor("#8d52be"));
                mLevel3.setNormalColor(Color.parseColor("#b1347e"));
                break;
            case 4:
                ivChuang.setImageResource(R.mipmap.warmbed_4);
                mLevel1.setNormalColor(Color.parseColor("#6e6fff"));
                mLevel2.setNormalColor(Color.parseColor("#8d52be"));
                mLevel3.setNormalColor(Color.parseColor("#b1347e"));
                mLevel4.setNormalColor(Color.parseColor("#c31f52"));
                break;
            case 5:
                ivChuang.setImageResource(R.mipmap.warmbed_5);
                mLevel1.setNormalColor(Color.parseColor("#6e6fff"));
                mLevel2.setNormalColor(Color.parseColor("#8d52be"));
                mLevel3.setNormalColor(Color.parseColor("#b1347e"));
                mLevel4.setNormalColor(Color.parseColor("#c31f52"));
                mLevel5.setNormalColor(Color.parseColor("#d90d28"));
                break;
        }
    }
}
