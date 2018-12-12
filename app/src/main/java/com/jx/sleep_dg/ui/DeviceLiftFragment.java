package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.base.BaseMainFragment;
import com.jx.sleep_dg.ble.BleUtils;
import com.jx.sleep_dg.protocol.BleComUtils;
import com.jx.sleep_dg.protocol.MSPProtocol;
import com.jx.sleep_dg.utils.LogUtil;
import com.jx.sleep_dg.view.BorderButton;
import com.jx.sleep_dg.view.Ruler;
import com.jx.sleep_dg.view.bar.VerticalSeekBar;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 设备升降
 */

public class DeviceLiftFragment extends BaseMainFragment implements View.OnClickListener, VerticalSeekBar.SlideChangeListener {

    private boolean isInitSeekbarVal;
    private boolean isYaolanExect;
    private MSPProtocol mspProtocol;
    private SoundPool soundPool;
    private CountDownTimer countDownTimer;

    private VerticalSeekBar seebLeftTou;
    private VerticalSeekBar seebLeftJiao;
    private Ruler rulerTou, rulerJiao;
    private ImageView ivZuoChuang;

    private BorderButton mBtnTvMode;
    private BorderButton mBtnYaolanMode;
    private BorderButton mBtnSleepMode;
    private BorderButton mBtnReadMode;
    private BorderButton mBtnYujiaMode;
    private BorderButton mBtnRelaxMode;
    private int touIndex = 0;
    private int jiaoIndex = 0;


    //各种模式切换
    private static final int MODE_NOME = 100;
    private static final int MODE_TV = 101;
    private static final int MODE_YAOLAN = 102;
    private static final int MODE_SLEEP = 103;
    private static final int MODE_READ = 104;
    private static final int MODE_YUJIA = 105;
    private static final int MODE_RELAX = 106;

    @IntDef({MODE_NOME, MODE_TV, MODE_YAOLAN, MODE_SLEEP, MODE_READ, MODE_YUJIA, MODE_RELAX})
    private @interface MODE_LIFT {
    }

