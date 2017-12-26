package com.maogu.htclibrary.widget.refresh;


import android.content.Context;
import android.support.v4.view.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.OverScroller;

/**
 * @author : kai.wang
 *         整体交互的父布局
 */
public class HeaderScrollLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    private static final String TAG = "HeaderScrollGroup";
    private       OverScroller                mScroller;
    private final NestedScrollingParentHelper mParentHelper;
    private final NestedScrollingChildHelper  mChildHelper;
    private       VelocityTracker             mVelocityTracker;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    private       boolean mIsNeedIntercept;
    private       boolean mIsBeingDragged;
    private       float   mLastTouchY;
    private       float   mLastTouchX;
    private       float   mDy;
    private       float   mDx;
    private final int     mMinFlingVelocity;
    private final int     mMaxFlingVelocity;
    private static final int S_FLING_DURATION = 2000;
    private static final int S_RESET_DURATION = 1000;
    private ChildScrollListener mChildScrollListener;
    private View                mChildScrollView;

    public void setChildScrollListener(ChildScrollListener mChildScrollListener) {
        this.mChildScrollListener = mChildScrollListener;
    }

    public void setChildScrollView(final View view) {
        mChildScrollView = view;
        setChildScrollListener(new ChildScrollListener() {
            @Override
            public boolean isOnTop() {
                return mChildScrollView != null && !mChildScrollView.canScrollVertically(-1);
            }

            @Override
            public boolean isOnBottom() {
                return mChildScrollView != null && !mChildScrollView.canScrollVertically(1);
            }
        });
    }

    public HeaderScrollLayout(Context context) {
        this(context, null);
    }

    public HeaderScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScroller = new OverScroller(context);
        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = getPaddingTop();
        int right;
        int bottom;
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            View childView = getChildAt(childIndex);

            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();

            left = left + layoutParams.leftMargin + getPaddingLeft();
            right = left + childView.getMeasuredWidth();
            top += layoutParams.topMargin;
            bottom = top + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);
            top = bottom + layoutParams.bottomMargin;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        dealMultiTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsBeingDragged) {
                    onTouchEvent(event);
                } else {
                    mIsNeedIntercept = false;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                mIsNeedIntercept = isNeedIntercept();

                if (mIsNeedIntercept && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    MotionEvent obtain = MotionEvent.obtain(event);
                    obtain.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(event);
                    return dispatchTouchEvent(obtain);
                }
                break;

            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }


    private void dealMultiTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        if (pointerIndex < 0) {
            return;
        }
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = event.getX(pointerIndex);
                mLastTouchY = event.getY(pointerIndex);
                mActivePointerId = event.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId != mActivePointerId) {
                    mLastTouchX = event.getX(pointerIndex);
                    mLastTouchY = event.getY(pointerIndex);
                    mActivePointerId = event.getPointerId(pointerIndex);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int pointerIndex1 = event.findPointerIndex(mActivePointerId);
                float moveX = event.getX(pointerIndex1);
                float moveY = event.getY(pointerIndex1);
                mDx = moveX - mLastTouchX;
                mDy = moveY - mLastTouchY;
                mLastTouchX = moveX;
                mLastTouchY = moveY;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerId = event.getPointerId(pointerIndex);
                if (mActivePointerId == pointerId) {
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsNeedIntercept;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // 如果滚动未结束时按下，则停止滚动
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "==========ACTION_MOVE===========");
                if (mIsNeedIntercept) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                    if (getScrollY() - mDy <= 0) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        mDy = getScrollY();
                    } else if (getScrollY() - mDy >= getScrollRange()) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        mDy = getScrollY() - getScrollRange();
                    }
                    scrollBy(0, -(int) mDy);
                } else {
                    if (mDy != 0) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        mIsBeingDragged = false;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(S_FLING_DURATION, mMaxFlingVelocity);
                float yVelocity = mVelocityTracker.getYVelocity();
                Log.d(TAG, "==========yVelocity===========" + yVelocity);
                if (yVelocity > mMinFlingVelocity) {
                    //自动向上动画
                    mScroller.fling(0, getScrollY(),
                            0, -(int) (yVelocity * 0.6f),
                            0, 0,
                            0, getScrollRange());
                    postInvalidate();
                } else if (yVelocity < -mMinFlingVelocity) {
                    //自动向下动画
                    mScroller.fling(0, getScrollY(),
                            0, -(int) (yVelocity * 0.6f),
                            0, 0,
                            0, getScrollRange());
                    postInvalidate();
                }

                mIsBeingDragged = false;

                // 回收速度监控器
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                // 回收速度监控器
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;

            default:
                break;
        }
        return true;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }


    private boolean isNeedIntercept() {
        boolean isChildOnTop = null != mChildScrollListener && mChildScrollListener.isOnTop();

        if (!isChildOnTop && getScrollY() > 0) {
            mIsNeedIntercept = false;
        } else if ((getScrollY() == 0 || getScrollY() < getScrollRange()) && mDy < 0) {
            mIsNeedIntercept = Math.abs(mDx) < Math.abs(mDy);
        } else if ((getScrollY() > 0 || getScrollY() == getScrollRange()) && mDy > 0) {
            mIsNeedIntercept = Math.abs(mDx) < Math.abs(mDy);
        } else {
            mIsNeedIntercept = false;
        }

        Log.d(TAG, "mIsNeedIntercept=" + mIsNeedIntercept);
        return mIsNeedIntercept;
    }

    private int getScrollRange() {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            return child.getMeasuredHeight();
        } else {
            return 0;
        }
    }


    @Override
    //Negative to check scrolling up, positive to check scrolling down.
    public boolean canScrollVertically(int direction) {
        boolean isChildOnTop = null != mChildScrollListener && mChildScrollListener.isOnTop();
        boolean isChildOnBottom = null != mChildScrollListener && mChildScrollListener.isOnBottom();

        if (direction < 0) {
            return getScrollY() > 0;
        } else {
            if (isChildOnBottom && isChildOnTop) {
                return getScrollY() >= 0 && getScrollY() <= getScrollRange();
            } else {
                return getScrollY() >= 0 && getScrollY() <= getScrollRange() && !isChildOnBottom;
            }
        }
    }


    /**
     * 滚动到起始位置
     */
    public void scrollToTop() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), S_RESET_DURATION);
        postInvalidate();
    }

    //nest child
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        Log.d(TAG, "=============dispatchNestedPreScroll==============");
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        Log.d(TAG, "=============dispatchNestedScroll==============");
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }


    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        Log.d(TAG, "=============dispatchNestedPreFling==============");
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "=============dispatchNestedFling==============");
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }


    //nest parent====
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d(TAG, "===============onStartNestedScroll===========");
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
        Log.d(TAG, "===============onNestedPreScroll===========");

    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        Log.d(TAG, "===============onNestedScroll===========");
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(TAG, "===============onNestedPreFling===========");
        return super.onNestedPreFling(target, velocityX, velocityY);
    }


    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "===============onNestedFling===========");
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }


    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        Log.d(TAG, "===============onStopNestedScroll===========");
        stopNestedScroll();
    }


    public interface ChildScrollListener {
        boolean isOnTop();

        boolean isOnBottom();
    }

}
