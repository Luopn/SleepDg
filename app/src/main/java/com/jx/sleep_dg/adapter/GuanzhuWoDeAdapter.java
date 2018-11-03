package com.jx.sleep_dg.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jx.sleep_dg.Bean.GuanzhuWodeBean;
import com.jx.sleep_dg.Bean.WodeGuanzhuBean;
import com.jx.sleep_dg.R;

import java.util.List;

public class GuanzhuWoDeAdapter extends BaseQuickAdapter<GuanzhuWodeBean, BaseViewHolder> {
    Context context;

    public GuanzhuWoDeAdapter(Context context, int layoutResId, @Nullable List<GuanzhuWodeBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, GuanzhuWodeBean item) {
        ImageView imageView = helper.getView(R.id.iv_user_image);
        ImageView IvSrate = helper.getView(R.id.iv_state);
        ImageView ivChakan = helper.getView(R.id.iv_chakan);

        helper.setText(R.id.tv_nickname, item.getName());
        if (item.getState().equals("1")) {
            helper.setText(R.id.tv_state, R.string.using);
            helper.setTextColor(R.id.tv_state, context.getResources().getColor(R.color.green));
            IvSrate.setBackgroundResource(R.mipmap.green);

        } else {
            helper.setText(R.id.tv_state, R.string.unused);
            helper.setTextColor(R.id.tv_state, context.getResources().getColor(R.color.grey));
            IvSrate.setBackgroundResource(R.mipmap.grey);

        }
        if (item.isGuanZhu()) {
            helper.setText(R.id.tv_guanlian, R.string.associated_together);
            ivChakan.setBackgroundResource(R.mipmap.ic_eyes);
        } else {
            helper.setText(R.id.tv_guanlian, R.string.associated);
            ivChakan.setBackgroundResource(R.mipmap.ic_noeyes);


        }

    }
}
