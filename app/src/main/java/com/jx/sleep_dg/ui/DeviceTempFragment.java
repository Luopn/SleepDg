package com.jx.sleep_dg.ui;

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

        view.findViewById(R.id.btn_jian).setOnClickListener(this);
        view.findViewById(R.id.btn_add).setOnClickListener(this);
        switchByLevel(index+1);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_jian:
                //if (index == 0) {
                //    ToastUtil.showMessage("请先打开开关");
                //    return;
                //}
                if (index > 0) {
                    index--;
                    switch (index) {
                        case 0:
                            ivChuang.setImageResource(R.mipmap.warmbed_0);
                            switchByLevel(1);
                            break;
                        case 1:
                            ivChuang.setImageResource(R.mipmap.warmbed_1);
                            switchByLevel(2);
                            break;
                        case 2:
                            ivChuang.setImageResource(R.mipmap.warmbed_2);
                            switchByLevel(3);
                            break;
                        case 3:
                            ivChuang.setImageResource(R.mipmap.warmbed_3);
                            switchByLevel(4);
                            break;
                        case 4:
                            ivChuang.setImageResource(R.mipmap.warmbed_4);
                            switchByLevel(5);
                            break;
                    }
                    BleComUtils.sendJiare(index + "");
                }
                break;
            case R.id.btn_add:
//                if (index == 0) {
//                    ToastUtil.showMessage("请先打开开关");
//                    return;
//                }
                if (index < 4) {
                    index++;
                    switch (index) {
                        case 0:
                            ivChuang.setImageResource(R.mipmap.warmbed_1);
                            switchByLevel(1);
                        case 1:
                            ivChuang.setImageResource(R.mipmap.warmbed_2);
                            switchByLevel(2);
                            break;
                        case 2:
                            ivChuang.setImageResource(R.mipmap.warmbed_3);
                            switchByLevel(3);
                            break;
                        case 3:
                            ivChuang.setImageResource(R.mipmap.warmbed_4);
                            switchByLevel(4);
                            break;
                        case 4:
                            ivChuang.setImageResource(R.mipmap.warmbed_5);
                            switchByLevel(5);
                            break;
                    }
                    LogUtil.e("index:" + index);
                    BleComUtils.sendJiare(index + "");
                }
                break;
        }
    }

    private void switchByLevel(int level) {
        mLevel1.setVisibility(View.INVISIBLE);
        mLevel2.setVisibility(View.INVISIBLE);
        mLevel3.setVisibility(View.INVISIBLE);
        mLevel4.setVisibility(View.INVISIBLE);
        mLevel5.setVisibility(View.INVISIBLE);
        switch (level){
            case 1:
                mLevel1.setVisibility(View.VISIBLE);
                break;
            case 2:
                mLevel1.setVisibility(View.VISIBLE);
                mLevel2.setVisibility(View.VISIBLE);
                break;
            case 3:
                mLevel1.setVisibility(View.VISIBLE);
                mLevel2.setVisibility(View.VISIBLE);
                mLevel3.setVisibility(View.VISIBLE);
                break;
            case 4:
                mLevel1.setVisibility(View.VISIBLE);
                mLevel2.setVisibility(View.VISIBLE);
                mLevel3.setVisibility(View.VISIBLE);
                mLevel4.setVisibility(View.VISIBLE);
                break;
            case 5:
                mLevel1.setVisibility(View.VISIBLE);
                mLevel2.setVisibility(View.VISIBLE);
                mLevel3.setVisibility(View.VISIBLE);
                mLevel4.setVisibility(View.VISIBLE);
                mLevel5.setVisibility(View.VISIBLE);
                break;
        }

    }
}
