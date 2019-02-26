package com.jx.sleep_dg.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.ui.SearchActivity;
import com.jx.sleep_dg.ui.UserInfoActivity;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.BorderButton;
import com.jx.sleep_dg.view.Ruler;
import com.jx.sleep_dg.view.bar.VerticalSeekBar;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 设备升降
 */
public class DeviceLiftTrippleFragment extends BaseMainFragment implements View.OnClickListener, VerticalSeekBar.SlideChangeListener {

    private static final String TAG = "DeviceTrippleLiftFragme";

    private static final int MAX_TOU = 15;
    private static final int MAX_JIAO = 25;
    private static final int LONG_PRESS_DELAY = 300;//ms

    private boolean isInitSeekbarVal;
    private MSPProtocol mspProtocol;
    private SoundPool soundPool;

    private AnimationDrawable animationDrawableL, animationDrawableR, animationDrawableC;
    private CountDownTimer countDownTimer;

    private VerticalSeekBar seebLeftTou;
    private VerticalSeekBar seebJiao;
    private VerticalSeekBar seebRightTou;
    private Ruler rulerLTou, rulerRTou, rulerJiao;
    private ImageView ivChuang;
    private ImageView ivAllJia,ivAllJian;

    private Runnable jiaRunnable,jianRunnable;

    private BorderButton mBtnTvMode;
    private BorderButton mBtnSleepMode;
    private BorderButton mBtnReadMode;
    private BorderButton mBtnYujiaMode;
    private BorderButton mBtnRelaxMode;
    private int touLIndex = 0;
    private int touRIndex = 0;
    private int jiaoIndex = 0;

