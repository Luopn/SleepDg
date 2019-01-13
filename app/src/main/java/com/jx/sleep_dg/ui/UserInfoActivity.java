package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.QMUIStatusBarHelper;
import com.jx.sleep_dg.utils.StatusBarUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 个人信息
 */

public class UserInfoActivity extends BaseActivity {

    private TextView tvUserName, tvUserCoins, tvCoinsTrans, tvTitleCoinsMgr;

    private final static String SEL_NONE = "sel_nome", SEL_QM = "sel_qm", SEL_SF = "sel_sf";

    @StringDef({SEL_NONE, SEL_QM, SEL_SF})
    @Retention(RetentionPolicy.SOURCE)
    @interface COMPANY {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_user_info);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.user_info);
        setTitleCol(Color.WHITE);
        setToolbarBackColor(ContextCompat.getColor(this, R.color.textTitleColor));
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.textTitleColor), 255);

        Button exitBtn = findViewById(R.id.btn_exit);
        exitBtn.setOnClickListener(this);
        tvUserName = findViewById(R.id.tv_username);
        tvUserCoins = findViewById(R.id.tv_user_coins_count);
        tvCoinsTrans = findViewById(R.id.tv_coins_trans);
        tvTitleCoinsMgr = findViewById(R.id.tv_coin_mgr);

        switch (getApplication().getPackageName()) {
            case Constance.QM:
                selCompany(SEL_QM);
                break;
            case Constance.SF:
                selCompany(SEL_SF);
                break;
            default:
                selCompany(SEL_NONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_exit:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void selCompany(@COMPANY String company) {
        switch (company) {
            case SEL_NONE:
                break;
            case SEL_QM:
                tvUserName.setText(R.string.user_def_username_qm);
                tvUserCoins.setText(R.string.user_coin_qm);
                tvTitleCoinsMgr.setText(R.string.user_coin_mgr_qm);
                break;
            case SEL_SF:
                tvUserName.setText(R.string.user_def_username_sf);
                tvUserCoins.setText(R.string.user_coin_sf);
                tvTitleCoinsMgr.setText(R.string.user_coin_mgr_sf);
                break;
        }
    }
}
