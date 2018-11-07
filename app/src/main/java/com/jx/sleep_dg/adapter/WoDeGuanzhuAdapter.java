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
            helper.setText(R.id.tv_state, R.string.using);
            helper.setTextColor(R.id.tv_state, context.getResources().getColor(R.color.green));
            IvSrate.setBackgroundResource(R.mipmap.ic_circle_green);
        } else {
            helper.setText(R.id.tv_state, R.string.unused);
            helper.setTextColor(R.id.tv_state, context.getResources().getColor(R.color.grey));
            IvSrate.setBackgroundResource(R.mipmap.ic_circle_grey);

        }
        if (item.isGuanZhu()) {
            helper.setText(R.id.tv_guanlian, R.string.associated_together);
        } else {
            helper.setText(R.id.tv_guanlian, R.string.associated);

        }

    }
}
