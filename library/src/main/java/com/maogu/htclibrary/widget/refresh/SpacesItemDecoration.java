package com.maogu.htclibrary.widget.refresh;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpaceLeft;
    private int mSpaceTop;
    private int mSpaceRight;
    private int mSpaceBottom;
    private int mHeaderCount;
    private boolean mIsWrapContent;

    public SpacesItemDecoration(int spaceValue) {
        this.mSpaceLeft = spaceValue;
        this.mSpaceTop = spaceValue;
        this.mSpaceRight = spaceValue;
        this.mSpaceBottom = spaceValue;
    }

    public SpacesItemDecoration(int left, int top, int right, int bottom) {
        this(left, top, right, bottom, false);
    }

    public SpacesItemDecoration(int left, int top, int right, int bottom, boolean isWrapContent) {
        this.mSpaceLeft = left;
        this.mSpaceTop = top;
        this.mSpaceRight = right;
        this.mSpaceBottom = bottom;
        this.mIsWrapContent = isWrapContent;
    }

    public void hasHeader(int headerCount) {
        mHeaderCount = headerCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int position = parent.getChildLayoutPosition(view);
        if (position < mHeaderCount) {
            return;
        }
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            position = mHeaderCount > 0 ? position - mHeaderCount : position;
            if (position < spanCount) {
                outRect.top = mSpaceTop;
            }
            if (mIsWrapContent) {
                outRect.bottom = (position / spanCount < (gridLayoutManager.getItemCount() - 1) / spanCount) ? 0 : mSpaceBottom;
            } else {
                outRect.bottom = mSpaceBottom;
            }
            int index = position % spanCount;
            if (0 == index) {
                outRect.left = mSpaceLeft;
                outRect.right = mSpaceRight / 2;
            } else if (spanCount - 1 == index) {
                outRect.left = mSpaceLeft / 2;
                outRect.right = mSpaceRight;
            } else {
                outRect.left = mSpaceLeft / 2;
                outRect.right = mSpaceRight / 2;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (LinearLayoutManager.VERTICAL == linearLayoutManager.getOrientation()) {

                outRect.left = mSpaceLeft;
                outRect.right = mSpaceRight;
                if (position == mHeaderCount) {
                    outRect.top = mIsWrapContent ? 0 : mSpaceTop;
                    outRect.bottom = mSpaceBottom / 2;
                } else if (position == layoutManager.getItemCount() - 1) {
                    outRect.top = mSpaceTop / 2;
                    outRect.bottom = mIsWrapContent ? 0 : mSpaceBottom;
                } else {
                    outRect.top = mSpaceTop / 2;
                    outRect.bottom = mSpaceBottom / 2;
                }
            } else {
                outRect.top = mSpaceTop;
                outRect.bottom = mSpaceBottom;
                if (0 == position) {
                    outRect.left = mSpaceLeft;
                    outRect.right = mSpaceRight / 2;
                } else if (layoutManager.getItemCount() - 1 == position) {
                    outRect.left = mSpaceLeft / 2;
                    outRect.right = mSpaceRight;
                } else {
                    outRect.left = mSpaceLeft / 2;
                    outRect.right = mSpaceRight / 2;
                }
            }
        }
    }
}