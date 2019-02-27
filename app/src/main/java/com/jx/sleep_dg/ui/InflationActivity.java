package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.Constance;
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
    private TextView tvWeeks;
    private PopupWindow weeksPop;
    private SegmentControl scOnOff;

    private boolean isInitialDatas;
    private String hour, minute;
    private int weeksByte;
    private ArrayList<String> hourList, minuteList, weeksList;

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
        weeksList = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            hourList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            minuteList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        for (int i = 0; i < 7; i++) {
            String str = "";
            switch (i + 1) {
                case 1:
                    str = "周一";
                    break;
                case 2:
                    str = "周二";
                    break;
                case 3:
                    str = "周三";
                    break;
                case 4:
                    str = "周四";
                    break;
                case 5:
                    str = "周五";
                    break;
                case 6:
                    str = "周六";
                    break;
                case 7:
                    str = "周日";
                    break;
            }
            weeksList.add(str);
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
        tvWeeks = findViewById(R.id.tv_weeks);
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
        //处理自动充气开关
        scOnOff.setOnSegmentChangedListener(new SegmentControl.OnSegmentChangedListener() {
            @Override
            public void onSegmentChanged(int newSelectedIndex) {
                int hourInt = Integer.valueOf(hour);
                int minuteInt = Integer.valueOf(minute);
                if (newSelectedIndex == 0) {
                    weeksByte = PreferenceUtils.getInt(Constance.KEY_INFLATION_WEEK, 0xff);
                    BleComUtils.sendInflation(hourInt, minuteInt, weeksByte);
                } else {
                    BleComUtils.sendInflation(hourInt, minuteInt, 0x0);
                }
                PreferenceUtils.putBoolean(Constance.KEY_INFLATION_SWITCH, newSelectedIndex == 0);
            }
        });
        //处理自动充气时间
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
                DaysViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new DaysViewHolder();
                    convertView = LayoutInflater.from(InflationActivity.this).
                            inflate(R.layout.layout_item_infaltion_time, null);
                    viewHolder.textView = convertView.findViewById(R.id.tv_title);
                    viewHolder.textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                    viewHolder.textView.setPadding(0, 0, 20, 0);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (DaysViewHolder) convertView.getTag();
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
                DaysViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new DaysViewHolder();
                    convertView = LayoutInflater.from(InflationActivity.this).
                            inflate(R.layout.layout_item_infaltion_time, null);
                    viewHolder.textView = convertView.findViewById(R.id.tv_title);
                    viewHolder.textView.setPadding(20, 0, 0, 0);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (DaysViewHolder) convertView.getTag();
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
        //确认发送命令
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(hour) && !TextUtils.isEmpty(minute)) {
                    int hourInt = Integer.valueOf(hour);
                    int minuteInt = Integer.valueOf(minute);
                    int weeksByte = PreferenceUtils.getInt(Constance.KEY_INFLATION_WEEK, 0xff);
                    BleComUtils.sendInflation(hourInt, minuteInt, weeksByte);

                    PreferenceUtils.putInt(Constance.KEY_INFLATION_HOUR, hourInt);
                    PreferenceUtils.putInt(Constance.KEY_INFLATION_MINUTE, minuteInt);

                    ToastUtil.showMessage("设置成功");
                }
            }
        });

        //显示保存设置的时和分
        int hour = PreferenceUtils.getInt(Constance.KEY_INFLATION_HOUR, 0);
        int minute = PreferenceUtils.getInt(Constance.KEY_INFLATION_MINUTE, 0);
        hourWv.setSelection(hour);
        minuteWv.setSelection(minute);

        //处理自动充气重复星期
        weeksByte = PreferenceUtils.getInt(Constance.KEY_INFLATION_WEEK, 0xff);
        onWeeksSelected();

        llWeeksSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWeekPopMenu();
            }
        });
    }

    //显示选择的星期
    private void onWeeksSelected() {
        StringBuilder weeksStr = new StringBuilder("");
        for (int i = 0; i < 7; i++) {
            boolean select = ((weeksByte >> i) & 1) == 1;
            if (!select) continue;
            switch (i) {
                case 0:
                    weeksStr.append(" 周一");
                    break;
                case 1:
                    weeksStr.append(" 周二");
                    break;
                case 2:
                    weeksStr.append(" 周三");
                    break;
                case 3:
                    weeksStr.append(" 周四");
                    break;
                case 4:
                    weeksStr.append(" 周五");
                    break;
                case 5:
                    weeksStr.append(" 周六");
                    break;
                case 6:
                    weeksStr.append(" 周日");
                    break;
            }
        }
        tvWeeks.setText(weeksStr.toString());
    }

    //显示星期选择菜单
    private void showWeekPopMenu() {
        weeksByte = PreferenceUtils.getInt(Constance.KEY_INFLATION_WEEK, 0xff);
        if (weeksPop == null) {
            View contentView = LayoutInflater.from(InflationActivity.this).
                    inflate(R.layout.layout_weeks, null);
            RecyclerView rvWeeks = contentView.findViewById(R.id.rv_weeks);
            contentView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (weeksPop != null) {
                        weeksPop.dismiss();
                    }
                    PreferenceUtils.putInt(Constance.KEY_INFLATION_WEEK, weeksByte);
                    onWeeksSelected();
                }
            });
            contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (weeksPop != null) {
                        weeksPop.dismiss();
                    }
                }
            });
            rvWeeks.setAdapter(new RecyclerView.Adapter<WeeksViewHolder>() {
                @NonNull
                @Override
                public WeeksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View contentView = LayoutInflater.from(InflationActivity.this).inflate(R.layout.item_weeks, viewGroup, false);
                    return new WeeksViewHolder(contentView);
                }

                @Override
                public void onBindViewHolder(@NonNull WeeksViewHolder weeksViewHolder, final int i) {
                    weeksViewHolder.tvWeeks.setText(weeksList.get(i));

                    weeksViewHolder.cbWeeks.setChecked(((weeksByte >> i) & 1) == 1);
                    weeksViewHolder.cbWeeks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                weeksByte = weeksByte | (1 << i);
                            } else {
                                weeksByte = weeksByte & (~(1 << i));
                            }
                        }
                    });
                }

                @Override
                public int getItemCount() {
                    return 7;
                }
            });
            weeksPop = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            //weeksPop.setBackgroundDrawable(new ColorDrawable());
            weeksPop.setAnimationStyle(R.style.AnimBottom);
            weeksPop.showAtLocation(container, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            weeksPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    weeksPop = null;
                }
            });
        }
    }

    static class DaysViewHolder {
        TextView textView;
    }

    class WeeksViewHolder extends RecyclerView.ViewHolder {

        TextView tvWeeks;
        CheckBox cbWeeks;

        WeeksViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeeks = itemView.findViewById(R.id.tv_weeek);
            cbWeeks = itemView.findViewById(R.id.cb_week);
        }

    }
}
