package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.fragment.ControlFragment;
import com.jx.sleep_dg.fragment.DataFragment;
import com.jx.sleep_dg.fragment.SettingFragment;
import com.jx.sleep_dg.fragment.ShopFragment;
import com.jx.sleep_dg.fragment.StatisticsFragment;
import com.jx.sleep_dg.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NewBaseActivity {

    public static final String KEY_FRAGMENT = "key_fragment";
    public static DrawerLayout mDrawerLayout;
    private FragmentTabHost mTabHost;
    private ViewPager mViewPager;

    private List<Fragment> mFragmentList;
    private Class mClass[] = {DataFragment.class, ControlFragment.class, StatisticsFragment.class, ShopFragment.class, SettingFragment.class};
    private Fragment mFragment[] = {new DataFragment(), new ControlFragment(), new StatisticsFragment(), new ShopFragment(), new SettingFragment()};
    private String[] mTitles;
    private int mImages[] = {
            R.drawable.tab_data,
            R.drawable.tab_control,
            R.drawable.tab_home,
            R.drawable.tab_shop, R.drawable.tab_set
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitles = new String[]{
                getResources().getString(R.string.data),
                getResources().getString(R.string.control),
                getResources().getString(R.string.statistics),
                getResources().getString(R.string.mall),
                getResources().getString(R.string.set)
        };
        bindView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void initView() {

        mDrawerLayout = findViewById(R.id.id_menu);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getHeaderView(0).findViewById(R.id.tv_user).setOnClickListener(this);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mFragmentList = new ArrayList<Fragment>();

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i < mFragment.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));
            mTabHost.addTab(tabSpec, mClass[i], null);
            mFragmentList.add(mFragment[i]);
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
    }

    @Override
    public void bindView() {
        initView();
        initEvent();
    }

    private View getTabView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);

        image.setImageResource(mImages[index]);
        title.setText(mTitles[index]);

        return view;
    }

    private void initEvent() {

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mViewPager.setCurrentItem(mTabHost.getCurrentTab());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_user:
                startActivity(new Intent(this, UserInfoActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressedSupport() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_menu);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressedSupport();
            MyApplication.getInstance().extiApp();
        }
    }
}
