package com.jx.sleep_dg.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.http.InterfaceMethod;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.PreferenceUtils;
import com.jx.sleep_dg.utils.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册页面
 * Created by Administrator on 2018/5/21.
 */

public class RegistActivity extends BaseActivity {

    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.iv_password)
    ImageView iv_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_regist);
        mUnbinder = ButterKnife.bind(this);
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.register);
    }

    @OnClick({R.id.btn_finish, R.id.tv_code})
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
                //短信验证码
                Map<String, String> map = new HashMap<>();
                map.put("phone_number", etPhone.getText().toString());
                doPost(InterfaceMethod.YANZHENGMA, map);
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
        doPost(InterfaceMethod.ADDUSER, map);
    }

    @Override
    public void onNetJSONObject(JSONObject jsonObject, String trxcode) {
        super.onNetJSONObject(jsonObject, trxcode);
        if (InterfaceMethod.ADDUSER.equals(trxcode)) {
            ToastUtil.showMessage(R.string.register_success);
            PreferenceUtils.putString(Constance.USERNAME, "");
            PreferenceUtils.putString(Constance.PASSWORD, "");
            finish();
        }
    }
}
