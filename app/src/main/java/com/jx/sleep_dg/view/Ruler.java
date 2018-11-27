package com.jx.sleep_dg.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.jx.sleep_dg.R;

public class Ruler extends View {
    //间隔，即两条刻度之间的距离
    private int interval;
    //起始值
    private int fromValue;
    //结束值
    private int toValue;
    //每两个值之间的间隔数,也指多少个最小单位，比如0cm到1cm有10个最小单位1mm
    private int intervalsBetweenValues;
    //相邻两个值的跳跃间隔，如上面第一张图的10000到11000，中间的跳跃值就是1000
    private int valuesInterval;
    //当前值
    private int currentValue;
    //值的文本大小
    private int valuesTextSize;
    //值的文本颜色
    private int valuesTextColor;
    //刻度的宽度
    private int linesWidth;
    //刻度的颜色
    private int linesColor;
    //刻度尺是vertical还是horizontal,上面第一张图的就是horizontal
    private int orientation;

    private Paint paint;

    private OnValueChangeListener listener;

    private int currentPosition;
    private int textHeight;
    private int oldX;
    private int oldY;

    public Ruler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Ruler);

        fromValue = array.getInt(R.styleable.Ruler_fromValue, 0);
        toValue = array.getInt(R.styleable.Ruler_toValue, intervalsBetweenValues);
        currentValue = array.getInt(R.styleable.Ruler_currentValue, (fromValue + toValue) / 2);
        intervalsBetweenValues = array.getInt(R.styleable.Ruler_intervalsBetweenValues, intervalsBetweenValues);
        valuesInterval = array.getInt(R.styleable.Ruler_valuesInterval, 1);
        valuesTextSize = array.getDimensionPixelSize(R.styleable.Ruler_valuesTextSize, sp2px(16));
        valuesTextColor = array.getColor(R.styleable.Ruler_valuesTextColor, Color.BLACK);
        linesWidth = array.getDimensionPixelSize(R.styleable.Ruler_linesWidth, dp2px(1));
        linesColor = array.getColor(R.styleable.Ruler_linesColor, Color.BLACK);
        array.recycle();

        paint = new Paint();
        paint.setTextSize(valuesTextSize);

        //文本高度
        FontMetrics fm = paint.getFontMetrics();
        textHeight = (int) (fm.bottom - fm.top);

        //当前所指的刻度位置，即中间红色指针指向的值
        currentPosition = currentValue / valuesInterval * intervalsBetweenValues;
    }

    public Ruler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public Ruler(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画中间的指针,就在中间画，很简单
        paint.setColor(Color.parseColor("#9f64ae"));
        paint.setStrokeWidth(dp2px(4));
        int height = getHeight() - textHeight / 2;
        interval = (height-textHeight/2) / (toValue - fromValue);

        canvas.drawLine(getWidth(), height-currentValue*interval - textHeight / 4,
                getWidth() * 2 / 5, height-currentValue*interval - textHeight / 4, paint);
        paint.setColor(linesColor);
        paint.setStrokeWidth(linesWidth);

        int position = 0;

        //循环画刻度，当画到上边界或起始值时则退出循环，去画下半部分刻度
        while (true) {
            //intervalsBetweenValues/2是指两个相邻值之间距离的中间那条稍微长一点的刻度的位置
            if (position % intervalsBetweenValues == 0) {
                //当刻度值的位置为刻度值旁边的时候则画长一点，并在旁边画上数字，否则就按普通刻度长度画
                if (position % intervalsBetweenValues == 0) {
                    canvas.drawLine(getWidth(), height - textHeight / 4, getWidth() / 2, height - textHeight / 4, paint);

                    String valueString = Integer.toString(position / intervalsBetweenValues * valuesInterval);
                    paint.setColor(valuesTextColor);
                    canvas.drawText(valueString, getWidth() / 2 - paint.measureText(valueString) - dp2px(5), height, paint);
                    paint.setColor(linesColor);
                } else {
                    canvas.drawLine(getWidth(), height - textHeight / 4, getWidth() * 3 / 5, height - textHeight / 4, paint);
                }
            } else {
                canvas.drawLine(getWidth(), height - textHeight / 4, getWidth() * 4 / 5, height - textHeight / 4, paint);
            }

            //每画完一条刻度则递减position和height,当position=起始值，或height低于0，即超出边界时，退出循环
            position++;
            if (position > toValue / valuesInterval * intervalsBetweenValues) break;
            height -= interval;
            if (height < 0 - 2 * textHeight) break;
        }
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public void setValue(int value) {
        currentPosition = value / valuesInterval * intervalsBetweenValues;
        invalidate();
    }

    public void setFromValue(int fromValue) {
        this.fromValue = fromValue;
        invalidate();
    }

    public void setToValue(int toValue) {
        this.toValue = toValue;
        invalidate();
    }

    public interface OnValueChangeListener {
        void onValueChange(int value);
    }
}