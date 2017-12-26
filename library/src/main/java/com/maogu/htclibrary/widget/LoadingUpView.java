package com.maogu.htclibrary.widget;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.maogu.htclibrary.R;
import com.maogu.htclibrary.util.DensityUtils;
import com.maogu.htclibrary.util.StringUtil;

/**
 * 加载等待框 此等待框基于单个Activity内部显示
 *
 * @version 2012-11-1 下午1:31:10 xu.xb
 */
public class LoadingUpView {
    private static final int SHOW    = 3;
    private static final int DISMISS = 5;
    private View             mLoadingView;
    private CircularProgress mCircularProgress;
    private TextView         mTvMsg;
    private Activity         mCurrentActivity;
    private boolean          mIsShowing;
    private View             mParentView;
    private boolean          mIsFrameLayoutParentView;

    /**
     * 构造方法
     *
     * @param activity Activity
     * @param isBlock  是否阻塞用户操作
     */
    public LoadingUpView(Activity activity, boolean isBlock) {
        init(activity);
    }

    public LoadingUpView(Activity activity) {
        init(activity);
    }

    private void init(Activity activity) {
        this.mCurrentActivity = activity;
        if (activity.getWindow() == null) {
            return;
        }
        mParentView = activity.getWindow().getDecorView().getRootView();
        mIsFrameLayoutParentView = mParentView instanceof FrameLayout;
    }

    private void initView() {
        mLoadingView = View.inflate(mCurrentActivity, R.layout.popup, null);
        mCircularProgress = (CircularProgress) mLoadingView.findViewById(R.id.cp_progress);
        mTvMsg = (TextView) mLoadingView.findViewById(R.id.tv_popup);
        DensityUtils.setViewHeight(mCircularProgress, DensityUtils.getScreenW() / 10);
        DensityUtils.setViewWidth(mCircularProgress, DensityUtils.getScreenW() / 10);
    }

    public void showPopup() {
        showPopup("");
    }

    public void showPopup(final String msg) {
        if (mCurrentActivity == null || mCurrentActivity.isFinishing()) {
            return;
        }
        if (mParentView == null) {
            return;
        }
        if (mLoadingView == null) {
            initView();
        }
        removeLoadingView();
        addLoadingView(msg);
    }

    public void dismiss() {
        if (mIsShowing) {
            changeShowStatus(false);
            changeStatus(DISMISS);
        }
    }

    private void removeLoadingView() {
        if (mIsFrameLayoutParentView) {
            ((FrameLayout) mParentView).removeView(mLoadingView);
        }
    }

    private void addLoadingView(String msg) {
        if (mIsFrameLayoutParentView) {
            ((FrameLayout) mParentView)
                    .addView(mLoadingView, new FrameLayout.LayoutParams(DensityUtils.getScreenW(), DensityUtils.getScreenH()));
            if (StringUtil.isNullOrEmpty(msg)) {
                mTvMsg.setVisibility(View.GONE);
            } else {
                mTvMsg.setVisibility(View.VISIBLE);
                mTvMsg.setText(msg);
            }
            changeShowStatus(true);
            changeStatus(SHOW);
        }
    }

    public void onResume() {
        if (mIsShowing && mLoadingView != null) {
            changeStatus(SHOW);
        }
    }

    /**
     * 返回EvtLoadingUpView是否显示
     *
     * @return EvtLoadingUpView是否显示
     */
    public boolean isShowing() {
        return mIsShowing;
    }

    private void changeShowStatus(boolean bShowing) {
        this.mIsShowing = bShowing;
    }

    private void changeStatus(int status) {
        switch (status) {
            case SHOW:
                mCircularProgress.startAnimation();
                break;
            case DISMISS:
                mCircularProgress.stopAnimation();
                removeLoadingView();
                mLoadingView = null;
                break;
            default:
                break;
        }

    }
}
