package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.http.InterfaceMethod;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.MyApplication;
import com.jx.sleep_dg.utils.PreferenceUtils;
import com.jx.sleep_dg.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by 覃微 on 2018/5/17.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_forget_pw)
    TextView tv_forget_pw;
    @BindView(R.id.btn_login)
    TextView btn_login;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.iv_user_view)
    ImageView iv_user_view;
    @BindView(R.id.iv_password_view)
    ImageView iv_password_view;
    @BindView(R.id.ll_login)
    LinearLayout ll_login;
    View iv_login_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_login);
        bindView();
    }

    @Override
    public void bindView() {
        setTitleLayoutVisiable(false);
        iv_login_view = findViewById(R.id.iv_login_view);

        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

        String username = PreferenceUtils.getString(Constance.USERNAME);
        String password = PreferenceUtils.getString(Constance.PASSWORD);
        if (!TextUtils.isEmpty(username)) {
            et_phone.setText(username);
        }
        String isauto = PreferenceUtils.getString(Constance.ISAUTO);
        if (!TextUtils.isEmpty(password) && "1".equals(isauto)) {
            et_password.setText(password);
            doLogin();
        }
    }

//    public void OnClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_register:
//                //注册
//                startActivity(new Intent(this, RegistActivity.class));
//                break;
//            case R.id.tv_forget_pw:
//                //忘记密码
////                startActivity(new Intent(this, ForgetPwdActivity.class));
//                break;
//            case R.id.btn_login:
////                doLogin();
//                break;
//        }
//    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_register:
                //注册
                startActivity(new Intent(this, RegistActivity.class));
                break;

            case R.id.btn_login:
                //登录
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    private void doLogin() {
        String phone = et_phone.getText().toString();
        String pwd = et_password.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            et_phone.requestFocus();
            et_phone.setError(getResources().getString(R.string.normal_input_null));
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            et_password.requestFocus();
            et_password.setError(getResources().getString(R.string.normal_input_null));
            return;
        }
//        showLoadingDialog(getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("phone_number", phone);
        map.put("password", pwd);
        doPost(InterfaceMethod.LOGIN, map);
    }

    @Override
    public void onNetJSONObject(JSONObject jsonObject, String trxcode) {
        super.onNetJSONObject(jsonObject, trxcode);
        try {
            if (InterfaceMethod.LOGIN.equals(trxcode)) {
                hideLoadingDialog();

                String phone = et_phone.getText().toString();
                String pwd = et_password.getText().toString();
                PreferenceUtils.putString(Constance.USERNAME, phone);
                PreferenceUtils.putString(Constance.PASSWORD, pwd);

                String token = jsonObject.getInt("user_id") + "";
                PreferenceUtils.putString(Constance.TOKEN, token);
                PreferenceUtils.putString(Constance.ISAUTO, "1");
                JSONArray jsonArray = jsonObject.getJSONArray("bluetoothInfo");
                if (jsonArray.length() > 0) {
                    //有设备
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject js = jsonArray.getJSONObject(k);
                        String url = js.getString("url");
                        int isUse = js.getInt("isUse");
//                        if (isUse == 1) {
//                            PreferenceUtils.putString(Constance.ADDRESS, js.getString("url"));
//                            PreferenceUtils.putString(Constance.BLE_NAME, js.getString("name"));
//                            PreferenceUtils.putString(Constance.BLE_ID, js.getString("id"));
////                            LauncherActivity.mBLE.connect(url);
//                            LauncherActivity.C(url);
//
//                        }
                        if (isUse == 1) {
                            PreferenceUtils.putString(Constance.ADDRESS, js.getString("url"));
                            PreferenceUtils.putString(Constance.BLE_NAME, js.getString("name"));
                            PreferenceUtils.putString(Constance.BLE_ID, js.getString("id"));
                            LauncherActivity.mBLE.connect(js.getString("url"));
                        }
                    }
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    //没有设备
//                    Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
//                    intent.putExtra("flag", "Login");
//                    startActivity(intent);
                }
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (LauncherActivity.mBLE != null)
                LauncherActivity.mBLE.disconnect();
            MyApplication.getInstance().extiApp();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
