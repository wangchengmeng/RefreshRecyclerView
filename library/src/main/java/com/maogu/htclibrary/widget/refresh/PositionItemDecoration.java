package com.maogu.htclibrary.widget.refresh;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.maogu.htclibrary.util.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class PositionItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpaceLeft;
    private int mSpaceTop;
    private int mSpaceRight;
    private int mSpaceBottom;
    private int mHeaderCount;

    private List<Integer> mExceptionPositions = new ArrayList<>();

    public PositionItemDecoration(int spaceValue) {
        this.mSpaceLeft = spaceValue;
        this.mSpaceTop = spaceValue;
        this.mSpaceRight = spaceValue;
        this.mSpaceBottom = spaceValue;
    }

    public PositionItemDecoration(int left, int top, int right, int bottom, int... exceptionPositions) {
        this.mSpaceLeft = left;
        this.mSpaceTop = top;
        this.mSpaceRight = right;
        this.mSpaceBottom = bottom;

        for (int exceptionPosition : exceptionPositions) {
            mExceptionPositions.add(exceptionPosition);
        }
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
            int count = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
            outRect.bottom = mSpaceBottom;
            position = mHeaderCount > 0 ? position - mHeaderCount : position;
            int index = position % count;
            if (position < count) {
                outRect.top = mSpaceTop;
            }
            if (0 == index) {
                outRect.left = mSpaceLeft;
                outRect.right = mSpaceRight / 2;
            } else if (count - 1 == index) {
                outRect.left = mSpaceLeft / 2;
                outRect.right = mSpaceRight;
            } else {
                outRect.left = mSpaceLeft / 2;
                outRect.right = mSpaceRight / 2;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            int screenW = DensityUtils.getScreenW();
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (LinearLayoutManager.VERTICAL == linearLayoutManager.getOrientation()) {

                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                int measuredWidth = layoutParams.width == RecyclerView.LayoutParams.MATCH_PARENT ? screenW : layoutParams.width;
                outRect.left = mSpaceLeft == 0 ? (screenW - measuredWidth) / 2 : mSpaceLeft;
                outRect.right = mSpaceRight == 0 ? (screenW - measuredWidth) / 2 : mSpaceRight;
                if (position == mHeaderCount) {
                    outRect.top = isExceptionPosition(position) ? 0 : mSpaceTop;
                    outRect.bottom = mSpaceBottom / 2;
                } else if (position == layoutManager.getItemCount() - 1) {
                    outRect.top = mSpaceTop / 2;
                    outRect.bottom = isExceptionPosition(position) ? 0 : mSpaceBottom;
                } else {
                    outRect.top = isExceptionPosition(position) ? 0 : mSpaceTop / 2;
                    outRect.bottom = isExceptionPosition(position) ? 0 : mSpaceBottom / 2;
                }
            } else {
                outRect.top = mSpaceTop;
                outRect.bottom = mSpaceBottom;
                if (0 == position) {
                    outRect.left = isExceptionPosition(position) ? 0 : mSpaceLeft;
                    outRect.right = mSpaceRight / 2;
                } else if (layoutManager.getItemCount() - 1 == position) {
                    outRect.left = mSpaceLeft / 2;
                    outRect.right = isExceptionPosition(position) ? 0 : mSpaceRight;
                } else {
                    outRect.left = isExceptionPosition(position) ? 0 : mSpaceLeft / 2;
                    outRect.right = isExceptionPosition(position) ? 0 : mSpaceRight / 2;
                }
            }
        }
    }

    private boolean isExceptionPosition(int position) {
        return mExceptionPositions.contains(position);
    }
}