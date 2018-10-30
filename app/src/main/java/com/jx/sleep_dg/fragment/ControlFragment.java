package com.jx.sleep_dg.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jx.sleep_dg.Bean.ControlBean;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.DeviceLiftAcyivity;
import com.jx.sleep_dg.ui.DeviceTempActivity;
import com.jx.sleep_dg.ui.DeviseHardnessActivity;
import com.jx.sleep_dg.view.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制
 * Created by Administrator on 2018/7/20.
 */

public class ControlFragment extends BaseFragment {

    private MSPProtocol mspProtocol;

    private TextView tvHeadHigh, tvTailHigh;//床头，床尾高度
    private TextView tvLHardness, tvRHardness;//左，右床位硬度
    private MyListView listview;
    private List<ControlBean> controlList;

    private Myadapter myadapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_control;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onBindView(View view) {
        controlList = new ArrayList<>();
        tvHeadHigh = view.findViewById(R.id.tv_head_high);
        tvTailHigh = view.findViewById(R.id.tv_tail_high);
        tvLHardness = view.findViewById(R.id.tv_l_hardness);
        tvRHardness = view.findViewById(R.id.tv_r_hardness);
        view.findViewById(R.id.ll_hardness).setOnClickListener(this);
        view.findViewById(R.id.ll_lift).setOnClickListener(this);
        view.findViewById(R.id.ll_temp).setOnClickListener(this);
        listview = view.findViewById(R.id.listview);
        myadapter = new Myadapter(getActivity(), 0, controlList);
        listview.setAdapter(myadapter);
        for (int i = 0; i < 3; i++) {
            ControlBean bean = new ControlBean();
            bean.setId(i + "");
            bean.setName(getResources().getString(R.string.device) + i);
            controlList.add(bean);
        }
        myadapter.notifyDataSetChanged();

        mspProtocol = MSPProtocol.getInstance();
        bindViewData();
    }

    @Override
    protected void notifyUIDataSetChanged() {
        super.notifyUIDataSetChanged();
        bindViewData();
    }

    //绑定UI数据
    private void bindViewData() {
        if (mspProtocol != null) {
            tvHeadHigh.setText(String.format("%s", mspProtocol.getHigh1()));
            tvTailHigh.setText(String.format("%s", mspProtocol.getHigh2()));
            tvLHardness.setText(String.format("%s", mspProtocol.getlPresureCurVal()));
            tvRHardness.setText(String.format("%s", mspProtocol.getrPresureCurVal()));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_lift:
                //床位升降
                startActivity(new Intent(getActivity(), DeviceLiftAcyivity.class));
                break;
            case R.id.ll_hardness:
                //床位硬度
                startActivity(new Intent(getActivity(), DeviseHardnessActivity.class));
                break;
            case R.id.ll_temp:
                //床位温度
                startActivity(new Intent(getActivity(), DeviceTempActivity.class));
                break;
        }
    }

    class Myadapter extends ArrayAdapter<ControlBean> {

        public Myadapter(@NonNull Context context, int resource, @NonNull List<ControlBean> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getContext(), R.layout.listview_item_control, null);
                holder.tvName = convertView.findViewById(R.id.tv_name);
                holder.tvUse = convertView.findViewById(R.id.tv_use);
                holder.tvMode = convertView.findViewById(R.id.tv_mode);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(getItem(position).getName());
            return convertView;
        }
    }

    class ViewHolder {
        TextView tvName;
        TextView tvUse;
        TextView tvMode;
    }
}
