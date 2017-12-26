package com.maogu.htclibrary.widget.clipImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ClipZoomImageView extends ImageView implements OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    public static float   SCALE_MAX    = 4.0f;
    private final float[] matrixValues = new float[9];
    private final Matrix  mScaleMatrix = new Matrix();
    private       boolean once         = true;
    private float   mLastX;
    private float   mLastY;
    private boolean isCanDrag;
    private int     lastPointerCount;
    private int     mHorizontalPadding;
    private int     mBorderPadding;

    public ClipZoomImageView(Context context) {
        this(context, null);
    }

    public ClipZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(this);
        setBackgroundColor(Color.WHITE);
    }

    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = 0, y = 0;
        final int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        RectF rectF = getMatrixRectF();
                        if (rectF.width() <= getWidth() - mBorderPadding * 2) {
                            dx = 0;
                        }
                        if (rectF.height() <= getHeight() - getHVerticalPadding() * 2) {
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorder();

                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
            default:
                break;
        }
        setImageMatrix(mScaleMatrix);
        return true;
    }

    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            int width = getWidth();
            int height = getHeight();
            int drawableW = d.getIntrinsicWidth();
            int drawableH = d.getIntrinsicHeight();
            float scale = 1.0f;

            int frameSize = getWidth() - mHorizontalPadding * 2;
            if (drawableW > frameSize && drawableH < frameSize) {
                scale = 1.0f * frameSize / drawableH;
            } else if (drawableH > frameSize && drawableW < frameSize) {
                scale = 1.0f * frameSize / drawableW;
            } else if (drawableW > frameSize && drawableH > frameSize) {
                float scaleW = frameSize * 1.0f / drawableW;
                float scaleH = frameSize * 1.0f / drawableH;
                scale = Math.max(scaleW, scaleH);
            }
            if (drawableW < frameSize && drawableH > frameSize) {
                scale = 1.0f * frameSize / drawableW;
            } else if (drawableH < frameSize && drawableW > frameSize) {
                scale = 1.0f * frameSize / drawableH;
            } else if (drawableW < frameSize && drawableH < frameSize) {
                float scaleW = 1.0f * frameSize / drawableW;
                float scaleH = 1.0f * frameSize / drawableH;
                scale = Math.max(scaleW, scaleH);
            }
            SCALE_MAX = scale * 4;
            mScaleMatrix.postTranslate((width - drawableW) / 2f, (height - drawableH) / 2f);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2f, getHeight() / 2f);
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    /**
     * 剪切图片，返回剪切后的bitmap对象
     *
     * @return Bitmap bitmap对象
     */
    public Bitmap clip() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return Bitmap.createBitmap(bitmap, mBorderPadding, getHVerticalPadding(), getWidth() - 2
                * mBorderPadding, getWidth() - 2 * mBorderPadding);
    }

    private void checkBorder() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        if (rect.width() + 0.01 >= width - 2 * mBorderPadding) {
            if (rect.left > mBorderPadding) {
                deltaX = -rect.left + mBorderPadding;
            }
            if (rect.right < width - mBorderPadding) {
                deltaX = width - mBorderPadding - rect.right;
            }
        }
        if (rect.height() + 0.01 >= height - 2 * getHVerticalPadding()) {
            if (rect.top > getHVerticalPadding()) {
                deltaY = -rect.top + getHVerticalPadding();
            }

            if (rect.bottom < height - getHVerticalPadding()) {
                deltaY = height - getHVerticalPadding() - rect.bottom;
            }
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= 0;
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 设置裁剪时的间距
     *
     * @param borderPadding 裁剪时的间距
     */
    public void setBorderPadding(int borderPadding) {
        this.mBorderPadding = borderPadding;
    }

    private int getHVerticalPadding() {
        return (getHeight() - (getWidth() - 2 * mBorderPadding)) / 2;
    }
}