    @MODE_LIFT
    private int curModeLift;//当前的升降模式


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
        ScrollView mScrollView = view.findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);

        view.findViewById(R.id.iv_tou_jia).setOnClickListener(this);
        view.findViewById(R.id.iv_tou_jian).setOnClickListener(this);
        view.findViewById(R.id.iv_jiao_jia).setOnClickListener(this);
        view.findViewById(R.id.iv_jiao_jian).setOnClickListener(this);

        rulerTou = view.findViewById(R.id.ruler_tou);
        rulerJiao = view.findViewById(R.id.ruler_jiao);

        seebLeftTou = view.findViewById(R.id.seeb_left_tou);
        seebLeftTou.setMaxProgress(30);
        seebLeftJiao = view.findViewById(R.id.seeb_left_jiao);
        seebLeftJiao.setMaxProgress(25);

        ivZuoChuang = view.findViewById(R.id.iv_zuo_chuang);

        seebLeftTou.setThumbSize(25, 25);
        seebLeftTou.setOnSlideChangeListener(this);
        seebLeftJiao.setThumbSize(25, 25);
        seebLeftJiao.setOnSlideChangeListener(this);

        mBtnTvMode = (BorderButton) view.findViewById(R.id.btn_tv_mode);
        mBtnTvMode.setOnClickListener(this);
        mBtnYaolanMode = (BorderButton) view.findViewById(R.id.btn_yaolan_mode);
        mBtnYaolanMode.setOnClickListener(this);
        mBtnSleepMode = (BorderButton) view.findViewById(R.id.btn_sleep_mode);
        mBtnSleepMode.setOnClickListener(this);
        mBtnReadMode = (BorderButton) view.findViewById(R.id.btn_read_mode);
        mBtnReadMode.setOnClickListener(this);
        mBtnYujiaMode = (BorderButton) view.findViewById(R.id.btn_yujia_mode);
        mBtnYujiaMode.setOnClickListener(this);
        mBtnRelaxMode = (BorderButton) view.findViewById(R.id.btn_relax_mode);
        mBtnRelaxMode.setOnClickListener(this);

        bindViewData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mspProtocol = MSPProtocol.getInstance();
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(_mActivity, R.raw.ding, 1);
        curModeLift = MODE_NOME;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isInitSeekbarVal = false;
    }

    //蓝牙数据
    private void bindViewData() {
        int touDevIndex = mspProtocol.getHigh2() & 0xff;
        int jiaoDevIndex = mspProtocol.getHigh1() & 0xff;
        if (mspProtocol != null) {
            rulerTou.setValue((int) Math.ceil(touDevIndex / 30.0f * 45.0f));
            rulerJiao.setValue((int) Math.ceil(jiaoDevIndex / 25.0f * 30.0f));
        }
        if(!isInitSeekbarVal){
            isInitSeekbarVal = true;
            seebLeftTou.setProgress(touDevIndex);
            seebLeftJiao.setProgress(jiaoDevIndex);
        }
        //升降模式动作
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
            case R.id.iv_tou_jia:
                if (touIndex < 30) {
                    touIndex++;
                    seebLeftTou.setProgress(touIndex);
                    LogUtil.e("左  leftTouIndex:" + touIndex);
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
                    LogUtil.e("左  leftJiaoIndex:" + touIndex);
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
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
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
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                        + BleUtils.convertDecimalToBinary(touIndex + "")
                        + BleUtils.convertDecimalToBinary(jiaoIndex + ""));

                break;
            case R.id.btn_tv_mode:
                isYaolanExect = false;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(18 + "")
                        + BleUtils.convertDecimalToBinary(9 + "")
                        + BleUtils.convertDecimalToBinary(18 + "")
                        + BleUtils.convertDecimalToBinary(9 + ""));
                break;
            case R.id.btn_yaolan_mode:
                isYaolanExect = true;
                soundPool.play(1, 1, 1, 0, 0, 1);
                countDownTimer = new CountDownTimer(5 * 60 * 1000, 10 * 1000) {
                    @Override
                    public void onTick(long l) {
                        if(!isYaolanExect)return;
                        int index = (int) (Math.random() * 17);
                        if (index < 8) index = 1;
                        if (index >= 8) index = 17;
                        BleComUtils.senddianji(BleUtils.convertDecimalToBinary(index + "")
                                + BleUtils.convertDecimalToBinary(index + "")
                                + BleUtils.convertDecimalToBinary(index + "")
                                + BleUtils.convertDecimalToBinary(index + ""));
                    }

                    @Override
                    public void onFinish() {
                        cancel();
                    }
                }.start();
                break;
            case R.id.btn_sleep_mode:
                isYaolanExect = false;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + ""));
                break;
            case R.id.btn_read_mode:
                isYaolanExect = false;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(16 + "")
                        + BleUtils.convertDecimalToBinary(9 + "")
                        + BleUtils.convertDecimalToBinary(16 + "")
                        + BleUtils.convertDecimalToBinary(9 + ""));
                break;
            case R.id.btn_yujia_mode:
                isYaolanExect = false;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(10 + "")
                        + BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(10 + ""));
                break;
            case R.id.btn_relax_mode:
                isYaolanExect = false;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(15 + "")
                        + BleUtils.convertDecimalToBinary(5 + "")
                        + BleUtils.convertDecimalToBinary(15 + "")
                        + BleUtils.convertDecimalToBinary(5 + ""));
                break;
        }
    }

    //升降模式切换
    private void swiftLiftMode(@MODE_LIFT int modeLift) {
        curModeLift = modeLift;
        switch (modeLift) {
            case MODE_YAOLAN:
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(10 + "")
                        + BleUtils.convertDecimalToBinary(mspProtocol.getHigh1() + "")
                        + BleUtils.convertDecimalToBinary(10 + "")
                        + BleUtils.convertDecimalToBinary(mspProtocol.getHigh1() + ""));
                if (mspProtocol.getHigh1() >= 12) {

                }
                break;
        }
    }

    //UI变化
    private void chang() {
        //左
        if (touIndex < 10) {
            if (jiaoIndex < 8) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot1);
            } else if (jiaoIndex < 16) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot3);

            } else if (jiaoIndex <= 25) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot4);

            }
        }
        if (10 <= touIndex && touIndex < 20) {
            if (jiaoIndex < 8) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot1);
            } else if (8 <= jiaoIndex && jiaoIndex < 16) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot3);

            } else if (16 <= jiaoIndex && jiaoIndex <= 25) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot4);

            }
        } else if (20 <= touIndex && touIndex <= 30) {
            if (jiaoIndex < 8) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot1);
            } else if (8 <= jiaoIndex && jiaoIndex < 16) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot3);

            } else if (16 <= jiaoIndex && jiaoIndex <= 25) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot4);
            }
        }
    }


    @Override
    public void onStart(VerticalSeekBar slideView, int progress) {
    }

    @Override
    public void onProgress(VerticalSeekBar slideView, int progress) {

    }

    @Override
    public void onStop(VerticalSeekBar slideView, int progress) {
        if (slideView.getId() == R.id.seeb_left_tou) {
            touIndex = progress;
            LogUtil.e("leftTouIndex:" + touIndex);
        } else if (slideView.getId() == R.id.seeb_left_jiao) {
            jiaoIndex = progress;
            LogUtil.e("leftJiaoIndex:" + jiaoIndex);

        }
        chang();
        BleComUtils.senddianji(BleUtils.convertDecimalToBinary(touIndex + "")
                + BleUtils.convertDecimalToBinary(jiaoIndex + "")
                + BleUtils.convertDecimalToBinary(touIndex + "")
                + BleUtils.convertDecimalToBinary(jiaoIndex + ""));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}
