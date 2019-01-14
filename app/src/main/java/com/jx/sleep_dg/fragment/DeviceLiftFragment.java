package com.jx.sleep_dg.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.DeviceNetConfigAcyivity;
import com.jx.sleep_dg.ui.MainQmActivity;
import com.jx.sleep_dg.ui.SearchActivity;
import com.jx.sleep_dg.ui.StatisticsActivity;
import com.jx.sleep_dg.ui.UserInfoActivity;
import com.jx.sleep_dg.utils.CommonUtil;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.QMUIDeviceHelper;
import com.jx.sleep_dg.view.BorderButton;
import com.jx.sleep_dg.view.Ruler;
import com.jx.sleep_dg.view.bar.VerticalSeekBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 设备升降
 */
public class DeviceLiftFragment extends BaseMainFragment implements View.OnClickListener, VerticalSeekBar.SlideChangeListener {

    private boolean isInitSeekbarVal;
    private MSPProtocol mspProtocol;
    private SoundPool soundPool;

    private static final int MAX_TOU = 20;
    private static final int MAX_JIAO = 25;

    private ImageView ivUserImage, ivRight, ivSwitch;
    private RadioGroup rb_switch;
    private PopupWindow switchPOp;
    private LinearLayout llBedContainer;
    private VerticalSeekBar seebLeftTou;
    private VerticalSeekBar seebLeftJiao;
    private Ruler rulerTou, rulerJiao;
    private ImageView ivTouChuang, icWeiChuang;

    private BorderButton mBtnTvMode;
    private BorderButton mBtnSleepMode;
    private BorderButton mBtnReadMode;
    private BorderButton mBtnYujiaMode;
    private BorderButton mBtnRelaxMode;
    private ArrayList<BorderButton> smartBtns = new ArrayList<>();
    private Runnable mExceedRunnable;

    private int touDevIndex, touIndex = 0;
    private int jiaoDevIndex, jiaoIndex = 0;

    private static final int MODE_NONE = 1000;
    private static final int MODE_TV = 1001;
    private static final int MODE_READ = 1002;
    private static final int MODE_SLEEP = 1003;
    private static final int MODE_RELAX = 1004;
    private static final int MODE_YOGA = 1005;

    @IntDef({MODE_NONE, MODE_TV, MODE_READ, MODE_SLEEP, MODE_RELAX, MODE_YOGA})
    @Retention(RetentionPolicy.SOURCE)
    @interface MODES {
    }

    @MODES
    private int curMode = MODE_NONE;

    public static DeviceLiftFragment newInstance() {
        Bundle args = new Bundle();
        DeviceLiftFragment fragment = new DeviceLiftFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_lift;
    }

    @Override
    public void onBindView(View view) {
        ivUserImage = view.findViewById(R.id.iv_user_image);
        ivRight = view.findViewById(R.id.iv_right);
        ivSwitch = view.findViewById(R.id.iv_switch);
        ivUserImage.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        ivSwitch.setOnClickListener(this);
        if (!_mActivity.getApplication().getApplicationInfo().packageName.equals(Constance.QM)) {
            ivUserImage.setVisibility(View.INVISIBLE);
            ivRight.setVisibility(View.INVISIBLE);
        }

        ScrollView mScrollView = view.findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);

        view.findViewById(R.id.iv_tou_jia).setOnClickListener(this);
        view.findViewById(R.id.iv_tou_jian).setOnClickListener(this);
        view.findViewById(R.id.iv_jiao_jia).setOnClickListener(this);
        view.findViewById(R.id.iv_jiao_jian).setOnClickListener(this);

        llBedContainer = view.findViewById(R.id.ll_bed_container);
        rulerTou = view.findViewById(R.id.ruler_tou);
        rulerJiao = view.findViewById(R.id.ruler_jiao);

        seebLeftTou = view.findViewById(R.id.seeb_left_tou);
        seebLeftTou.setMaxProgress(MAX_TOU);
        seebLeftJiao = view.findViewById(R.id.seeb_left_jiao);
        seebLeftJiao.setMaxProgress(MAX_JIAO);

        ivTouChuang = view.findViewById(R.id.iv_tou_chuang);
        icWeiChuang = view.findViewById(R.id.iv_wei_chuang);
        ivTouChuang.setBackgroundResource(R.mipmap.ic_head0);
        icWeiChuang.setBackgroundResource(R.mipmap.ic_foot0);

        seebLeftTou.setThumbSize(25, 25);
        seebLeftTou.setOnSlideChangeListener(this);
        seebLeftJiao.setThumbSize(25, 25);
        seebLeftJiao.setOnSlideChangeListener(this);

        mBtnTvMode = view.findViewById(R.id.btn_tv_mode);
        mBtnTvMode.setOnClickListener(this);
        mBtnSleepMode = view.findViewById(R.id.btn_sleep_mode);
        mBtnSleepMode.setOnClickListener(this);
        mBtnReadMode = view.findViewById(R.id.btn_read_mode);
        mBtnReadMode.setOnClickListener(this);
        mBtnYujiaMode = view.findViewById(R.id.btn_yujia_mode);
        mBtnYujiaMode.setOnClickListener(this);
        mBtnRelaxMode = view.findViewById(R.id.btn_relax_mode);
        mBtnRelaxMode.setOnClickListener(this);