    public static DeviceLiftTrippleFragment newInstance() {
        Bundle args = new Bundle();
        DeviceLiftTrippleFragment fragment = new DeviceLiftTrippleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_lift_tripple;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindView(View view) {
        ScrollView mScrollView = view.findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);

        view.findViewById(R.id.iv_tou_jia_l).setOnClickListener(this);
        view.findViewById(R.id.iv_tou_jian_l).setOnClickListener(this);
        view.findViewById(R.id.iv_tou_jia_r).setOnClickListener(this);
        view.findViewById(R.id.iv_tou_jian_r).setOnClickListener(this);
        view.findViewById(R.id.iv_jiao_jia).setOnClickListener(this);
        view.findViewById(R.id.iv_jiao_jian).setOnClickListener(this);
        ivAllJia = view.findViewById(R.id.iv_all_jia);
        ivAllJian = view.findViewById(R.id.iv_all_jian);
        //长按加
        ivAllJia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        allUp();
                        ivAllJia.postDelayed(jiaRunnable = new Runnable() {
                            @Override
                            public void run() {
                                allUp();
                                ivAllJia.postDelayed(this,LONG_PRESS_DELAY);
                            }
                        },LONG_PRESS_DELAY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(jiaRunnable != null){
                            ivAllJia.removeCallbacks(jiaRunnable);
                        }
                        onAll();
                        sendCMD();
                        break;
                }
                return true;
            }
        });
        //长按减
        ivAllJian.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        allDown();
                        ivAllJian.postDelayed(jianRunnable = new Runnable() {
                            @Override
                            public void run() {
                                allDown();
                                ivAllJian.postDelayed(this,LONG_PRESS_DELAY);
                            }
                        },LONG_PRESS_DELAY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(jianRunnable != null){
                            ivAllJian.removeCallbacks(jianRunnable);
                        }
                        onAll();
                        sendCMD();
                        break;
                }
                return true;
            }
        });

        rulerLTou = view.findViewById(R.id.ruler_tou_l);
        rulerRTou = view.findViewById(R.id.ruler_tou_r);
        rulerJiao = view.findViewById(R.id.ruler_jiao);

        seebLeftTou = view.findViewById(R.id.seeb_tou_l);
        seebLeftTou.setMaxProgress(MAX_TOU);
        seebRightTou = view.findViewById(R.id.seeb_tou_r);
        seebRightTou.setMaxProgress(MAX_TOU);
        seebJiao = view.findViewById(R.id.seeb_jiao);
        seebJiao.setMaxProgress(MAX_JIAO);

        ivChuang = view.findViewById(R.id.iv_chuang);

        seebLeftTou.setThumb(R.mipmap.ic_head_purple);
        seebLeftTou.setThumbSize(25, 25);
        seebLeftTou.setOnSlideChangeListener(this);
        seebRightTou.setThumb(R.mipmap.ic_head_purple_r);
        seebRightTou.setThumbSize(25, 25);
        seebRightTou.setOnSlideChangeListener(this);
        seebJiao.setThumb(R.mipmap.ic_foot_purple);
        seebJiao.setThumbSize(25, 25);
        seebJiao.setOnSlideChangeListener(this);

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
        if (hidden) {
            ivChuang.setImageResource(R.mipmap.ic_lift_0);
        }
    }

    //蓝牙数据
    private void bindViewData() {
        final int touLDevIndex = mspProtocol.getHigh1() & 0xff;
        final int touRDevIndex = mspProtocol.getHigh2() & 0xff;
        final int jiaoDevIndex = mspProtocol.getHigh4() & 0xff;
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mspProtocol != null) {
                    rulerLTou.setCurrentValue((int) Math.ceil((float) touLDevIndex / MAX_TOU * 45.0f));
                    rulerRTou.setCurrentValue((int) Math.ceil((float) touRDevIndex / MAX_TOU * 45.0f));
                    rulerJiao.setCurrentValue((int) Math.ceil((float) jiaoDevIndex / MAX_JIAO * 30.0f));
                }
                if (!isInitSeekbarVal) {
                    isInitSeekbarVal = true;
                    seebLeftTou.setProgress(touLIndex = touLDevIndex);
                    seebRightTou.setProgress(touRIndex = touRDevIndex);
                    seebJiao.setProgress(jiaoIndex = jiaoDevIndex);
                }
            }
        });
    }

    @Override
    protected void notifyBleDataChanged(Intent intent) {
        super.notifyBleDataChanged(intent);
        bindViewData();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_user_image:
                startActivity(new Intent(_mActivity, UserInfoActivity.class));
                break;
            case R.id.iv_ble:
                Intent intent = new Intent();
                intent.setClass(_mActivity, SearchActivity.class);
                _mActivity.startActivity(intent);
                break;
            case R.id.iv_tou_jia_l:
                if (touLIndex < MAX_TOU) {
                    touLIndex++;
                    seebLeftTou.setProgress(touLIndex);
                    LogUtil.e("左  leftTouIndex:" + touLIndex);
                }
                onLTou();
                sendCMD();

                break;
            case R.id.iv_tou_jian_l:
                if (touLIndex > 0) {
                    touLIndex--;
                    seebLeftTou.setProgress(touLIndex);
                    LogUtil.e("左  leftJiaoIndex:" + touLIndex);
                }
                onLTou();
                sendCMD();

                break;
            case R.id.iv_tou_jia_r:
                if (touRIndex < MAX_TOU) {
                    touRIndex++;
                    seebRightTou.setProgress(touRIndex);
                    LogUtil.e("右  rightTouIndex:" + touRIndex);
                }
                onRTou();
                sendCMD();

                break;
            case R.id.iv_tou_jian_r:
                if (touRIndex > 0) {
                    touRIndex--;
                    seebRightTou.setProgress(touRIndex);
                    LogUtil.e("右  rightTouIndex:" + touRIndex);
                }
                onRTou();
                sendCMD();

                break;
            case R.id.iv_jiao_jia:
                //左
                if (jiaoIndex < MAX_JIAO) {
                    jiaoIndex++;
                    seebJiao.setProgress(jiaoIndex);
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
                }
                onJiao();
                sendCMD();

                break;
            case R.id.iv_jiao_jian:
                //左
                if (jiaoIndex > 0) {
                    jiaoIndex--;
                    seebJiao.setProgress(jiaoIndex);
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
                }
                onJiao();
                sendCMD();

                break;
            case R.id.iv_all_jia:
                //同时升
                allUp();
                sendCMD();

                break;
            case R.id.iv_all_jian:
                //同时降
                allDown();
                sendCMD();

                break;
            case R.id.btn_tv_mode:
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(18 + "")
                        + BleUtils.convertDecimalToBinary(9 + "")
                        + BleUtils.convertDecimalToBinary(18 + "")
                        + BleUtils.convertDecimalToBinary(9 + ""));
                break;
            case R.id.btn_sleep_mode:
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + ""));
                break;
            case R.id.btn_read_mode:
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(16 + "")
                        + BleUtils.convertDecimalToBinary(9 + "")
                        + BleUtils.convertDecimalToBinary(16 + "")
                        + BleUtils.convertDecimalToBinary(9 + ""));
                break;
            case R.id.btn_yujia_mode:
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(10 + "")
                        + BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(10 + ""));
                break;
            case R.id.btn_relax_mode:
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(15 + "")
                        + BleUtils.convertDecimalToBinary(5 + "")
                        + BleUtils.convertDecimalToBinary(15 + "")
                        + BleUtils.convertDecimalToBinary(5 + ""));
                break;
        }
    }

    //同时升
    private void allUp() {
        if (touLIndex < MAX_TOU) {
            touLIndex++;
            touRIndex = touLIndex;
            jiaoIndex = (int) ((float) touLIndex / MAX_TOU * MAX_JIAO);
            seebJiao.setProgress(jiaoIndex);
            seebRightTou.setProgress(touRIndex);
            seebLeftTou.setProgress(touLIndex);
        }
    }

    //同时降
    private void allDown() {
        if (touLIndex > 0) {
            touLIndex--;
            touRIndex = touLIndex;
            jiaoIndex = (int) ((float) touLIndex / MAX_TOU * MAX_JIAO);
            seebJiao.setProgress(jiaoIndex);
            seebRightTou.setProgress(touRIndex);
            seebLeftTou.setProgress(touLIndex);
        }
    }

    //发送指令
    private void sendCMD() {
        BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touLIndex + "")
                + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                + BleUtils.convertDecimalToBinary(touRIndex + "")
                + BleUtils.convertDecimalToBinary(jiaoIndex + ""));
    }

    //左边头高度变化
    private void onLTou() {
        ivChuang.setImageResource(R.drawable.anim_lift_headl);
        animationDrawableL = (AnimationDrawable) ivChuang.getDrawable();
        animationDrawableL.start();
        stopShan();
    }

    //右边头高度变化
    private void onRTou() {
        ivChuang.setImageResource(R.drawable.anim_lift_headr);
        animationDrawableR = (AnimationDrawable) ivChuang.getDrawable();
        animationDrawableR.start();
        stopShan();
    }

    //脚高度变化
    private void onJiao() {
        ivChuang.setImageResource(R.drawable.anim_lift_headc);
        animationDrawableC = (AnimationDrawable) ivChuang.getDrawable();
        animationDrawableC.start();
        stopShan();
    }

    //同时变化
    private void onAll() {
        ivChuang.setImageResource(R.drawable.anim_lift_all);
        animationDrawableC = (AnimationDrawable) ivChuang.getDrawable();
        animationDrawableC.start();
        stopShan();
    }

    //一段时间后停止闪烁
    private void stopShan() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                ivChuang.setImageResource(R.mipmap.ic_lift_0);
                countDownTimer.cancel();
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onStart(VerticalSeekBar slideView, int progress) {
    }

    @Override
    public void onProgress(VerticalSeekBar slideView, int progress) {
    }

    @Override
    public void onStop(VerticalSeekBar slideView, int progress) {
        if (slideView.getId() == R.id.seeb_tou_l) {
            touLIndex = progress;
            onLTou();
        } else if (slideView.getId() == R.id.seeb_tou_r) {
            touRIndex = progress;
            onRTou();
        } else if (slideView.getId() == R.id.seeb_jiao) {
            jiaoIndex = progress;
            onJiao();
        }
        sendCMD();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}
