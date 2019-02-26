package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.DisplayUtils;
import com.jx.sleep_dg.utils.PreferenceUtils;
import com.jx.sleep_dg.utils.ToastUtil;
import com.jx.sleep_dg.view.SegmentControl;
import com.wx.wheelview.adapter.BaseWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Locale;

public class InflationActivity extends BaseActivity {

    private RelativeLayout container;
    private WheelView<String> hourWv, minuteWv;
    private TextView tvCurTime;
    private LinearLayout llWeeksSel;
    private PopupWindow weeksPop;
    private SegmentControl scOnOff;

    private boolean isInitialDatas;
    private String hour, minute;
    private ArrayList<String> hourList, minuteList;

    private MSPProtocol mspProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_inflation);
        setToolbarTitle(R.string.auto_inflation);
        initDatas();

        bindView();
    }

    private void initDatas() {
        isInitialDatas = false;
        mspProtocol = MSPProtocol.getInstance();
        BleComUtils.sendTime("F10100000001");

        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            hourList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            minuteList.add(String.format(Locale.getDefault(), "%02d", i));
        }
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        if (mspProtocol != null) {
            if (!isInitialDatas && hourWv != null) {
                int hour = (mspProtocol.getTire_hour() & 0xff);
                int minute = (mspProtocol.getTire_minute() & 0xff);
                hourWv.setSelection(hour);
                minuteWv.setSelection(minute);

                isInitialDatas = true;
            }
            if (tvCurTime != null) {
                tvCurTime.setText(String.format("机器时间 %s:%s",
                        String.format(Locale.getDefault(), "%02d", mspProtocol.getTire_hour() & 0xff),
                        String.format(Locale.getDefault(), "%02d", mspProtocol.getTire_minute() & 0xff)));
            }
        }
    }

    @Override
    public void bindView() {
        container = findViewById(R.id.container);
        llWeeksSel = findViewById(R.id.ll_weeks);
        tvCurTime = findViewById(R.id.tv_cur_time);
        Button okButton = findViewById(R.id.ok);
        hourWv = findViewById(R.id.wv_hour);
        minuteWv = findViewById(R.id.wv_minute);
        scOnOff = findViewById(R.id.sc_on_off);

        if (PreferenceUtils.getBoolean(Constance.KEY_INFLATION_SWITCH, true)) {
            scOnOff.setSelectedIndex(0);
        } else {
            scOnOff.setSelectedIndex(1);
        }
        scOnOff.setOnSegmentChangedListener(new SegmentControl.OnSegmentChangedListener() {
            @Override
            public void onSegmentChanged(int newSelectedIndex) {
                int hourInt = Integer.valueOf(hour);
                int minuteInt = Integer.valueOf(minute);
                PreferenceUtils.putBoolean(Constance.KEY_INFLATION_SWITCH, newSelectedIndex == 0);
                if (newSelectedIndex == 0) {
                    BleComUtils.sendInflation(hourInt, minuteInt, 0xff);
                } else {
                    BleComUtils.sendInflation(hourInt, minuteInt, 0x0);
                }
            }
        });

        WheelView.WheelViewStyle wheelViewStyle = new WheelView.WheelViewStyle();
        wheelViewStyle.backgroundColor = Color.TRANSPARENT;
        wheelViewStyle.holoBorderColor = ContextCompat.getColor(this, R.color.textTitleColor);
        wheelViewStyle.selectedTextColor = ContextCompat.getColor(this, R.color.textTitleColor);
        wheelViewStyle.textColor = ContextCompat.getColor(this, R.color.textTitleColorLight);
        wheelViewStyle.textSize = 25;
        wheelViewStyle.selectedTextZoom = 1.5f;

        hourWv.setStyle(wheelViewStyle);
        minuteWv.setStyle(wheelViewStyle);

        hourWv.setWheelData(hourList);
        hourWv.setWheelAdapter(new BaseWheelAdapter<String>() {
            @Override
            protected View bindView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = LayoutInflater.from(InflationActivity.this).
                            inflate(R.layout.layout_item_infaltion_time, null);
                    viewHolder.textView = convertView.findViewById(R.id.tv_title);
                    viewHolder.textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                    viewHolder.textView.setPadding(0, 0, 20, 0);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.textView.setText(hourList.get(position));
                return convertView;
            }
        });
        hourWv.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int position, String s) {
                hour = s;
            }
        });

        minuteWv.setWheelData(minuteList);
        minuteWv.setWheelAdapter(new BaseWheelAdapter<String>() {
            @Override
            protected View bindView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = LayoutInflater.from(InflationActivity.this).
                            inflate(R.layout.layout_item_infaltion_time, null);
                    viewHolder.textView = convertView.findViewById(R.id.tv_title);
                    viewHolder.textView.setPadding(20, 0, 0, 0);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.textView.setText(minuteList.get(position));
                return convertView;
            }
        });
        minuteWv.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int position, String s) {
                minute = s;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(hour) && !TextUtils.isEmpty(minute)) {
                    int hourInt = Integer.valueOf(hour);
                    int minuteInt = Integer.valueOf(minute);
                    BleComUtils.sendInflation(hourInt, minuteInt, 0xff);

                    PreferenceUtils.putInt(Constance.KEY_INFLATION_HOUR, hourInt);
                    PreferenceUtils.putInt(Constance.KEY_INFLATION_MINUTE, minuteInt);

                    ToastUtil.showMessage("设置成功");
                }
            }
        });

        int hour = PreferenceUtils.getInt(Constance.KEY_INFLATION_HOUR, 0);
        int minute = PreferenceUtils.getInt(Constance.KEY_INFLATION_MINUTE, 0);
        hourWv.setSelection(hour);
        minuteWv.setSelection(minute);

        //选择重复星期
        llWeeksSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weeksPop == null) {
                    View contentView = LayoutInflater.from(InflationActivity.this).
                            inflate(R.layout.layout_weeks, null);
                    weeksPop = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    //weeksPop.setBackgroundDrawable(new ColorDrawable());
                    weeksPop.setAnimationStyle(R.style.AnimBottom);
                    weeksPop.showAtLocation(container, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                } else {
                    weeksPop.dismiss();
                    weeksPop = null;
                }
            }
        });
    }

    static class ViewHolder {
        TextView textView;
    }
}
