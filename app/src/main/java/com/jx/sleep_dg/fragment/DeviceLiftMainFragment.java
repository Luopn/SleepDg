package com.jx.sleep_dg.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jx.sleep_dg.R;

import me.yokeyword.fragmentation.SupportFragment;

public class DeviceLiftMainFragment extends SupportFragment implements View.OnClickListener {

    private SupportFragment mFragment[] = new SupportFragment[2];
    private LinearLayout llSels;
    private boolean isShowSels;
    private ImageView ivSwitch;
    private RadioButton rbLift, rbLiftTripple;

    public static DeviceLiftMainFragment newInstance() {

        Bundle args = new Bundle();
        DeviceLiftMainFragment fragment = new DeviceLiftMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_lift, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findFragment(DeviceLiftFragment.class);
        if (firstFragment == null) {

            mFragment[0] = DeviceLiftFragment.newInstance();
            mFragment[1] = DeviceLiftTrippleFragment.newInstance();

            loadMultipleRootFragment(R.id.content, 0,
                    mFragment[0],
                    mFragment[1]
            );
        } else {
            mFragment[0] = firstFragment;
            mFragment[1] = findFragment(DeviceLiftTrippleFragment.class);
        }
    }

    private void initView(View view) {
        llSels = view.findViewById(R.id.ll_sels);
        ivSwitch = view.findViewById(R.id.iv_switch);
        ivSwitch.setOnClickListener(this);
        RadioGroup rb = view.findViewById(R.id.rb_group);
        rbLift = view.findViewById(R.id.lift_sel1);
        rbLiftTripple = view.findViewById(R.id.lift_sel2);
        rbLift.setOnClickListener(this);
        rbLiftTripple.setOnClickListener(this);
    }

    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_switch:
                isShowSels = !isShowSels;
                llSels.setVisibility(isShowSels ? View.GONE : View.VISIBLE);
                break;
            case R.id.lift_sel1:
                showHideFragment(mFragment[0]);
                break;
            case R.id.lift_sel2:
                showHideFragment(mFragment[1]);
                break;
        }
    }
}
