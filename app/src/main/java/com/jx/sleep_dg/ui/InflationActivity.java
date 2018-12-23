package com.jx.sleep_dg.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.wx.wheelview.adapter.BaseWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Locale;

public class InflationActivity extends BaseActivity {

    private WheelView<String> hourWv, minuteWv;
    private ArrayList<String> hourList, minuteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_inflation);
        setToolbarTitle(R.string.auto_inflation);

        initDatas();

    }

    private void initDatas() {
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();

        for (int i = 0; i < 23; i++) {
            hourList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        for (int i = 0; i < 59; i++) {
            minuteList.add(String.format(Locale.getDefault(), "%02d", i));
        }
    }

    @Override
    public void bindView() {
        hourWv = findViewById(R.id.wv_hour);
        minuteWv = findViewById(R.id.wv_minute);
        hourWv.setWheelData(hourList);
        hourWv.setWheelAdapter(new BaseWheelAdapter<String>() {
            @Override
            protected View bindView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = LayoutInflater.from(InflationActivity.this).inflate(android.R.layout.simple_list_item_1, null);
                    viewHolder.textView = convertView.findViewById(android.R.id.text1);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.textView.setText(mList.get(position));
                return convertView;
            }
        });
        minuteWv.setWheelData(minuteList);
        minuteWv.setWheelAdapter(new BaseWheelAdapter<String>() {
            @Override
            protected View bindView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        });
    }

    static class ViewHolder {
        TextView textView;
    }
}
