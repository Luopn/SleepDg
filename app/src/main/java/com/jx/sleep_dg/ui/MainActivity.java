package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseActivity;
import com.jx.sleep_dg.fragment.DataFragment;
import com.jx.sleep_dg.fragment.DeviceLiftFragment;
import com.jx.sleep_dg.fragment.DeviceTempFragment;
import com.jx.sleep_dg.fragment.DeviceTrippleLiftFragment;
import com.jx.sleep_dg.fragment.DeviseHardnessFragment;
import com.jx.sleep_dg.fragment.SettingFragment;
import com.jx.sleep_dg.MyApplication;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.utils.StatusBarUtil;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends BaseActivity {

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    public static final String KEY_FRAGMENT = "key_fragment";
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView mBottomNavigation;

    private SupportFragment mFragment[] = new SupportFragment[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        BleComUtils.sendTime("F10100000001");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void bindView() {
        mDrawerLayout = findViewById(R.id.id_menu);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getHeaderView(0).findViewById(R.id.tv_user).setOnClickListener(this);

        mBottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_data:
                        showHideFragment(mFragment[0]);
                        break;
                    case R.id.action_lift:
                        showHideFragment(mFragment[1]);
                        break;
                    case R.id.action_hardness:
                        showHideFragment(mFragment[2]);
                        break;
                    case R.id.action_temp:
                        showHideFragment(mFragment[3]);
                        break;
                    case R.id.action_setting:
                        showHideFragment(mFragment[4]);
                        break;
                }
                return true;
            }
        });

        SupportFragment firstFragment = findFragment(DataFragment.class);
        if (firstFragment == null) {

            mFragment[0] = DataFragment.newInstance();
            mFragment[1] = DeviceLiftFragment.newInstance();
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_user:
                startActivity(new Intent(this, UserInfoActivity.class));
                break;
        }
    }

    public DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    public void onBackPressedSupport() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_menu);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitConfiem();
        }
    }

    private void exitConfiem() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            super.onBackPressedSupport();
            MyApplication.getInstance().extiApp();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
    }
}
