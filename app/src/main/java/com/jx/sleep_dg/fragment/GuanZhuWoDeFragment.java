package com.jx.sleep_dg.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jx.sleep_dg.Bean.GuanzhuWodeBean;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.adapter.GuanzhuWoDeAdapter;
import com.jx.sleep_dg.ui.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注我的
 */

public class GuanZhuWoDeFragment extends BaseFragment {
    private RecyclerView recyclerView;

    private List<GuanzhuWodeBean> guanzhuWodeList = new ArrayList<>();

    private GuanzhuWoDeAdapter adapter;

    public GuanZhuWoDeFragment() {
    }

    public static GuanZhuWoDeFragment newInstance() {
        GuanZhuWoDeFragment fragment = new GuanZhuWoDeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guanzhuwode;
    }

    @Override
    public void onBindView(View view) {

        for (int i = 0; i < 5; i++) {
            GuanzhuWodeBean bean = new GuanzhuWodeBean();
            if (i % 2 == 0) {
                bean.setGuanZhu(true);
                bean.setState("1");
            } else {
                bean.setGuanZhu(false);
                bean.setState("2");

            }
            bean.setName(getResources().getString(R.string.device) + i);
            guanzhuWodeList.add(bean);
        }

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL,
                1, getActivity().getResources().getColor(R.color.white)));

        adapter = new GuanzhuWoDeAdapter(getActivity(), R.layout.recycler_item_guanzhuwode, guanzhuWodeList);
        recyclerView.setAdapter(adapter);
    }
}
