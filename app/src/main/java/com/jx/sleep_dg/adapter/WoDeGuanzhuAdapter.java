package com.jx.sleep_dg.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jx.sleep_dg.Bean.WodeGuanzhuBean;
import com.jx.sleep_dg.R;

import java.util.List;

public class WoDeGuanzhuAdapter extends BaseQuickAdapter<WodeGuanzhuBean, BaseViewHolder> {
    Context context;

    public WoDeGuanzhuAdapter(Context context, int layoutResId, @Nullable List<WodeGuanzhuBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, WodeGuanzhuBean item) {
        ImageView imageView = helper.getView(R.id.iv_user_image);
        ImageView IvSrate = helper.getView(R.id.iv_state);
        helper.setText(R.id.tv_nickname, item.getName());
        if (item.getState().equals("1")) {
            helper.setText(R.id.tv_state, "正在使用");
            helper.setTextColor(R.id.tv_state, context.getResources().getColor(R.color.green));
            IvSrate.setBackgroundResource(R.mipmap.green);
        } else {
            helper.setText(R.id.tv_state, "未使用");
            helper.setTextColor(R.id.tv_state, context.getResources().getColor(R.color.grey));
            IvSrate.setBackgroundResource(R.mipmap.grey);

        }
        if (item.isGuanZhu()) {
            helper.setText(R.id.tv_guanlian, "互相关联");
        } else {
            helper.setText(R.id.tv_guanlian, "已关联");

        }

    }
}
