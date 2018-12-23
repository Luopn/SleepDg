package com.jx.sleep_dg.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private MSPProtocol mspProtocol;
    private SoundPool soundPool;

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

        llBedContainer = view.findViewById(R.id.ll_bed_container);
        rulerTou = view.findViewById(R.id.ruler_tou);
        rulerJiao = view.findViewById(R.id.ruler_jiao);

        seebLeftTou = view.findViewById(R.id.seeb_left_tou);
        seebLeftTou.setMaxProgress(30);
        seebLeftJiao = view.findViewById(R.id.seeb_left_jiao);
        seebLeftJiao.setMaxProgress(25);

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
        int touDevIndex = mspProtocol.getHigh2() & 0xff;
        int jiaoDevIndex = mspProtocol.getHigh1() & 0xff;
        if (mspProtocol != null) {
            rulerTou.setValue((int) Math.ceil(touDevIndex / 30.0f * 45.0f));
            rulerJiao.setValue((int) Math.ceil(jiaoDevIndex / 25.0f * 30.0f));
        }
        if (!isInitSeekbarVal) {
            isInitSeekbarVal = true;
            seebLeftTou.setProgress(touIndex = touDevIndex);
            seebLeftJiao.setProgress(jiaoIndex = jiaoDevIndex);
            chang();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}
