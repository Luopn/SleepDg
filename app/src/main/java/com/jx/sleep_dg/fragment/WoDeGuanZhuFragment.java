package com.jx.sleep_dg.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jx.sleep_dg.Bean.WodeGuanzhuBean;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.adapter.WoDeGuanzhuAdapter;
import com.jx.sleep_dg.ui.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的关注
 */

public class WoDeGuanZhuFragment extends BaseFragment {
    private RecyclerView recyclerView;

    private WoDeGuanzhuAdapter adapter;
    private List<WodeGuanzhuBean> wodeGuanzhuList = new ArrayList<>();

    public WoDeGuanZhuFragment() {
    }

    public static WoDeGuanZhuFragment newInstance() {
        WoDeGuanZhuFragment fragment = new WoDeGuanZhuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wodeguanlian;
    }

    @Override
    public void onBindView(View view) {
        for (int i = 0; i < 5; i++) {
            WodeGuanzhuBean bean = new WodeGuanzhuBean();
            if (i % 2 == 0) {
                bean.setGuanZhu(true);
                bean.setState("1");
            } else {
                bean.setGuanZhu(false);
                bean.setState("2");

            }
            bean.setName(getResources().getString(R.string.device) + i);
            wodeGuanzhuList.add(bean);
        }
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL,
                1, getActivity().getResources().getColor(R.color.white)));
        adapter = new WoDeGuanzhuAdapter(getActivity(), R.layout.recycler_item_wodeguanzhu, wodeGuanzhuList);
        recyclerView.setAdapter(adapter);
    }
}
