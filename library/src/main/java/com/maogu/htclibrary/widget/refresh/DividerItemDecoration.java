package com.maogu.htclibrary.widget.refresh;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.maogu.htclibrary.R;
import com.maogu.htclibrary.util.DensityUtils;

/**
 * @author kai.wang
 * recycleview的item 间隔封装类
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint = new Paint();
    private int mHeaderCount;
    private int mColor;
    private int mSpaceLeft;
    private int mSpaceTop;
    private int mSpaceRight;
    private int mSpaceBottom;

    public DividerItemDecoration(Activity activity) {
        mColor = ContextCompat.getColor(activity, R.color.divider_line);
        mSpaceLeft = 2;
        mSpaceTop = 2;
        mSpaceRight = 2;
        mSpaceBottom = 2;
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public DividerItemDecoration(Activity activity, int color, int spaceLeft, int spaceTop, int spaceRight, int spaceBottom) {
        this(activity, 0, color, spaceLeft, spaceTop, spaceRight, spaceBottom);
    }

    private DividerItemDecoration(Activity activity, int headerCount, int color, int spaceLeft, int spaceTop, int spaceRight, int spaceBottom) {
        this.mHeaderCount = headerCount;
        this.mSpaceLeft = DensityUtils.getMeasureValue(spaceLeft);
        this.mSpaceTop = DensityUtils.getMeasureValue(spaceTop);
        this.mSpaceRight = DensityUtils.getMeasureValue(spaceRight);
        this.mSpaceBottom = DensityUtils.getMeasureValue(spaceBottom);
        this.mColor = ContextCompat.getColor(activity, color);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            drawGridItem(c, parent, (GridLayoutManager) layoutManager);
        } else if (layoutManager instanceof LinearLayoutManager) {
            drawLinearItem(c, parent, (LinearLayoutManager) layoutManager);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            drawStaggeredItem(c, parent, (StaggeredGridLayoutManager) layoutManager);
        }
    }

    private void drawGridItem(Canvas canvas, RecyclerView parent, GridLayoutManager layoutManager) {
        int spanCount = layoutManager.getSpanCount();

        for (int i = 0; i < layoutManager.getItemCount(); i++) {
            View child = parent.getChildAt(i);
            if (child == null) {
                return;
            }
            int childAdapterPosition = parent.getChildAdapterPosition(child);
            int index = childAdapterPosition % spanCount;

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left;
            int top;
            int right;
            int bottom;
            if (index < spanCount - 1) {
                left = child.getRight() + layoutParams.rightMargin;
                right = left + mSpaceRight;
                top = child.getTop() + layoutParams.topMargin;
                bottom = child.getBottom() + layoutParams.bottomMargin;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
            if (childAdapterPosition < layoutManager.getItemCount() - spanCount) {
                left = child.getLeft() + layoutParams.leftMargin;
                top = child.getBottom() + layoutParams.bottomMargin;
                right = index == spanCount - 1 ? (child.getRight() + layoutParams.rightMargin)
                        : (child.getRight() + layoutParams.rightMargin + mSpaceRight);
                bottom = top + mSpaceBottom;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    private void drawLinearItem(Canvas canvas, RecyclerView parent, LinearLayoutManager layoutManager) {
        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            for (int i = mHeaderCount; i < layoutManager.getItemCount(); i++) {
                View child = parent.getChildAt(i);
                if (child == null) {
                    return;
                }
                int left;
                int top;
                int right;
                int bottom;
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

                left = child.getLeft() - layoutParams.leftMargin;
                top = child.getTop() - mSpaceTop - layoutParams.topMargin;
                right = child.getRight() + layoutParams.rightMargin;
                bottom = top + mSpaceTop;
                canvas.drawRect(left, top, right, bottom, mPaint);
                if (i == layoutManager.getItemCount() - mHeaderCount - 1) {
                    left = child.getLeft() - layoutParams.leftMargin;
                    top = child.getBottom() + layoutParams.bottomMargin;
                    right = child.getRight() + layoutParams.rightMargin;
                    bottom = top + mSpaceBottom;
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        } else {
            for (int i = mHeaderCount; i < layoutManager.getItemCount(); i++) {
                View child = parent.getChildAt(i);
                if (child == null) {
                    return;
                }
                int left;
                int top;
                int right;
                int bottom;

                if (i == layoutManager.getItemCount() - mHeaderCount - 1) {
                    left = child.getRight();
                    top = child.getTop();
                    right = left + mSpaceRight;
                    bottom = child.getBottom();
                    canvas.drawRect(left, top, right, bottom, mPaint);
                } else {
                    left = child.getLeft() - mSpaceLeft;
                    top = child.getTop();
                    right = left + mSpaceLeft;
                    bottom = child.getBottom();
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    private void drawStaggeredItem(Canvas c, RecyclerView parent, StaggeredGridLayoutManager layoutManager) {
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            layoutGridItem(outRect, view, parent, (GridLayoutManager) layoutManager);
        } else if (layoutManager instanceof LinearLayoutManager) {
            layoutLinearItem(outRect, view, parent, (LinearLayoutManager) layoutManager);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            layoutStaggeredItem(outRect, view, parent, (StaggeredGridLayoutManager) layoutManager);
        }
    }

    private void layoutGridItem(Rect outRect, View view, RecyclerView parent, GridLayoutManager layoutManager) {
        int spanCount = layoutManager.getSpanCount();
        int childAdapterPosition = parent.getChildAdapterPosition(view);

        int index = childAdapterPosition % spanCount;
        if (index == 0) {
            outRect.left = 0;
            outRect.right = mSpaceRight / 2;
        } else if (index < spanCount - 1) {
            outRect.left = mSpaceLeft / 2;
            outRect.right = mSpaceRight / 2;
        } else {
            outRect.left = mSpaceLeft / 2;
            outRect.right = 0;
        }

        if (childAdapterPosition < spanCount) {
            outRect.top = 0;
            outRect.bottom = mSpaceBottom / 2;
        } else if (childAdapterPosition < layoutManager.getItemCount() - spanCount) {
            outRect.top = mSpaceTop / 2;
            outRect.bottom = mSpaceBottom / 2;
        } else {
            outRect.top = mSpaceTop / 2;
            outRect.bottom = 0;
        }
    }

    private void layoutLinearItem(Rect outRect, View view, RecyclerView parent, LinearLayoutManager layoutManager) {
        int position = parent.getChildLayoutPosition(view);

        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            if (position == mHeaderCount) {
                outRect.top = mSpaceTop;
                outRect.bottom = mSpaceBottom / 2;
            } else if (position == layoutManager.getItemCount() - mHeaderCount - 1) {
                outRect.top = mSpaceTop / 2;
                outRect.bottom = mSpaceBottom;
            } else {
                outRect.top = mSpaceTop / 2;
                outRect.bottom = mSpaceBottom / 2;
            }

            outRect.left = mSpaceLeft;
            outRect.right = mSpaceRight;

        } else {
            outRect.top = mSpaceTop;
            outRect.bottom = mSpaceBottom;
            if (position == mHeaderCount) {
                outRect.left = mSpaceLeft;
                outRect.right = mSpaceRight / 2;
            } else if (position == layoutManager.getItemCount() - mHeaderCount - 1) {
                outRect.left = mSpaceLeft / 2;
                outRect.right = mSpaceRight;
            } else {
                outRect.left = mSpaceLeft / 2;
                outRect.right = mSpaceRight / 2;
            }
        }
    }


    private void layoutStaggeredItem(Rect outRect, View view, RecyclerView parent, StaggeredGridLayoutManager layoutManager) {

    }

}
