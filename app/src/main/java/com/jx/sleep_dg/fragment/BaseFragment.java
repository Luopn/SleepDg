package com.jx.sleep_dg.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jx.sleep_dg.ble.BluetoothLeService;
import com.jx.sleep_dg.http.Config;
import com.jx.sleep_dg.http.OkHttpUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.utils.ToastUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 覃微 on 2018/6/6.
 */

public abstract class BaseFragment extends SupportFragment implements View.OnClickListener {

    private Unbinder mUnBinder;
    private BroadcastReceiver receiver;//接收蓝牙广播

    public View mRootView;

    protected abstract int getLayoutId();

    public abstract void onBindView(View view);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //监听蓝牙状态
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyUIDataSetChanged(intent);
                        }
                    });
                } else if (intent.getAction() != null && intent.getAction().equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDeviceDisconnected();
                        }
                    });
                }
            }
        };
    }

    //更新UI数据
    protected void notifyUIDataSetChanged(Intent intent) {
    }

    protected void notifyDeviceDisconnected() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (getLayoutId() != 0) {
            view = inflater.inflate(getLayoutId(), container, false);
            mUnBinder = ButterKnife.bind(this, view);
            mRootView = view;
            onBindView(view);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null)
            getActivity().registerReceiver(receiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null)
            getActivity().unregisterReceiver(receiver);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null) mUnBinder.unbind();
    }

    @Override
    public void onClick(View v) {

    }

    //蓝牙数据相关
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    /**************************************************************************************************************/
    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
    public void doPost(final String trxcode, String str) {
        LogUtil.e("提交的参数" + str);
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("jsonParameters", str);
        builder.add("trxcode", trxcode);
        builder.add("encodeFlag", "1");
        FormBody formBody = builder.build();
        //创建一个Request
        Request request = new Request.Builder()
                .url(Config.SERVER_ADDRESS)
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(resBody);
                            String resCode = jsonObject.getString("resCode");
                            String resMessage = jsonObject.getString("resMessage");
                            String nettrxcode = jsonObject.getString("trxcode");
                            JSONObject jsonBody = jsonObject.getJSONObject("resBody");
                            if (resCode.equals("0000")) {
                                boolean isHas = jsonBody.has("datas");
                                LogUtil.e("isHas:" + isHas);
                                if (isHas) {
                                    JSONArray jsonArray = jsonBody.getJSONArray("datas");
                                    onNetJSONArray(jsonArray, nettrxcode);
                                } else {
                                    onNetJSONObject(jsonBody, nettrxcode);
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
    /**************************************************************************************************************/
    /**
     * 这个公共方法只对应登录注册我忘记密码等
     *
     * @param trxcode 请求的方法名称
     */
    public void doHeadPost(final String trxcode, Map<String, String> map, String token) {
//        LogUtil.e("返回", map.toString());
        FormBody formBody = map2FormBodys(map);
        //创建一个Request
        Request request = new Request.Builder()
                .url(Config.SERVER_ADDRESS + trxcode)
                .post(formBody)
                .addHeader("token", token)
                .build();
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
                getActivity().runOnUiThread(new Runnable() {
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
                                    JSONObject jsonObject1 = new JSONObject();
                                    onNetJSONObject(jsonObject1, nettrxcode);
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

    public void doHeadPostTag(final String trxcode, Map<String, String> map, String token, final String tag) {
        LogUtil.e("返回", map.toString());
        FormBody formBody = map2FormBodys(map);
        //创建一个Request
        Request request = new Request.Builder()
                .url(Config.SERVER_ADDRESS + trxcode)
                .post(formBody)
                .addHeader("token", token)
                .tag(tag)
                .build();
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
                getActivity().runOnUiThread(new Runnable() {
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
                                    onNetJSONObject((JSONObject) jsonBody, tag);
                                } else if (jsonBody instanceof JSONArray) {
                                    onNetJSONArray((JSONArray) jsonBody, tag);
                                } else {
                                    JSONObject jsonObject1 = new JSONObject();
                                    onNetJSONObject(jsonObject1, tag);
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
}
