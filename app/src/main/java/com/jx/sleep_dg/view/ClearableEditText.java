package com.jx.sleep_dg.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.jx.sleep_dg.R;


/**
 * 带有删除按钮的EditText
 * Created by JC on 2016-06-30.
 */
@SuppressLint("AppCompatCustomView")
public class ClearableEditText extends AutoCompleteTextView implements EditText.OnFocusChangeListener {

    public static final String TAG = "ClearableEditText";

    private Drawable mClearDrawable;
    /**
     * Right Drawable 是否可见
     */
    private boolean mIsClearVisible;

    public ClearableEditText(Context context) {
        super(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        Drawable drawables[] = getCompoundDrawables();
        mClearDrawable = drawables[2]; // Right Drawable;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClearableEditText);

        int rightDrawableColor = a.getColor(R.styleable.ClearableEditText_right_drawable_color,
                Color.TRANSPARENT);

        a.recycle();

        // 给mRightDrawable上色
        if (mClearDrawable != null && !(rightDrawableColor == Color.TRANSPARENT))
            DrawableCompat.setTint(mClearDrawable, rightDrawableColor);

        setOnFocusChangeListener(this);

        // 添加TextChangedListener
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setClearDrawableVisible(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 第一次隐藏
        setClearDrawableVisible(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // error drawable 不显示 && clear drawable 显示 && action up
        if (getError() == null && mIsClearVisible && event.getAction() == MotionEvent.ACTION_UP) {

            float x = event.getX();
            if (x >= getWidth() - getTotalPaddingRight() && x <= getWidth() - getPaddingRight()) {
                clearText();

            }
        }

        return super.onTouchEvent(event);
    }


    /**
     * 清空输入框
     */
    private void clearText() {
        if (getText().length() > 0) {
            setText("");
        }
    }

    /**
     * 设置Right Drawable是否可见
     *
     * @param isVisible true for visible , false for invisible
     */
    public void setClearDrawableVisible(boolean isVisible) {
        if (mClearDrawable == null) {
            return;
        }
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                isVisible ? mClearDrawable : null, getCompoundDrawables()[3]);

        mIsClearVisible = isVisible;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        // error drawable 不显示的时候
        if (getError() == null) {
            if (hasFocus) {
                if (getText().length() > 0) {
                    setClearDrawableVisible(true);
                }
            } else {
                setClearDrawableVisible(false);
            }
        }
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        if (error != null) {
            setClearDrawableVisible(true);
        }
        super.setError(error, icon);
    }
}
