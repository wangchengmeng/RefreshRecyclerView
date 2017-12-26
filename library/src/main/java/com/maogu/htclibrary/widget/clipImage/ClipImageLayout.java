package com.maogu.htclibrary.widget.clipImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ClipImageLayout extends RelativeLayout {
    private ClipZoomImageView   mZoomImageView;
    private ClipImageBorderView mClipImageView;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);
        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        addView(mZoomImageView, lp);
        addView(mClipImageView, lp);
    }

    public void setImageDrawable(Drawable drawable) {
        mZoomImageView.setImageDrawable(drawable);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        mZoomImageView.setBorderPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }

    public Bitmap clip() {
        return mZoomImageView.clip();
    }
}
