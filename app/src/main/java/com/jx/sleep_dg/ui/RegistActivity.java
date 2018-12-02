package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册页面
 * Created by Administrator on 2018/5/21.
 */

public class RegistActivity extends BaseActivity {

    Button btnFinish;
    TextView tvCode;
    EditText etPhone;
    EditText etCode;
    EditText etPwd;
    ImageView iv_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_regist);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.register);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_finish:
                //注册
                doRegist();
                break;
            case R.id.tv_code:
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    etPhone.requestFocus();
                    etPhone.setError(getResources().getString(R.string.normal_input_null));
                    return;
                }
                break;
        }
    }

    private void doRegist() {
        String phone = etPhone.getText().toString();
        String code = etCode.getText().toString();
        String pwd = etPwd.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            etPhone.requestFocus();
            etPhone.setError(getResources().getString(R.string.normal_input_null));
            return;
        }

        if (TextUtils.isEmpty(code)) {
            etCode.requestFocus();
            etCode.setError(getResources().getString(R.string.normal_input_null));
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            etPwd.requestFocus();
            etPwd.setError(getResources().getString(R.string.normal_input_null));
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("phone_number", phone);
        map.put("password", pwd);
        map.put("messagecode", code);
    }
}
