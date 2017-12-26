package com.maogu.htclibrary.widget.refresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.maogu.htclibrary.R;

/**
 * @author wang.k
 *         支持垂直下拉刷新的Layout
 */
public class VRefreshLayout extends ViewGroup {

    private static final int REFRESH_WAIT_TIME = 800;
    private static final int MOVE_TIME = 400;
    private static final int RESET_TIME = 100;
    private final static float mRatioOfHeaderHeightToRefresh = 1.0f;
    private final static float mRatioOfHeaderHeightToReach = 4.0f;
    private int mMaxDragDistance = -1;
    private int mRefreshDistance;
    private int mLoadingDistance;
    private OnScrollChangedListener mOnScrollListener;
    private OnRefreshListener mOnRefreshListener;
    private VRefreshMoveListener mMoveListener;//下拉动作的监听
    private VRefreshFooterView mFooterView;//底部加载更多的View
    private View mHeaderView;
    private View mContentView;

    private ScrollerCompat mScroller;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    private boolean mIsInitMeasure = true;
    private boolean mIsRefreshing;
    private boolean mIsLoading;
    private float mLastTouchY;
    private float mDy;
    private boolean mIsNeedIntercept;
    private boolean mIsBeingDragged;
    private boolean mIsCallRefresh;
    private boolean mIsCallLoading;

    private RefreshRunnable mRefreshRunnable = new RefreshRunnable();
    private RectF mRectF = new RectF();
    private Paint mPaint = new Paint();
    private int mTouchSlop;

    private AutoRefreshCompleteListener mAutoRefreshCompleteListener;
    private boolean mIsFirstAutoRefresh = true;
    private VRefreshHeaderView mVRefreshHeaderView;

    public VRefreshLayout(Context context) {
        super(context);
        initConfig();
    }

