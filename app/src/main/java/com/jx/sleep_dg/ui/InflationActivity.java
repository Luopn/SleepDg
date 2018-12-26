package com.jx.sleep_dg.ui;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.utils.ToastUtil;
import com.wx.wheelview.adapter.BaseWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Locale;

public class InflationActivity extends BaseActivity {

    private Button okButton;
    private WheelView<String> hourWv, minuteWv;

    private String hour, minute;
    private ArrayList<String> hourList, minuteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_inflation);
        setToolbarTitle(R.string.auto_inflation);
        initDatas();

        bindView();
    }

    private void initDatas() {
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
    public void bindView() {
        okButton = findViewById(R.id.ok);
        hourWv = findViewById(R.id.wv_hour);
        minuteWv = findViewById(R.id.wv_minute);

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
                    BleComUtils.sendInflation(Integer.valueOf(hour), Integer.valueOf(minute));
                    ToastUtil.showMessage("设置成功");
                }
            }
        });
    }

    static class ViewHolder {
        TextView textView;
    }
}
