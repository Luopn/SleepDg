package com.jx.sleep_dg.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.http.Config;
import com.jx.sleep_dg.http.OkHttpUtils;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.utils.MyApplication;
import com.jx.sleep_dg.utils.ToastUtil;
import com.jx.sleep_dg.view.dialog.DialogHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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

    public Unbinder mUnbinder;
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
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyUIDataSetChanged();
                        }
                    });
                }
            }
        };
        mLoadingDialog = DialogHelper.getLoadingDialog(this);
        initView();
    }

    //蓝牙数据变动，UI更新
    protected void notifyUIDataSetChanged() {

    }

    public Bundle getBundle() {
        return savedInstanceState;
    }

    private void initView() {
        flContent = findViewById(R.id.fl_content);
        toolbar = findViewById(R.id.fl_title);

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
        mUnbinder = ButterKnife.bind(this);
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
        if (mUnbinder != null)
            mUnbinder.unbind();

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
     * 设置标题栏颜色
     *
     * @param titleCol
     */
    public void setTitleCol(int titleCol) {
        tvTitle.setTextColor(titleCol);
    }

    /**
     * 设置标题栏是否隐藏
     *
     * @param visiable
     */
    public void setTitleLayoutVisiable(boolean visiable) {
        if (!visiable) {
            toolbar.setVisibility(View.GONE);
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
//    /**
//     * 6.0权限申请
//     */
//    // 配置需要取的权限
//    public final String[] PERMISSION = new String[]{Manifest.permission.CAMERA, // 相机
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.INTERNET,
//    };
//
//    public void permissions() {
//        if (PermissionsUtil.isGranted1(this, PERMISSION)) {
//
//        } else {
//            PermissionsUtil.requestPermission(this, new PermissionListener() {
//                @Override
//                public void permissionGranted(String[] permissions) {
//                }
//
//                @Override
//                public void permissionDenied(String[] permissions) {
//                }
//            }, PERMISSION);
//        }
//
//    }

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

    /**************************************************************************************************************/
    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
    public void doPost(final String trxcode, Map<String, String> map) {

//        com.zhy.http.okhttp.OkHttpUtils.post()
//                .url(Config.SERVER_ADDRESS + trxcode)
//                .params(map)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        LogUtil.e("错误原因：" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        LogUtil.e("接收到数据：" + response);
//
//                    }
//                });
        com.zhy.http.okhttp.OkHttpUtils.post()
                .url(Config.SERVER_ADDRESS + trxcode)
                .params(map)
                .build().execute(new StringCallback() {
            @Override
            public void onError(com.squareup.okhttp.Request request, Exception e) {
                LogUtil.e("错误原因：" + e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e("接收到数据：" + response);
            }
        });


////        FormBody formBody = map2FormBodys(map);
//        RequestBody formBody = Newmap2FormBodys(map);
//        //创建一个Request
//        Request request = new Request.Builder()
//                .url(Config.SERVER_ADDRESS + trxcode)
//                .post(formBody)
//                .build();
//        //发起异步请求，并加入回调
//        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                LogUtil.e("gdg", "onFailure: " + e.getMessage());
//                if (e instanceof ConnectTimeoutException) {
//                    onNetError();
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String resBody = response.body().string();
//                LogUtil.e("请求:" + trxcode + "\n\r" + "返回:" + resBody);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            JSONObject jsonObject = new JSONObject(resBody);
//                            String resCode = jsonObject.getString("status");
//                            String resMessage = jsonObject.getString("msg");
//                            String nettrxcode = jsonObject.getString("trxCode");
//                            Object jsonBody = jsonObject.get("data");
//                            if (resCode.equals("1")) {
//                                if (jsonBody instanceof JSONObject) {
//                                    onNetJSONObject((JSONObject) jsonBody, nettrxcode);
//                                } else if (jsonBody instanceof JSONArray) {
//                                    onNetJSONArray((JSONArray) jsonBody, nettrxcode);
//                                } else {
//                                    JSONObject jsonObject1 = new JSONObject();
//                                    onNetJSONObject(jsonObject1, nettrxcode);
//                                }
//
//                            } else {
//                                onNetError();
//                                ToastUtil.showMessage(resMessage);
//                            }
//                        } catch (JSONException e) {
//                            onNetError();
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
    }

    /**************************************************************************************************************/
    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
    public void doHeadPost(final String trxcode, Map<String, String> map, String token) {
        //创建一个Request
        Request request = null;
        if (map.size() == 0) {
            request = new Request.Builder()
                    .url(Config.SERVER_ADDRESS + trxcode)
                    .addHeader("token", token)
                    .build();
        } else {
            RequestBody formBody = Newmap2FormBodys(map);
            request = new Request.Builder()
                    .url(Config.SERVER_ADDRESS + trxcode)
                    .post(formBody)
                    .addHeader("token", token)
                    .build();
        }

        //发起异步请求，并加入回调
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("gdg", "onFailure: " + e.getMessage());
                if (e instanceof ConnectTimeoutException) {
                    onNetError();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resBody = response.body().string();
                LogUtil.e("请求:" + trxcode + "\n\r" + "返回:" + resBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(resBody);
                            String resCode = jsonObject.getString("status");
                            String resMessage = jsonObject.getString("msg");
                            String nettrxcode = jsonObject.getString("trxCode");
                            Object jsonBody = jsonObject.get("data");
                            if (resCode.equals("1")) {
                                if (jsonBody instanceof JSONObject) {
                                    onNetJSONObject(jsonObject, nettrxcode);
                                } else if (jsonBody instanceof JSONArray) {
                                    onNetJSONArray((JSONArray) jsonBody, nettrxcode);
                                } else {
                                    onSuccess();
                                }
                            } else {
                                onNetError();
                                ToastUtil.showMessage(resMessage);
                            }
                        } catch (JSONException e) {
                            onNetError();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
//    public void doMessagePost(final String trxcode, Map<String, String> map, List<File> othersList, String token) {
//        //创建一个Request
//        Request request = null;
//        if (map.size() == 0) {
//            request = new Request.Builder()
//                    .url(Config.SERVER_ADDRESS + trxcode)
//                    .addHeader("token", token)
//                    .build();
//            return;
//        }
//
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        //设置为表单类型
//        builder.setType(MultipartBody.FORM);
//        for (File file : othersList) {
//            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//            builder.addFormDataPart("file", (int) (Math.random() * (9999 - 1000 + 1)) + 1000 + ".png", fileBody);
//        }
//        //添加表单键值
//        Set<Map.Entry<String, String>> entries = map.entrySet();
//        for (Map.Entry<String, String> entry : entries) {
//            builder.addFormDataPart(entry.getKey(), entry.getValue());
//        }
//        request = new Request.Builder()
//                .url(Config.SERVER_ADDRESS + trxcode)
//                .post(builder.build())
//                .addHeader("token", token)
//                .build();
//
//        //发起异步请求，并加入回调
//        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                LogUtil.e("gdg", "onFailure: " + e.getMessage());
//                if (e instanceof ConnectTimeoutException) {
//                    onNetError();
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String resBody = response.body().string();
//                LogUtil.e("请求:" + trxcode + "\n\r" + "返回:" + resBody);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            JSONObject jsonObject = new JSONObject(resBody);
//                            String resCode = jsonObject.getString("status");
//                            String resMessage = jsonObject.getString("msg");
//                            String nettrxcode = jsonObject.getString("trxCode");
//                            Object jsonBody = jsonObject.get("data");
//                            if (resCode.equals("1")) {
//                                if (jsonBody instanceof JSONObject) {
//                                    onNetJSONObject(jsonObject, nettrxcode);
//                                } else if (jsonBody instanceof JSONArray) {
//                                    onNetJSONArray((JSONArray) jsonBody, nettrxcode);
//                                } else {
//                                    onSuccess();
//                                }
//
//                            } else {
//                                onNetError();
//                                ToastUtil.showMessage(resMessage);
//                            }
//                        } catch (JSONException e) {
//                            onNetError();
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//    }


    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
//    public void doHeadImgPost(final String trxcode, File file, String token) {
//
////        多个文件集合
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        //设置为表单类型
//        builder.setType(MultipartBody.FORM);
//        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//        builder.addFormDataPart("file", (int) (Math.random() * (9999 - 1000 + 1)) + 1000 + ".png", fileBody);
//        Request request = new Request.Builder()
//                .url(Config.SERVER_ADDRESS + trxcode)
//                .post(builder.build())
//                .addHeader("token", token)
//                .build();
////        //发起异步请求，并加入回调
//        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                LogUtil.e("gdg", "onFailure: " + e.getMessage());
//                if (e instanceof ConnectTimeoutException) {
//                    onNetError();
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String resBody = response.body().string();
//                LogUtil.e("请求:" + trxcode + "\n\r" + "返回:" + resBody);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            JSONObject jsonObject = new JSONObject(resBody);
//                            String resCode = jsonObject.getString("status");
//                            String resMessage = jsonObject.getString("msg");
//                            String nettrxcode = jsonObject.getString("trxCode");
//                            Object jsonBody = jsonObject.get("data");
//                            if (resCode.equals("1")) {
//                                if (jsonBody instanceof JSONObject) {
//                                    onNetJSONObject(jsonObject, nettrxcode);
//                                } else if (jsonBody instanceof JSONArray) {
//                                    onNetJSONArray((JSONArray) jsonBody, nettrxcode);
//                                } else {
//                                    JSONObject jsonObject1 = new JSONObject();
//                                    onNetJSONObject(jsonObject1, nettrxcode);
//                                }
//
//                            } else {
//                                onNetError();
//                                ToastUtil.showMessage(resMessage);
//                            }
//                        } catch (JSONException e) {
//                            onNetError();
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//    }

    /**
     * 把map转成frombody
     *
     * @param params
     * @return
     */
    public FormBody map2FormBodys(Map<String, String> params) {
        if (params == null) return null;
        FormBody.Builder builder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    /**
     * 把map转成RequestBody
     *
     * @param params
     * @return
     */
    public RequestBody Newmap2FormBodys(Map<String, String> params) {
        if (params == null) return null;
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            requestBody.addFormDataPart(entry.getKey(), entry.getValue());
            LogUtil.e(entry.getKey() + "" + entry.getValue());
        }

        return requestBody.build();
    }

    /**
     * 返回 JSONObject数据
     *
     * @param jsonObject 数据体
     * @param trxcode    请求方法
     */
    public void onNetJSONObject(JSONObject jsonObject, String trxcode) {

    }

    /**
     * 返回 JSONArray数据
     *
     * @param jsonArray 数据体
     * @param trxcode   请求方法
     */
    public void onNetJSONArray(JSONArray jsonArray, String trxcode) {

    }

    public void onNetError() {

    }

    public void onSuccess() {

    }


    public void doNewPost(final String trxcode, Map<String, String> params) {
        LogUtil.e("提交的参数" + params.toString());
//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("jsonParameters", str);
//        builder.add("trxcode", trxcode);
//        builder.add("encodeFlag", "1");
//        FormBody formBody = builder.build();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            String value = params.get(key);
            formBodyBuilder.add(key, value);
        }
        FormBody formBody = formBodyBuilder.build();
        //创建一个Request
        Request request = new Request.Builder()
                .url(Config.SERVER_ADDRESS + trxcode)
                .post(formBody)
                .build();
        //发起异步请求，并加入回调
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("gdg", "onFailure: " + e.getMessage());
                if (e instanceof ConnectTimeoutException) {
                    onNetError();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resBody = response.body().string();
                LogUtil.e("请求:" + trxcode + "\n\r" + "返回:" + resBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(resBody);
                            String resCode = jsonObject.getString("status");
                            String resMessage = jsonObject.getString("msg");
                            String nettrxcode = jsonObject.getString("trxCode");
                            Object jsonBody = jsonObject.get("data");
                            if (resCode.equals("1")) {
//                                if (jsonBody==null)
//                                boolean isHas = jsonBody.has("datas");
//                                LogUtil.e("isHas:" + isHas);
//                                if (isHas) {
//                                    JSONArray jsonArray = jsonBody.getJSONArray("datas");
//                                    onNetJSONArray(jsonArray, nettrxcode);
//                                } else {
                                JSONObject jsonObject1 = new JSONObject();
                                if (jsonBody instanceof JSONObject) {
                                    onNetJSONObject((JSONObject) jsonBody, nettrxcode);
                                } else if (jsonBody instanceof JSONArray) {
                                    onNetJSONArray((JSONArray) jsonBody, nettrxcode);
                                } else {
                                    onNetJSONObject(jsonObject1, nettrxcode);
                                }
//                                }
                            } else {
                                onNetError();
                                ToastUtil.showMessage(resMessage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