        smartBtns.add(mBtnTvMode);
        smartBtns.add(mBtnSleepMode);
        smartBtns.add(mBtnReadMode);
        smartBtns.add(mBtnYujiaMode);
        smartBtns.add(mBtnRelaxMode);

        //根据高度最高的床图，设置高度
        Bitmap maxHeightBed = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_head5);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llBedContainer.getLayoutParams();
        params.height = maxHeightBed.getHeight();
        llBedContainer.setLayoutParams(params);

        bindViewData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mspProtocol = MSPProtocol.getInstance();
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(_mActivity, R.raw.ding, 1);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isInitSeekbarVal = false;
    }

    //蓝牙数据
    private void bindViewData() {
        touDevIndex = mspProtocol.getHigh2() & 0xff;
        jiaoDevIndex = mspProtocol.getHigh4() & 0xff;
        if (mspProtocol != null) {
            rulerTou.setCurrentValue((int) Math.ceil((float) touDevIndex / MAX_TOU * 45.0f));
            rulerJiao.setCurrentValue((int) Math.ceil((float) jiaoDevIndex / MAX_JIAO * 30.0f));
        }
        if (!isInitSeekbarVal) {
            isInitSeekbarVal = true;
            seebLeftTou.setProgress(touIndex = touDevIndex);
            seebLeftJiao.setProgress(jiaoIndex = jiaoDevIndex);
            chang();
        }
        //升降模式动作
        switch (curMode) {
            case MODE_TV:
                //继续发送脚部升降
                if (touDevIndex == (MAX_TOU * 15 / 20)) {
                    BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO * 8 / 20 + "")
                            + BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO * 8 / 20 + ""));
                    curMode = MODE_NONE;
                }
                break;
            case MODE_READ:
                if (touDevIndex == (MAX_TOU * 13 / 20)) {
                    BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO * 8 / 20 + "")
                            + BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO * 8 / 20 + ""));
                    curMode = MODE_NONE;
                }
                break;
            case MODE_SLEEP:
                if (touDevIndex == 0) {
                    BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(0 + "")
                            + BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(0 + ""));
                    curMode = MODE_NONE;
                }
                break;
            case MODE_RELAX:
                if (touDevIndex == (MAX_TOU * 10 / 20)) {
                    BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO * 10 / 20 + "")
                            + BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO * 10 / 20 + ""));
                    curMode = MODE_NONE;
                }
                break;
            case MODE_YOGA:
                if (touDevIndex == MAX_TOU) {
                    BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO + "")
                            + BleUtils.convertDecimalToBinary(touDevIndex + "")
                            + BleUtils.convertDecimalToBinary(MAX_JIAO + ""));
                    curMode = MODE_NONE;
                }
                break;
            case MODE_NONE:
                break;
        }
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        curMode = MODE_NONE;
        switch (view.getId()) {
            case R.id.iv_user_image:
                startActivity(new Intent(_mActivity, UserInfoActivity.class));
                break;
            case R.id.iv_right:
                Intent intent = new Intent();
                intent.setClass(_mActivity, SearchActivity.class);
                _mActivity.startActivity(intent);
                break;
            case R.id.iv_switch: {
                View contentV = LayoutInflater.from(_mActivity).inflate(R.layout.layout_bed_lift_switch, null);
                contentV.findViewById(R.id.lift_switch).setOnClickListener(this);
                rb_switch = contentV.findViewById(R.id.rb_group);
                if (switchPOp != null) {
                    switchPOp.dismiss();
                    switchPOp = null;
                }
                switchPOp = new PopupWindow(contentV, -2, -2, true);
                switchPOp.showAsDropDown(ivSwitch);
            }
            break;
            case R.id.lift_switch:
                switch (rb_switch.getCheckedRadioButtonId()) {
                    case R.id.lift_sel1:
                        break;
                    case R.id.lift_sel2:
                        start(DeviceTrippleLiftFragment.newInstance());
                        break;
                    case R.id.lift_sel3:
                        break;
                }
                if (switchPOp != null) {
                    switchPOp.dismiss();
                    switchPOp = null;
                }
                break;
            case R.id.iv_tou_jia:
                if (touIndex < 30) {
                    touIndex++;
                    seebLeftTou.setProgress(touIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                        + BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + ""));

                break;
            case R.id.iv_tou_jian:
                if (touIndex > 0) {
                    touIndex--;
                    seebLeftTou.setProgress(touIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                        + BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + ""));

                break;
            case R.id.iv_jiao_jia:
                //左
                if (jiaoIndex < 25) {
                    jiaoIndex++;
                    seebLeftJiao.setProgress(jiaoIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                        + BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + ""));

                break;
            case R.id.iv_jiao_jian:
                //左
                if (jiaoIndex > 0) {
                    jiaoIndex--;
                    seebLeftJiao.setProgress(jiaoIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                        + BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + ""));

                break;

            //=========一键智能==================================================================
            case R.id.btn_tv_mode:
                playSound();
                curMode = MODE_TV;
                selectModeBtns(mBtnTvMode);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(MAX_TOU * 15 / 20 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + "")
                        + BleUtils.convertDecimalToBinary(MAX_TOU * 15 / 20 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + ""));
                break;
            case R.id.btn_sleep_mode:
                playSound();
                curMode = MODE_SLEEP;
                selectModeBtns(mBtnSleepMode);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(0 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + "")
                        + BleUtils.convertDecimalToBinary(0 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + ""));
                break;
            case R.id.btn_read_mode:
                playSound();
                curMode = MODE_READ;
                selectModeBtns(mBtnReadMode);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(MAX_TOU * 13 / 20 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + "")
                        + BleUtils.convertDecimalToBinary(MAX_TOU * 13 / 20 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + ""));
                break;
            case R.id.btn_yujia_mode:
                playSound();
                curMode = MODE_YOGA;
                selectModeBtns(mBtnYujiaMode);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(MAX_TOU + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + "")
                        + BleUtils.convertDecimalToBinary(MAX_TOU + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + ""));
                break;
            case R.id.btn_relax_mode:
                playSound();
                curMode = MODE_RELAX;
                selectModeBtns(mBtnRelaxMode);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(MAX_TOU * 10 / 20 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + "")
                        + BleUtils.convertDecimalToBinary(MAX_TOU * 10 / 20 + "")
                        + BleUtils.convertDecimalToBinary(jiaoDevIndex + ""));
                break;
        }
    }

    //播放声音
    private void playSound() {
        soundPool.play(1, 1, 1, 0, 0, 1);
    }

    //UI变化
    private void chang() {
        if (touIndex == 0) {
            ivTouChuang.setBackgroundResource(R.mipmap.ic_head0);
        } else if (touIndex > 0 && touIndex <= 6) {
            ivTouChuang.setBackgroundResource(R.mipmap.ic_head1);
        } else if (touIndex > 6 && touIndex <= 12) {
            ivTouChuang.setBackgroundResource(R.mipmap.ic_head2);
        } else if (touIndex > 12 && touIndex <= 18) {
            ivTouChuang.setBackgroundResource(R.mipmap.ic_head3);
        } else if (touIndex > 18 && touIndex <= 24) {
            ivTouChuang.setBackgroundResource(R.mipmap.ic_head4);
        } else if (touIndex > 24 && touIndex <= 30) {
            ivTouChuang.setBackgroundResource(R.mipmap.ic_head5);
        }
        if (jiaoIndex == 0) {
            icWeiChuang.setBackgroundResource(R.mipmap.ic_foot0);
        } else if (jiaoIndex > 0 && jiaoIndex <= 5) {
            icWeiChuang.setBackgroundResource(R.mipmap.ic_foot1);
        } else if (jiaoIndex > 5 && jiaoIndex <= 10) {
            icWeiChuang.setBackgroundResource(R.mipmap.ic_foot2);
        } else if (jiaoIndex > 10 && jiaoIndex <= 15) {
            icWeiChuang.setBackgroundResource(R.mipmap.ic_foot3);
        } else if (jiaoIndex > 15 && jiaoIndex <= 20) {
            icWeiChuang.setBackgroundResource(R.mipmap.ic_foot4);
        } else if (jiaoIndex > 20 && jiaoIndex <= 25) {
            icWeiChuang.setBackgroundResource(R.mipmap.ic_foot5);
        }
    }

    @Override
    public void onStart(VerticalSeekBar slideView, int progress) {
    }

    @Override
    public void onProgress(VerticalSeekBar slideView, int progress) {
        if (slideView.getId() == R.id.seeb_left_tou) {
            touIndex = progress;
        } else if (slideView.getId() == R.id.seeb_left_jiao) {
            jiaoIndex = progress;
        }
        chang();
    }

    @Override
    public void onStop(VerticalSeekBar slideView, int progress) {
        curMode = MODE_NONE;

        if (slideView.getId() == R.id.seeb_left_tou) {
            touIndex = progress;
        } else if (slideView.getId() == R.id.seeb_left_jiao) {
            jiaoIndex = progress;

        }
        BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touIndex + "")
                + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                + BleUtils.convertDecimalToBinary(touIndex + "")
                + BleUtils.convertDecimalToBinary(jiaoIndex + ""));
    }

    /**
     * 执行指令时，其他按钮不选
     *
     * @param curBtn 当前按钮
     */
    private void selectModeBtns(BorderButton curBtn) {
        if (curBtn != null) {
            for (BorderButton btn : smartBtns) {
                if (btn != curBtn) {
                    btn.setSelected(false);
                }
            }
        } else {
            for (BorderButton btn : smartBtns) {
                btn.setSelected(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}
