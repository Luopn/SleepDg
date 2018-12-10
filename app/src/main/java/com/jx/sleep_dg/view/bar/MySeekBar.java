package com.jx.sleep_dg.view.bar;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.utils.CommonUtil;

import java.text.DecimalFormat;

public class MySeekBar extends View {

    private static final String TAG = "MySeekBar";

    private boolean isLeft;
    //圆环的宽度
    private float ringWidth;
    //底部的圆弧颜色
    private int ringBgCorlor;
    //滑动的圆弧颜色
    private int slideRingCorlor;
    //同心圆的外圆半径
    private float radius;
    //中间字的颜色
    //int wordCorlor;
    //中间字的大小
    //int wordSize;

    //最大进度范围
    private int maxProgress;
    //最小进度范围
    private int minProgress;
    //当前进度(总是将起始位置等分为100份)，通过进度的百分比求出实际显示数值
    private int progress;
    //实际显示的数值
    private double realShowProgress;
    //每次要增加减少的数值
    private double addOrReduce = 1;
    //开始滑动的起始位置度数，顶部270 右侧0 底部90 左侧180,因为这是半圆直接写死从左侧180开始滑动
    private int beginLocation;
    private int sweepLocation;
    //当前可滑动区域的范围
    private int slideAbleLocation;

    //圆环上的圆圈
    private Bitmap mDragBitmap;
    //圆环的宽
    private int bitmapWidth;
    //圆环的高度
    private int bitmapHight;

    //外侧刻度线的数量
    //int scaleLineCount;
    //外侧正常刻度线的长度
    //int scaleLineLength;
    //线条的宽度
    private int scaleLineWidth;
    //未选择的刻度线颜色
    private int scaleLineNormalCorlor;
    //滑动后的刻度线颜色
    private int specialScaleCorlor;
    //画底部背景环的画笔
    private Paint ringBgPaint;
    //画上面圆弧的画笔
    private Paint slideRingPaint;
    //圆环上的小圆圈
    private Paint mBitmapPaint;
    //写当前progress的画笔
    private Paint wordPaint;
    //画普通背景刻度线的画笔
    //Paint scalePaint;
    //这是画滑动后的刻度颜色画笔
    private Paint specialScalePaint;

    //显示中间显示文字(当前为progress)所占的区域
    private Rect rect;

    //这是保留小数的使用类
    private DecimalFormat df;

    public MySeekBar(Context context) {
        this(context, null);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        initPaint(context);
    }

    /**
     * 定位到指定刻度
     */
    public void setProgress(double p) {
        if (p <= maxProgress && p >= minProgress) {
            realShowProgress = p;
            progress = (int) ((realShowProgress - minProgress) * 100.0 / (maxProgress - minProgress));
        }
        invalidate();
    }

    /**
     * 点击增加progress
     */
    public void addProgress() {
        if (realShowProgress < maxProgress) {
            synchronized (MySeekBar.class) {
                realShowProgress = Double.parseDouble(df.format(realShowProgress + addOrReduce));
                progress = (int) ((realShowProgress - minProgress) * 100.0 / (maxProgress - minProgress));
            }
            invalidate();
        }
    }

