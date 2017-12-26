package com.maogu.htclibrary.widget.tagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maogu.htclibrary.R;
import com.maogu.htclibrary.util.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class TagView extends RelativeLayout {

    private List<Tag> mTags = new ArrayList<>();

    private OnTagClickListener mClickListener;
    private int                mWidth;
    private boolean            mInitialized;

    int mTextColor;
    int mTextSize;
    int tagMargin;
    int textPaddingLeft;
    int textPaddingRight;
    int textPaddingTop;
    int texPaddingBottom;
    private Drawable mBg;

    public TagView(Context ctx) {
        super(ctx, null);
        initialize(ctx, null, 0);
    }

    public TagView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initialize(ctx, attrs, 0);
    }

    public TagView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        initialize(ctx, attrs, defStyle);
    }

    private void initialize(Context ctx, AttributeSet attrs, int defStyle) {
        ViewTreeObserver mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mInitialized) {
                    mInitialized = true;
                    drawTags();
                }
            }
        });

        // get AttributeSet
        TypedArray typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyle);
        mTextColor = typeArray.getColor(R.styleable.TagView_tagTextColor, Color.BLACK);
        mTextSize = (int) typeArray.getDimension(R.styleable.TagView_tagTextSize, 12);
        mBg = typeArray.getDrawable(R.styleable.TagView_tagBg);
        tagMargin = DensityUtils.dip2px(typeArray.getDimension(R.styleable.TagView_tagMargin, 0));
        textPaddingLeft = DensityUtils.dip2px(typeArray.getDimension(R.styleable.TagView_textPaddingLeft, 5));
        textPaddingRight = DensityUtils.dip2px(typeArray.getDimension(R.styleable.TagView_textPaddingRight, 5));
        textPaddingTop = DensityUtils.dip2px(typeArray.getDimension(R.styleable.TagView_textPaddingTop, 5));
        texPaddingBottom = DensityUtils.dip2px(typeArray.getDimension(R.styleable.TagView_textPaddingBottom, 5));
        typeArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (width <= 0)
            return;
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTags();
    }

    private void drawTags() {
        if (!mInitialized) {
            return;
        }
        removeAllViews();

        float total = getPaddingLeft() + getPaddingRight();

        int listIndex = 1;
        int index_bottom = 1;
        int index_header = 1;
        for (Tag item : mTags) {
            final int position = listIndex - 1;
            final Tag tag = item;
            View tagLayout = View.inflate(getContext(), R.layout.tagview_item, null);
            tagLayout.setId(listIndex);
            tagLayout.setBackgroundDrawable(mBg);

            TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
            tagView.setText(tag.text);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tagView.getLayoutParams();
            params.setMargins(textPaddingLeft, textPaddingTop, textPaddingRight, texPaddingBottom);
            params.gravity = Gravity.CENTER;
            tagView.setLayoutParams(params);
            tagView.setTextColor(mTextColor);
            tagView.setTextSize(mTextSize);
            tagLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onTagClick(tag, position);
                    }
                }
            });

            // calculate　of tag layout width
            float tagWidth = tagView.getPaint().measureText(tag.text) + textPaddingLeft + textPaddingRight;

            LayoutParams tagParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (mWidth <= total + tagWidth + DensityUtils.dip2px(0)) {
                tagParams.addRule(RelativeLayout.BELOW, index_bottom);
                total = getPaddingLeft() + getPaddingRight();
                index_bottom = listIndex;
                index_header = listIndex;
                tagParams.topMargin = tagMargin;
            } else {
                tagParams.addRule(RelativeLayout.ALIGN_TOP, index_header);
                if (listIndex != index_header) {
                    tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                    tagParams.leftMargin = tagMargin;
                    total += tagMargin;
                }
            }
            total += tagWidth;
            addView(tagLayout, tagParams);
            listIndex++;
        }
    }

    public void setTags(List<Tag> tags) {
        mTags.clear();
        mTags.addAll(tags);
        drawTags();
    }

    public void remove(int position) {
        mTags.remove(position);
        drawTags();
    }

    public void removeAllTags() {
        mTags.clear();
        drawTags();
    }

    public void setOnTagClickListener(OnTagClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface OnTagClickListener {
        void onTagClick(Tag tag, int position);
    }
}
