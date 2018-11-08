package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jx.sleep_dg.Bean.TabEntity;
import com.jx.sleep_dg.R;
import com.jx.sleep_dg.fragment.BaseFragment;
import com.jx.sleep_dg.fragment.GuanZhuWoDeFragment;
import com.jx.sleep_dg.fragment.WoDeGuanZhuFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 床位关联
 */

public class AssociatedActivity extends BaseActivity {
    private CommonTabLayout tablay;
    private ViewPager viewpager;

    private String[] mTitles;
    private List<BaseFragment> mFragments;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.activity_associated);
        mTitles = new String[]{getResources().getString(
                R.string.me_associated),
                getResources().getString(R.string.associated_me)
        };
        bindView();
    }

    @Override
    public void bindView() {
        setToolbarTitle(R.string.mattress_inter);
        tablay = findViewById(R.id.tablay);
        viewpager = findViewById(R.id.viewpager);
        initData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.action_do).setIcon(R.mipmap.ic_add_white);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_do:
                startActivity(new Intent(this, AddAssociatedActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initData() {

        mFragments = new ArrayList<>();
        mFragments.add(new WoDeGuanZhuFragment());
        mFragments.add(new GuanZhuWoDeFragment());

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
        viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        tablay.setTabData(mTabEntities);
        tablay.setOnTabSelectListener(new OnTabSelectListener() {
            public void onTabSelect(int position) {
                viewpager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tablay.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewpager.setCurrentItem(0);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

}
