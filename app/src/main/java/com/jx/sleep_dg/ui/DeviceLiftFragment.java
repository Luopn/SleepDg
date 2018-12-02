package com.jx.sleep_dg.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    private MSPProtocol mspProtocol;
    private SoundPool soundPool;
    private CountDownTimer countDownTimer;

    private VerticalSeekBar seebLeftTou;
    private VerticalSeekBar seebLeftJiao;
    private Ruler tvTou, tvJiao;
    private ImageView ivZuoChuang;

    private int touIndex = 0;
    private int jiaoIndex = 0;

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

        tvTou = view.findViewById(R.id.ruler_tou);
        tvJiao = view.findViewById(R.id.ruler_jiao);

        seebLeftTou = view.findViewById(R.id.seeb_left_tou);
        seebLeftJiao = view.findViewById(R.id.seeb_left_jiao);

        ivZuoChuang = view.findViewById(R.id.iv_zuo_chuang);

        seebLeftTou.setThumbSizePx(56, 56);
        seebLeftTou.setOnSlideChangeListener(this);
        seebLeftJiao.setThumbSizePx(56, 56);
        seebLeftJiao.setOnSlideChangeListener(this);

        BorderButton mBtnTvMode = (BorderButton) view.findViewById(R.id.btn_tv_mode);
        mBtnTvMode.setOnClickListener(this);
        BorderButton mBtnYaolanMode = (BorderButton) view.findViewById(R.id.btn_yaolan_mode);
        mBtnYaolanMode.setOnClickListener(this);
        BorderButton mBtnSleepMode = (BorderButton) view.findViewById(R.id.btn_sleep_mode);
        mBtnSleepMode.setOnClickListener(this);
        BorderButton mBtnReadMode = (BorderButton) view.findViewById(R.id.btn_read_mode);
        mBtnReadMode.setOnClickListener(this);
        BorderButton mBtnYujiaMode = (BorderButton) view.findViewById(R.id.btn_yujia_mode);
        mBtnYujiaMode.setOnClickListener(this);
        BorderButton mBtnRelaxMode = (BorderButton) view.findViewById(R.id.btn_relax_mode);
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

    private void bindViewData() {
        if (mspProtocol != null) {
            tvTou.setValue((int) Math.ceil((mspProtocol.getHigh2() & 0xff) / 17.0f * 45.0f));
            tvJiao.setValue((int) Math.ceil((mspProtocol.getHigh1() & 0xff) / 17.0f * 30.0f));
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
        switch (view.getId()) {
            case R.id.iv_tou_jia:
                if (touIndex < 17) {
                    touIndex++;
                    seebLeftTou.setProgress(touIndex);
                    LogUtil.e("左  leftTouIndex:" + touIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
            case R.id.iv_tou_jian:
                if (touIndex > 0) {
                    touIndex--;
                    seebLeftTou.setProgress(touIndex);
                    LogUtil.e("左  leftJiaoIndex:" + touIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
            case R.id.iv_jiao_jia:
                //左
                if (jiaoIndex < 17) {
                    jiaoIndex++;
                    seebLeftJiao.setProgress(jiaoIndex);
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
            case R.id.iv_jiao_jian:
                //左
                if (jiaoIndex > 0) {
                    jiaoIndex--;
                    seebLeftJiao.setProgress(jiaoIndex);
                    LogUtil.e("左  leftJiaoIndex:" + jiaoIndex);
                }
                chang();
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                        + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

                break;
            case R.id.btn_tv_mode:
                if(countDownTimer != null){
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(9 + "")
                        + BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(9 + ""));
                break;
            case R.id.btn_yaolan_mode:
                soundPool.play(1, 1, 1, 0, 0, 1);
                countDownTimer = new CountDownTimer(5 * 60 * 1000, 10 * 1000) {
                    @Override
                    public void onTick(long l) {
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
                if(countDownTimer != null){
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + "")
                        + BleUtils.convertDecimalToBinary(1 + ""));
                break;
            case R.id.btn_read_mode:
                if(countDownTimer != null){
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(16 + "")
                        + BleUtils.convertDecimalToBinary(9 + "")
                        + BleUtils.convertDecimalToBinary(16 + "")
                        + BleUtils.convertDecimalToBinary(9 + ""));
                break;
            case R.id.btn_yujia_mode:
                if(countDownTimer != null){
                    countDownTimer.cancel();
                }
                soundPool.play(1, 1, 1, 0, 0, 1);
                BleComUtils.senddianji(BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(10 + "")
                        + BleUtils.convertDecimalToBinary(17 + "")
                        + BleUtils.convertDecimalToBinary(10 + ""));
                break;
            case R.id.btn_relax_mode:
                if(countDownTimer != null){
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

    public void chang() {
        //左
        if (touIndex < 6) {
            if (jiaoIndex < 6) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot1);
            } else if (jiaoIndex < 12) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot3);

            } else if (jiaoIndex <= 17) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head1_foot4);

            }
        }
        if (6 <= touIndex && touIndex < 12) {
            if (jiaoIndex < 6) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot1);
            } else if (6 <= jiaoIndex && jiaoIndex < 12) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot3);

            } else if (12 <= jiaoIndex && jiaoIndex <= 17) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head3_foot4);

            }
        } else if (12 <= touIndex && touIndex <= 17) {
            if (jiaoIndex < 6) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot1);
            } else if (6 <= jiaoIndex && jiaoIndex < 12) {
                ivZuoChuang.setBackgroundResource(R.mipmap.head4_foot3);

            } else if (12 <= jiaoIndex && jiaoIndex <= 17) {
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
        BleComUtils.senddianji(BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((touIndex + 1) + "")
                + BleUtils.convertDecimalToBinary((jiaoIndex + 1) + ""));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}
