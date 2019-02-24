package com.jx.sleep_dg.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jx.sleep_dg.R;

import me.yokeyword.fragmentation.SupportFragment;

public class MainFragment extends SupportFragment {

    private BottomNavigationView mBottomNavigation;
    private SupportFragment mFragment[] = new SupportFragment[5];

    public static MainFragment newInstance() {

        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findFragment(DataFragment.class);
        if (firstFragment == null) {

            mFragment[0] = DataFragment.newInstance();
            mFragment[1] = DeviceLiftMainFragment.newInstance();
            mFragment[2] = DeviseHardnessFragment.newInstance();
            mFragment[3] = DeviceTempFragment.newInstance();
            mFragment[4] = SettingFragment.newInstance();

            loadMultipleRootFragment(R.id.content, 0,
                    mFragment[0],
                    mFragment[1],
                    mFragment[2],
                    mFragment[3],
                    mFragment[4]);
        } else {
            mFragment[0] = firstFragment;
            mFragment[1] = findFragment(DeviceLiftFragment.class);
            mFragment[2] = findFragment(DeviseHardnessFragment.class);
            mFragment[3] = findFragment(DeviceTempFragment.class);
            mFragment[4] = findFragment(SettingFragment.class);
        }
    }

    private void initView(View view) {
        mBottomNavigation = (BottomNavigationView) view.findViewById(R.id.navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                SupportFragment selectedFragment = mFragment[getSelection()];
                switch (menuItem.getItemId()) {
                    case R.id.action_data:
                        showHideFragment(mFragment[0], selectedFragment);
                        break;
                    case R.id.action_lift:
                        showHideFragment(mFragment[1], selectedFragment);
                        break;
                    case R.id.action_hardness:
                        showHideFragment(mFragment[2], selectedFragment);
                        break;
                    case R.id.action_temp:
                        showHideFragment(mFragment[3], selectedFragment);
                        break;
                    case R.id.action_setting:
                        showHideFragment(mFragment[4], selectedFragment);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }

    private int getSelection() {
        int selectionIndex = 0;
        Menu menu = mBottomNavigation.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).isChecked()) {
                selectionIndex = i;
            }
        }
        return selectionIndex;
    }
}
