package com.jx.sleep_dg.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import com.jx.sleep_dg.http.Config;
import com.jx.sleep_dg.http.OkHttpUtils;
import com.jx.sleep_dg.permission.PermissionListener;
import com.jx.sleep_dg.permission.PermissionsUtil;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.utils.MyApplication;
import com.jx.sleep_dg.utils.ToastUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.yokeyword.fragmentation.SupportActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 覃微 on 2018/6/6.
 */

public abstract class NewBaseActivity extends SupportActivity implements View.OnClickListener {

    private LayoutInflater inflater;
    private FrameLayout flContent;
    private Toolbar toolbar;

    private TextView tvTitle;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.base_title);
        initView();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
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
     * @param Color
     */
    public void setTitleCenterColor(int Color) {
        tvTitle.setTextColor(Color);
    }

    /**
     * 设置标题栏背景颜色
     *
     * @param Color
     */
    public void setTitleBgColor(int Color) {
        toolbar.setBackgroundColor(Color);
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
     * 公共出右侧按键点击事件
     */
    public void onRightLisenter() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View view) {
    }


    /*************************************其他相关***************************************************/
    /**
     * 6.0权限申请
     */
    // 配置需要取的权限
    public final String[] PERMISSION = new String[]{Manifest.permission.CAMERA, // 相机
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
    };

    public void permissions() {
        if (PermissionsUtil.isGranted1(this, PERMISSION)) {

        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(String[] permissions) {
                }

                @Override
                public void permissionDenied(String[] permissions) {
                }
            }, PERMISSION);
        }

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
        if (view != null && (view instanceof EditText)) {

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
     * 没有登录(随便看看)
     */
    public void onLoginDialog() {

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

    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
    public void doPost(final String trxcode, Map<String, String> map) {
//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("jsonParameters", str);
//        builder.add("trxcode", trxcode);
//        builder.add("encodeFlag", "1");
        FormBody formBody = map2FormBodys(map);
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
                try {
                    JSONObject jsonObject = new JSONObject(resBody);
                    int resCode = jsonObject.getInt("respCode");
                    String respDesc = jsonObject.getString("respDesc");
                    if (resCode == 1) {
                        Object object = jsonObject.get("data");
                        if (object instanceof JSONObject) {
                            JSONObject jsonData = jsonObject.getJSONObject("data");
                            onNetJSONObject(jsonData, trxcode);
                        } else {
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            onNetJSONArray(jsonData, trxcode);
                        }
                    } else {
                        onNetError();
                        ToastUtil.showMessage(respDesc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
    public void doPostHeader(final String trxcode, Map<String, String> map) {
//        String token = PreferenceUtils.getString(Constant.TOKEN);
        FormBody formBody = map2FormBodys(map);
        Request request = new Request.Builder()
                .url(Config.SERVER_ADDRESS + trxcode)
                .post(formBody)
//                .addHeader("Authorization", token)
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
                try {
                    JSONObject jsonObject = new JSONObject(resBody);
                    int resCode = jsonObject.getInt("respCode");
                    String respDesc = jsonObject.getString("respDesc");
                    if (resCode == 1) {
                        if (jsonObject.has("data")) {
                            Object object = jsonObject.get("data");
                            if (object instanceof JSONObject) {
                                JSONObject jsonData = jsonObject.getJSONObject("data");
                                onNetJSONObject(jsonData, trxcode);
                            } else if (object instanceof JSONArray) {
                                JSONArray jsonData = jsonObject.getJSONArray("data");
                                onNetJSONArray(jsonData, trxcode);
                            }
                        } else {
                            JSONObject jsonObject1 = new JSONObject();
                            onNetJSONObject(jsonObject1, trxcode);
                        }

                    } else {
                        onNetError();
                        ToastUtil.showMessage(respDesc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 图片上传
     *
     * @param map
     */
    public void upImg(Map<String, String> map, List<File> othersList) {
        //多个文件集合
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置为表单类型
        builder.setType(MultipartBody.FORM);
        //添加表单键值
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        String othersKey = null;
        for (File file : othersList) {
            othersKey = "file1";
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            builder.addFormDataPart(othersKey, file.getName(), fileBody);
        }
        Request request = new Request.Builder()
                .url(Config.SERVER_ADDRESS)
                .post(builder.build())
                .build();
        //发起异步请求，并加入回调
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resBody = response.body().string();
                LogUtil.e("返回:" + resBody);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(resBody);
                            String resCode = jsonObject.getString("resCode");
                            String resMessage = jsonObject.getString("resMessage");
                            // String nettrxcode = jsonObject.getString("trxcode");
                            JSONObject jsonBody = jsonObject.getJSONObject("resBody");
                            if (resCode.equals("0000")) {
                                boolean isHas = jsonBody.has("datas");
                                LogUtil.e("isHas:" + isHas);
                                if (isHas) {
                                    JSONArray jsonArray = jsonBody.getJSONArray("datas");
//                                    onNetJSONArray(jsonArray, nettrxcode);
                                    onNetJSONArray(jsonArray, "");
                                } else {
//                                    onNetJSONObject(jsonBody, nettrxcode);
                                    onNetJSONObject(jsonBody, "");
                                }
                            } else if (resCode.equals("9999")) {
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

}
