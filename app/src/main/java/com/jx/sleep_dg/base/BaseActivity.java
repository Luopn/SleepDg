package com.jx.sleep_dg.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jx.sleep_dg.MyApplication;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.utils.QMUIStatusBarHelper;
import com.jx.sleep_dg.view.dialog.DialogHelper;

import me.yokeyword.fragmentation.SupportActivity;


/**
 * 作者：  王静波
 * 日期：  2018/2/27
 * 注明：
 */

public abstract class BaseActivity extends SupportActivity implements View.OnClickListener {

    private LayoutInflater inflater;

    private FrameLayout flContent;
    private Toolbar toolbar;
    private TextView tvTitle;
    protected Dialog mLoadingDialog = null;

    private BroadcastReceiver receiver;//接收蓝牙广播

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.base_title);
        //监听蓝牙状态
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyBleDataChanged(intent);
                        }
                    });
                }
            }
        };
        mLoadingDialog = DialogHelper.getLoadingDialog(this);
        initView();
    }

    //蓝牙数据变动，UI更新
    protected void notifyBleDataChanged(Intent intent) {

    }

    public Bundle getBundle() {
        return savedInstanceState;
    }

    private void initView() {
        flContent = findViewById(R.id.fl_content);
        toolbar = findViewById(R.id.toolbar);

        //配置toolbar
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle = findViewById(R.id.tv_title);
        inflater = LayoutInflater.from(this);
    }

    public abstract void bindView();

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 重写设置Activity布局文件
     *
     * @param layoutId activity引用的布局
     */
    public void setLayout(int layoutId) {
        flContent.removeAllViews();
        View view = inflater.inflate(layoutId, null);
        flContent.addView(view);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }

        MyApplication.getInstance().removeActivity(this);
    }

    protected void showLoadingDialog(String str) {
        if (mLoadingDialog != null) {
            TextView tv = (TextView) mLoadingDialog.findViewById(R.id.tv_load_dialog);
            tv.setText(str);
            mLoadingDialog.show();
        }
    }

    protected void showLoadingDialog(@StringRes int str) {
        if (mLoadingDialog != null) {
            TextView tv = (TextView) mLoadingDialog.findViewById(R.id.tv_load_dialog);
            tv.setText(str);
            mLoadingDialog.show();
        }
    }

    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 设置标题栏标题
     *
     * @param title
     */
    public void setToolbarTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 设置标题栏标题
     *
     * @param stringRes
     */
    public void setToolbarTitle(@StringRes int stringRes) {
        tvTitle.setText(getResources().getString(stringRes));
    }

    /**
     * 设置标题栏背景
     *
     * @param color
     */
    public void setToolbarBackColor(@ColorInt int color) {
        toolbar.setBackgroundColor(color);
    }


    /**
     * 设置标题栏颜色
     *
     * @param titleCol
     */
    public void setTitleCol(@ColorInt int titleCol) {
        tvTitle.setTextColor(titleCol);
        QMUIStatusBarHelper.translucent(this, titleCol);
    }

    /**
     * 设置标题栏是否隐藏
     *
     * @param visiable
     */
    public void setTitleLayoutVisiable(boolean visiable) {
        if (!visiable) {
            toolbar.setVisibility(View.GONE);
            QMUIStatusBarHelper.translucent(this);
        }
    }

    /**
     * 设置标题栏是否隐藏
     *
     * @param clcor
     */
    public void setTitleLayout(int clcor) {
        toolbar.setBackgroundColor(clcor);
    }

    @Override
    public boolean onSupportNavigateUp() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        finish();
        return super.onSupportNavigateUp();
    }

    /*************************************其他相关***************************************************/
    //蓝牙数据相关
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    public void hideSoftInput(final Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();

            //如果不是落在EditText区域，则需要关闭输入法
            if (HideKeyboard(v, ev)) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private boolean HideKeyboard(View view, MotionEvent event) {
        if ((view instanceof EditText)) {

            int[] location = {0, 0};
            view.getLocationInWindow(location);

            //获取现在拥有焦点的控件view的位置，即EditText
            int left = location[0], top = location[1], bottom = top + view.getHeight(), right =
                    left + view.getWidth();
            //判断我们手指点击的区域是否落在EditText上面，如果不是，则返回true，否则返回false
            boolean isInEt = (event.getX() > left
                    && event.getX() < right
                    && event.getY() > top
                    && event.getY() < bottom);
            return !isInEt;
        }
        return false;
    }

}
