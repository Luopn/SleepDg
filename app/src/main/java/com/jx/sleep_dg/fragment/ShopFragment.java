package com.jx.sleep_dg.fragment;

import android.os.Bundle;
import android.view.View;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;

/**
 * 商城
 * Created by Administrator on 2018/7/20.
 */

public class ShopFragment extends BaseMainFragment {

    public static ShopFragment newInstance() {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shop;
    }

    @Override
    public void onBindView(View view) {

    }
}
