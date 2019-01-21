package com.jx.sleep_dg.ui;

import android.os.Bundle;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.fragment.MainFragment;
import com.jx.sleep_dg.fragment.MainQmFragment;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.utils.Constance;

public class MainActivity extends BaseActivity {

    public static final String KEY_FRAGMENT = "key_fragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();

        BleComUtils.sendTime("F10100000001");
    }

    @Override
    public void bindView() {
        switch (getApplication().getPackageName()) {
            case Constance.QM:
                if (findFragment(MainFragment.class) == null) {
                    loadRootFragment(R.id.content, MainQmFragment.newInstance());
                }
                break;
            default:
                if (findFragment(MainFragment.class) == null) {
                    loadRootFragment(R.id.content, MainFragment.newInstance());
                }
                break;
        }

    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }
}
