package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jx.sleep_dg.MyApplication;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.LanguageUtil;
import com.jx.sleep_dg.utils.PreferenceUtils;

import java.util.Locale;

/**
 * Created by 覃微 on 2018/5/17.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private RadioButton rbChSimple;
    private RadioButton rbChTradition;
    private RadioButton rbEn;
    private Button btnLogin;
    private EditText et_phone;
    private EditText et_password;
    private ImageView ivLogin;
    private TextView tvLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_login);
        bindView();
    }

    @Override
    public void bindView() {
        setTitleLayoutVisiable(false);

        ScrollView mScrollView = findViewById(R.id.scrollView);
        rbChSimple = findViewById(R.id.rb_ch_simple);
        rbChTradition = findViewById(R.id.rb_ch_tradition);
        rbEn = findViewById(R.id.rb_en);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        findViewById(R.id.tv_register).setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        rbChSimple.setOnClickListener(this);
        rbChTradition.setOnClickListener(this);
        rbEn.setOnClickListener(this);
        ivLogin = findViewById(R.id.iv_login);
        tvLogin = findViewById(R.id.tv_login);

        Locale locale = (Locale) PreferenceUtils.deSerialization(PreferenceUtils.getString(LanguageUtil.LANGUAGE));
        if (locale != null) {
            if (locale.getCountry().equals(Locale.SIMPLIFIED_CHINESE.getCountry())) {
                rbChSimple.setChecked(true);
            }
            if (locale.getCountry().equals(Locale.TRADITIONAL_CHINESE.getCountry())) {
                rbChTradition.setChecked(true);
            }
            if (locale.getCountry().equals(Locale.US.getCountry())) {
                rbEn.setChecked(true);
            }
        }

        String username = PreferenceUtils.getString(Constance.USERNAME);
        String password = PreferenceUtils.getString(Constance.PASSWORD);
        if (!TextUtils.isEmpty(username)) {
            et_phone.setText(username);
        }
        String isauto = PreferenceUtils.getString(Constance.ISAUTO);
        if (!TextUtils.isEmpty(password) && "1".equals(isauto)) {
            et_password.setText(password);
        }

        switch (getApplication().getPackageName()) {
            case Constance.QM:
                ivLogin.setImageResource(R.mipmap.ic_launcher_qm);
                tvLogin.setText(R.string.login_welcome_qm);
                break;
            case Constance.SF:
                ivLogin.setImageResource(R.mipmap.ic_launcher_sf);
                tvLogin.setText(R.string.login_welcome_sf);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        switch (view.getId()) {
            case R.id.tv_register:
                //注册
                intent.setClass(this, RegistActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                //登录
                switch (getApplication().getPackageName()) {
                    case Constance.QM:
                        intent.setClass(this, MainQmActivity.class);
                        break;
                    default:
                        intent.setClass(this, MainActivity.class);
                        break;
                }
                startActivity(intent);
                finish();
                break;
            case R.id.rb_ch_simple:
                LanguageUtil.changeAppLanguage(this, Locale.SIMPLIFIED_CHINESE, true);
                restartApplication();
                break;
            case R.id.rb_ch_tradition:
                LanguageUtil.changeAppLanguage(this, Locale.TRADITIONAL_CHINESE, true);
                restartApplication();
                break;
            case R.id.rb_en:
                LanguageUtil.changeAppLanguage(this, Locale.US, true);
                restartApplication();
                break;
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

    private void restartApplication() {
        //切换语言信息，需要重启 Activity 才能实现
        Intent intent = new Intent(this, LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