    /**
     * 点击减少progress
     */
    public void reduceProgress() {
        if (realShowProgress > minProgress) {
            synchronized (MySeekBar.class) {
                realShowProgress = Double.parseDouble(df.format(realShowProgress - addOrReduce));
                progress = (int) ((realShowProgress - minProgress) * 100.0 / (maxProgress - minProgress));
            }
            invalidate();
        }
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        slideAbleLocation = CommonUtil.dip2px(context, 30);

        bitmapWidth = CommonUtil.dip2px(context, 30);
        bitmapHight = CommonUtil.dip2px(context, 30);

        mDragBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ring_dot);
        mDragBitmap = CommonUtil.conversionBitmap(mDragBitmap
                , bitmapWidth
                , bitmapHight);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.MySeekBar
                , defStyleAttr, 0);
        isLeft = array.getBoolean(R.styleable.MySeekBar_seek_left, true);
        ringWidth = array.getInt(R.styleable.MySeekBar_ringWidth, CommonUtil.dip2px(context, 10));
        slideRingCorlor = array.getInt(R.styleable.MySeekBar_slideRingCorlor, 0xFF6a6aff);
        ringBgCorlor = array.getInt(R.styleable.MySeekBar_ringBgCorlor, 0xFFbebebe);
        radius = array.getDimension(R.styleable.MySeekBar_radius, CommonUtil.dip2px(context, 100));
        // wordCorlor = array.getInt(R.styleable.MyHalfCircularSildeView_wordCorlor, Color.BLUE);
        // wordSize = array.getInt(R.styleable.MyHalfCircularSildeView_wordSize, 18);
        maxProgress = array.getInt(R.styleable.MySeekBar_maxProgress, 100);
        minProgress = array.getInt(R.styleable.MySeekBar_minProgress, 0);
        progress = array.getInt(R.styleable.MySeekBar_progress, 0);
        //因为这是个半弧，所以我们直接写死了，从左侧开始

        if (isLeft) {
            beginLocation = 110;
        } else {
            beginLocation = 290;
        }
        sweepLocation = 140;
        scaleLineNormalCorlor = array.getInt(R.styleable.MySeekBar_scaleLineNormalCorlor, 0xFFbebebe);
        specialScaleCorlor = array.getInt(R.styleable.MySeekBar_specialScaleCorlor, 0xFF6a6aff);
        scaleLineWidth = array.getInt(R.styleable.MySeekBar_scaleLineWidth, CommonUtil.dip2px(context, 2));
        //记得使用完销毁
        array.recycle();

        //保留1位小数
        df = new DecimalFormat("0");
        realShowProgress = getShowProgress(progress);
    }

    private void initPaint(Context context) {
        //画背景圆弧的画笔初始化
        ringBgPaint = new Paint();
        ringBgPaint.setColor(ringBgCorlor);
        ringBgPaint.setAntiAlias(true);// 抗锯齿效果
        ringBgPaint.setStyle(Paint.Style.STROKE);//设置空心
        ringBgPaint.setStrokeWidth(ringWidth);//线宽度，即环宽
        ringBgPaint.setStrokeCap(Paint.Cap.ROUND);//圆形笔头

        //画滑动圆弧的画笔初始化
        slideRingPaint = new Paint();
        slideRingPaint.setAntiAlias(true);
        slideRingPaint.setStyle(Paint.Style.STROKE);
        slideRingPaint.setColor(slideRingCorlor);
        slideRingPaint.setStrokeWidth(ringWidth);
        slideRingPaint.setStrokeCap(Paint.Cap.ROUND);//圆形笔头

        //写中间文字的画笔初始化
        // wordPaint = new Paint();
        // wordPaint.setColor(wordCorlor);
        // wordPaint.setTextSize(CommonUtil.sp2px(context, wordSize));
        // rect = new Rect();
        // String str = progress+" C";
        // wordPaint.getTextBounds(str, 0, str.length(),rect);

        //设置圆环上圆圈的画笔初始化
        mBitmapPaint = new Paint();
        mBitmapPaint.setDither(true);//设置防抖动
        mBitmapPaint.setFilterBitmap(true);//对Bitmap进行滤波处理
        mBitmapPaint.setAntiAlias(true);//设置抗锯齿

        //这是画滑动后的刻度线的画笔
        specialScalePaint = new Paint();
        specialScalePaint.setColor(specialScaleCorlor);
        specialScalePaint.setAntiAlias(true);
        specialScalePaint.setStyle(Paint.Style.STROKE);
        specialScalePaint.setStrokeWidth(scaleLineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthModel) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                widthSize = (int) (2 * radius + getPaddingLeft() + getPaddingRight());
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        switch (heightModel) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                //当控件自适应时候，尺寸=半径+左右边距
                heightSize = (int) (radius + getPaddingTop() + getPaddingBottom() + bitmapHight / 4);
                break;
            //当宽度全屏或者固定尺寸时候
            case MeasureSpec.EXACTLY:
                break;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isLeft) {
            //画背景圆环
            canvas.drawArc(new RectF(ringWidth / 2 + getPaddingLeft()
                            , ringWidth / 2 + getPaddingTop()
                            , 2 * radius - ringWidth / 2 + getPaddingLeft()
                            , 2 * radius - ringWidth / 2 + getPaddingTop())
                    , beginLocation, sweepLocation, false, ringBgPaint);
            //画滑动圆弧
            canvas.drawArc(new RectF(ringWidth / 2 + getPaddingLeft()
                            , ringWidth / 2 + getPaddingTop()
                            , 2 * radius - ringWidth / 2 + getPaddingLeft()
                            , 2 * radius - ringWidth / 2 + getPaddingTop())
                    , beginLocation, progress * sweepLocation / 100, false, slideRingPaint);
            //画上滑动图标
            PointF progressPoint = CommonUtil.calcArcEndPointXY(radius + getPaddingLeft()
                    , radius + getPaddingTop()
                    , radius - ringWidth / 2
                    , progress * sweepLocation / 100, beginLocation);
            int left = (int) progressPoint.x - mDragBitmap.getWidth() / 2;
            int top = (int) progressPoint.y - mDragBitmap.getHeight() / 2;
            canvas.drawBitmap(mDragBitmap, left, top, mBitmapPaint);
        } else {
            canvas.save();
            //坐标系左移
            canvas.translate(-radius, 0);
            //画背景圆环
            canvas.drawArc(new RectF(ringWidth / 2 + getPaddingLeft()
                            , ringWidth / 2 + getPaddingTop()
                            , 2 * radius - ringWidth / 2 + getPaddingLeft()
                            , 2 * radius - ringWidth / 2 + getPaddingTop())
                    , beginLocation, sweepLocation, false, slideRingPaint);
            //画滑动圆弧
            canvas.drawArc(new RectF(ringWidth / 2 + getPaddingLeft()
                            , ringWidth / 2 + getPaddingTop()
                            , 2 * radius - ringWidth / 2 + getPaddingLeft()
                            , 2 * radius - ringWidth / 2 + getPaddingTop())
                    , beginLocation, (100 - progress) * sweepLocation / 100, false, ringBgPaint);
            //画上滑动图标
            PointF progressPoint = CommonUtil.calcArcEndPointXY(radius + getPaddingLeft()
                    , radius + getPaddingTop()
                    , radius - ringWidth / 2
                    , (-progress) * sweepLocation / 100, beginLocation + sweepLocation);
            int left = (int) progressPoint.x - mDragBitmap.getWidth() / 2;
            int top = (int) progressPoint.y - mDragBitmap.getHeight() / 2;
            canvas.drawBitmap(mDragBitmap, left, top, mBitmapPaint);

            canvas.restore();
        }
        // onSeekBarItemClick(realShowProgress);
        onSeekBar.onSeekBarClick((int) realShowProgress);
        //展示进度
        // String str = (int)Math.floor(realShowProgress)+" C";//整数
        // String str = realShowProgress + " C";//小数
        // wordPaint.getTextBounds(str, 0, str.length(),rect);
        // canvas.drawText(str, radius+getPaddingLeft() + specialScaleLineLength -rect.width()/2
        //         , radius + getPaddingTop() + specialScaleLineLength , wordPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isOnRing(x, y)) {
                    updateProgress(x, y);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                updateProgress(x, y);
                return true;
            case MotionEvent.ACTION_UP:
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据当前点的位置求角度，再转换成当前进度
     */
    private void updateProgress(int eventX, int eventY) {
        double angle = Math.atan2(eventY - (radius + getPaddingTop())
                , eventX - (radius + getPaddingLeft())) / Math.PI;
        if (isLeft) {
            angle = ((2 + angle) % 2 + (-beginLocation / 180.0f)) % 2;
            if ((int) Math.round(angle * 100) >= 0 && (int) Math.round(angle * 100) <= 100) {
                progress = (int) Math.round(angle * 100);
                realShowProgress = getShowProgress(progress);
            }
        } else {

            angle = ((2 + angle) % 2 + (-beginLocation / 180.0f)) % 2;
            if ((int) Math.round(angle * 100) >= 0 && (int) Math.round(angle * 100) <= 100) {
                progress = (int) Math.round(angle * 100);
                progress = 100 - progress;
                Log.i(TAG, "updateProgress: " + progress);
                realShowProgress = getShowProgress(progress);
            }
        }
        invalidate();
    }

    /**
     * 判断当前触摸屏幕的位置是否位于咱们定的可滑动区域内
     */
    private boolean isOnRing(float eventX, float eventY) {
        Log.i(TAG, "isOnRing: eventX=" + eventX + ";eventY=" + eventY);
        boolean result = false;
        if (isLeft) {
            double distance = Math.sqrt(Math.pow(eventX - (radius + getPaddingLeft()), 2)
                    + Math.pow(eventY - (radius + getPaddingLeft()), 2));
            if (distance < (2 * radius + getPaddingLeft() + getPaddingRight())
                    && distance > radius - slideAbleLocation) {
                result = true;
            }
        } else {
            double distance = Math.sqrt(Math.pow(eventX - radius - getPaddingLeft(), 2)
                    + Math.pow(eventY - radius - getPaddingLeft(), 2));
            if (distance < (radius + getPaddingLeft() + getPaddingRight() + ringWidth)
                    && distance > radius - slideAbleLocation) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 根据progress，再求出如果首位不是0-100的时候的数字
     */
    private double getShowProgress(int progress) {
        return Double.parseDouble(df.format((maxProgress - minProgress) / 100.0 * progress + minProgress));
    }


    private onSeekBarClickListener onSeekBar = null;

    public void setSeekBarClickListener(onSeekBarClickListener onSeekBar) {
        this.onSeekBar = onSeekBar;
    }

    public interface onSeekBarClickListener {
        void onSeekBarClick(int position);
    }

}