    public VRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig();
    }

    private void initConfig() {
        mScroller = ScrollerCompat.create(getContext());
        setWillNotDraw(false);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.content_bg));
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setDefaultHeaderView();
        setDefaultFooterView();
        ensureContent();
        if (null != mContentView) {
            mContentView.bringToFront();
        }
    }

    /**
     * 设置默认刷新头部
     */
    private void setDefaultHeaderView() {
        mVRefreshHeaderView = new VRefreshHeaderView(getContext());
        setHeaderView(mVRefreshHeaderView);
    }

    /**
     * 设置默认刷新foot
     */
    private void setDefaultFooterView() {
        mFooterView = new VRefreshFooterView(getContext(), true);
        addView(mFooterView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //measure contentView
        if (mContentView != null) {
            int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int contentHeight = getMeasuredHeight() - getPaddingTop() + getPaddingBottom();
            mContentView.measure(MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY));
        }

        //measure headerView
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            if (mIsInitMeasure) {
                int measuredHeight = mHeaderView.getMeasuredHeight();
                mMaxDragDistance = (int) (measuredHeight * mRatioOfHeaderHeightToReach);
                mRefreshDistance = (int) (measuredHeight * mRatioOfHeaderHeightToRefresh);
                mIsInitMeasure = false;
            }
        }

        if (null != mFooterView) {
            measureChild(mFooterView, widthMeasureSpec, heightMeasureSpec);
            mLoadingDistance = mFooterView.getMeasuredHeight();
        }
    }

    private void ensureContent() {
        if (mContentView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt != mHeaderView && childAt != mFooterView) {
                    mContentView = childAt;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        //layout headerView
        if (mHeaderView != null) {
            mHeaderView.layout(paddingLeft, -mHeaderView.getMeasuredHeight(),
                    paddingLeft + mHeaderView.getMeasuredWidth(), 0);
        }

        //layout contentView
        if (mContentView != null) {
            int contentHeight = mContentView.getMeasuredHeight();
            int contentWidth = mContentView.getMeasuredWidth();
            int right = paddingLeft + contentWidth;
            int bottom = paddingTop + contentHeight;
            mContentView.layout(paddingLeft, paddingTop, right, bottom);

            if (null != mFooterView) {
                mFooterView.layout(paddingLeft, bottom, right, bottom + mFooterView.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getScrollY() < 0) {
            RectF rectF = mRectF;
            rectF.set(getLeft() + getPaddingLeft(), getScrollY(), getRight() + getPaddingRight(), mHeaderView.getBottom());
            canvas.drawRect(rectF, mPaint);
        }
    }

    //多手势触发
    private void dealMultiTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        if (pointerIndex < 0) {
            return;
        }

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchY = event.getY(pointerIndex);
                mActivePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId != mActivePointerId) {
                    mLastTouchY = event.getY(pointerIndex);
                    mActivePointerId = event.getPointerId(pointerIndex);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int pointerIndex1 = event.findPointerIndex(mActivePointerId);
                float moveY = event.getY(pointerIndex1);
                mDy = moveY - mLastTouchY;
                mLastTouchY = moveY;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerId = event.getPointerId(pointerIndex);
                if (mActivePointerId == pointerId) {
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;

            default:
                break;
        }
    }

    private boolean isNeedIntercept() {
        if (mContentView == null) {
            return false;
        }

        boolean isOnBottom = !canChildScrollDown();
        boolean isOnTop = !canChildScrollUp();

        if (mHeaderView != null && isOnTop) {

            return mDy > mTouchSlop || getScrollY() < 0;
        }
        if (mFooterView != null && isOnBottom && !isOnTop) {
            return mDy < -mTouchSlop || getScrollY() > 0;
        }

        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        dealMultiTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsBeingDragged) {//回调down事件为己用
                    onTouchEvent(ev);
                }

                boolean isOnTop = !canChildScrollUp();
                boolean isOnBottom = !canChildScrollDown();
                if (isOnTop || isOnBottom) {
                    mIsNeedIntercept = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // disable move when header not reach top
                mIsNeedIntercept = isNeedIntercept();
                if (mIsNeedIntercept && !mIsBeingDragged) {//交给自己处理
                    mIsBeingDragged = true;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    MotionEvent obtain = MotionEvent.obtain(ev);
                    obtain.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(ev);
                    return dispatchTouchEvent(obtain);
                }
                break;

            case MotionEvent.ACTION_UP:
                break;

            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsNeedIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != mMoveListener && !mIsRefreshing) {
                    mMoveListener.moveStart();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (mIsNeedIntercept) {
                    if (mDy > 0) {
                        //再次下拉清空上次请求
                        clearRefreshState();
                    }
                    if (null != mMoveListener) {
                        mMoveListener.moveDragging((float) getScrollY() / mRefreshDistance / 2f);
                    }

                    if (getScrollY() == 0 && mDy > 0) {
                        //默认开始下拉
                        scrollBy(0, -(int) mDy * (mMaxDragDistance + getScrollY()) / mMaxDragDistance);
                    } else if (getScrollY() < 0 && getScrollY() >= -mMaxDragDistance) {
                        //下拉状态处理
                        scrollBy(0, -(int) mDy * (mMaxDragDistance + getScrollY()) / mMaxDragDistance);
                    } else if (getScrollY() == 0 && mDy < 0) {
                        //上拉目前不做处理
                    }
                } else {
                    if (mDy != 0) {
                        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), RESET_TIME);
                        invalidate();
                        mIsBeingDragged = false;
                        clearRefLoadState();
                        //把滚动事件交给内部控件处理
                        ev.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(ev);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                resetPosition();
                break;
            case MotionEvent.ACTION_CANCEL:
                resetPosition();
                return false;
            default:
                break;
        }
        return true;
    }

    /**
     * 恢复开始位置
     */
    private void resetStartPosition() {
        if (null != mMoveListener) {
            mMoveListener.moveEnd();
        }
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), RESET_TIME);
        invalidate();
    }

    /**
     * 设置下啦刷新的position
     */
    private void resetRefreshPosition() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - mRefreshDistance, MOVE_TIME);
        invalidate();
        if (null != mMoveListener && !mIsRefreshing) {
            mMoveListener.moveRefresh();
        }
        mIsRefreshing = true;
    }

    /**
     * 设置加载更多的position
     */
    void resetLoadingPosition() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + mLoadingDistance, MOVE_TIME);
        invalidate();
        if (null != mMoveListener) {
            mMoveListener.moveLoading();
        }
        mIsLoading = true;
    }

    private void resetPosition() {
        if (getScrollY() < 0 && getScrollY() <= -mRefreshDistance) {
            resetRefreshPosition();
        } else if (getScrollY() < 0 && getScrollY() > -mRefreshDistance) {
            //refresh cancel
            resetStartPosition();
        }
    }

    /**
     * 清除记录的刷新状态
     */
    private void clearRefreshState() {
        if (mIsRefreshing) {
            mIsRefreshing = false;
            mIsCallRefresh = false;
            removeCallbacks(mRefreshRunnable);
        }
    }

    private void clearRefLoadState() {
        if (mIsRefreshing) {
            mIsRefreshing = false;
            mIsCallRefresh = false;
            removeCallbacks(mRefreshRunnable);
        }

        if (mIsLoading) {
            mIsLoading = false;
            mIsCallLoading = false;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }

        if (mScroller.isFinished()) {
            if (mIsRefreshing && !mIsCallRefresh) {
                mIsCallRefresh = true;
                notifyRefresh();
            } else if (mIsLoading && !mIsCallLoading) {
                mIsCallLoading = true;
                notifyLoading();
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        clearRefreshState();
        if (!mIsRefreshing) {
            post(new Runnable() {
                @Override
                public void run() {
                    resetRefreshPosition();
                }
            });
        }
    }

    public void refreshComplete() {
        if (mIsRefreshing) {
            postDelayed(mRefreshRunnable, REFRESH_WAIT_TIME);
        }

        if (mIsLoading) {
            resetStartPosition();
            mIsLoading = false;
            mIsCallLoading = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (null != mOnRefreshListener) {
            mOnRefreshListener = null;
        }
        super.onDetachedFromWindow();
    }

    private void setHeaderView(View view) {
        if (view == null || view == mHeaderView) {
            return;
        }
        LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }
        removeView(mHeaderView);
        mHeaderView = view;
        mIsInitMeasure = true;
        this.addView(mHeaderView);
        if (view instanceof VRefreshMoveListener) {
            setMoveListener((VRefreshMoveListener) view);
        }
    }

    private boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(mContentView, -1);
    }

    private boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(mContentView, 1);
    }

    public void addOnScrollChangedListener(OnScrollChangedListener onScrollChangeListener) {
        mOnScrollListener = onScrollChangeListener;
    }

    private void setMoveListener(VRefreshMoveListener vRefreshMoveListener) {
        mMoveListener = vRefreshMoveListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void addOnAutoRefreshCompleteListener(AutoRefreshCompleteListener refreshCompleteListener) {
        this.mAutoRefreshCompleteListener = refreshCompleteListener;
        getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mIsFirstAutoRefresh && getScrollY() == 0) {
                    getViewTreeObserver().removeOnScrollChangedListener(this);

                    if (mAutoRefreshCompleteListener != null) {
                        mAutoRefreshCompleteListener.autoRefreshComplete();
                        mIsFirstAutoRefresh = false;
                        mAutoRefreshCompleteListener = null;
                    }
                }
            }
        });
    }

    private void notifyRefresh() {
        if (mOnRefreshListener == null) {
            return;
        }
        mOnRefreshListener.onRefresh();
    }

    private void notifyLoading() {
        if (mOnRefreshListener == null) {
            return;
        }
        mOnRefreshListener.onLoading();
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldL, int oldT);
    }

    public interface OnRefreshListener {
        void onRefresh();

        void onLoading();
    }

    interface VRefreshMoveListener {
        void moveStart();

        void moveDragging(float percent);

        void moveRefresh();

        void moveEnd();

        void moveLoading();
    }

    public interface AutoRefreshCompleteListener {
        void autoRefreshComplete();
    }

    private class RefreshRunnable implements Runnable {

        @Override
        public void run() {
            resetStartPosition();
            mIsRefreshing = false;
            mIsCallRefresh = false;
        }
    }
}
