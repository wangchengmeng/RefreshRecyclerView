package com.maogu.htclibrary.widget.clipImage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 矩形框的试图
 *
 * @author zou.sq
 * @version 1.0
 */
public class ClipImageBorderView extends View {
    private int mHorizontalPadding;
    private int mBorderColor = Color.parseColor("#FFFFFF");
    private int mBorderWidth = 1;
    private Paint mPaint;

    public ClipImageBorderView(Context context) {
        this(context, null);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
                .getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mWidth = getWidth() - 2 * mHorizontalPadding;
        int mVerticalPadding = (getHeight() - mWidth) / 2;
        mPaint.setColor(Color.parseColor("#aa000000"));
        mPaint.setStyle(Style.FILL);
        canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
        canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(), getHeight(), mPaint);
        canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding, mVerticalPadding, mPaint);
        canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding, getWidth() - mHorizontalPadding,
                getHeight(), mPaint);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Style.STROKE);
        canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth() - mHorizontalPadding, getHeight()
                - mVerticalPadding, mPaint);
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        invalidate();
        this.mHorizontalPadding = mHorizontalPadding;
    }
}
