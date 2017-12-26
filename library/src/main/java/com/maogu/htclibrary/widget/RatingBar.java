package com.maogu.htclibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.maogu.htclibrary.R;
import com.maogu.htclibrary.util.DensityUtils;

public class RatingBar extends LinearLayout {

    public interface OnRatingListener {
        void onRating(View view, int RatingScore);
    }

    private boolean mClickable = true;
    private OnRatingListener mOnRatingListener;
    private int              mStarImageSize;
    private int              mStarMargin;
    private int              mTotalCount;
    private int              mStarRating;
    private Drawable         mStarEmptyDrawable;
    private Drawable         mStarFillDrawable;

    public void setClickable(boolean clickable) {
        this.mClickable = clickable;
    }

    public RatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RatingBarView);
        mStarImageSize = ta.getInteger(R.styleable.RatingBarView_starImageSize, 20);
        mStarMargin = ta.getInteger(R.styleable.RatingBarView_starPadding, 20);
        mTotalCount = ta.getInteger(R.styleable.RatingBarView_totalCount, 5);
        mStarRating = ta.getInteger(R.styleable.RatingBarView_rating, 3);
        mStarEmptyDrawable = ta.getDrawable(R.styleable.RatingBarView_starEmpty);
        mStarFillDrawable = ta.getDrawable(R.styleable.RatingBarView_starFill);
        ta.recycle();

        for (int i = 0; i < mTotalCount; ++i) {
            final ImageView imageView = getStarImageView(context, attrs);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickable) {
                        mStarRating = indexOfChild(v) + 1;
                        setStar(mStarRating);
                        if (mOnRatingListener != null) {
                            mOnRatingListener.onRating(imageView, mStarRating);
                        }
                    }
                }
            });
            addView(imageView);
        }
        setStar(mStarRating);
    }

    private ImageView getStarImageView(Context context, AttributeSet attrs) {
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(para);
        int padding = DensityUtils.getScreenW() * mStarMargin / DensityUtils.screenWidth;
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setImageDrawable(mStarFillDrawable);
        DensityUtils.measure(imageView, mStarImageSize, mStarImageSize);
        return imageView;
    }

    public void setStar(int rating) {
        rating = rating > this.mTotalCount ? this.mTotalCount : rating;
        rating = rating < 0 ? 0 : rating;
        for (int i = 0; i < mTotalCount; ++i) {
            ImageView start = (ImageView) getChildAt(i);
            if (rating < i + 1) {
                start.setImageDrawable(mStarEmptyDrawable);
            } else {
                start.setImageDrawable(mStarFillDrawable);
            }
        }
    }

    public int getRating() {
        return mStarRating;
    }

    public void setOnRatingListener(OnRatingListener onRatingListener) {
        this.mOnRatingListener = onRatingListener;
    }
}