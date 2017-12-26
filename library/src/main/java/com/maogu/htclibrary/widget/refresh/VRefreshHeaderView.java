package com.maogu.htclibrary.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maogu.htclibrary.R;
import com.maogu.htclibrary.util.DensityUtils;

/**
 * @author wang.k
 *         刷新头部
 */
public class VRefreshHeaderView extends RelativeLayout implements VRefreshLayout.VRefreshMoveListener {

    private ImageView mIvRefresh;//刷新的gifImage
    private TextView mTvRefresh;//刷新的文字
    private VRefreshAnimation mVRefreshAnimation;
    private View mView;
    private Context mContext;

    public VRefreshHeaderView(Context context) {
        this(context, null);
    }

    public VRefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.view_refresh_header, this, true);
        mIvRefresh = (ImageView) findViewById(R.id.iv_refresh_icon);
        mTvRefresh = (TextView) findViewById(R.id.tv_refresh_text);
        mVRefreshAnimation = new VRefreshAnimation(mIvRefresh);
        DensityUtils.measure(mIvRefresh, 160, 50);
        DensityUtils.setMargins(mIvRefresh, 250, 28, 0, 28);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.getMeasureValue(230)));
    }

    private void startDrawable() {
        mVRefreshAnimation.startAnimation();
    }

    private void stopDrawable() {
        mVRefreshAnimation.stopAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVRefreshAnimation.stopAnimation();
    }

    @Override
    public void moveStart() {
        //初始化位置
        stopDrawable();
        mTvRefresh.setText(mContext.getString(R.string.pull_to_refresh));
    }

    @Override
    public void moveDragging(float percent) {
        percent = Math.abs(percent);
        if (percent == 1.0f) {
            return;
        }
        //正在下拉 percent>0.55f 释放下拉刷新 否则下拉刷新
        if (percent >= 0.5f) {
            mTvRefresh.setText(mContext.getString(R.string.release_to_refresh));
        } else {
            mTvRefresh.setText(mContext.getString(R.string.pull_to_refresh));
        }

        float scalFactor = percent * 1.4f;

        mView.setScaleX(scalFactor > 1.0f ? 1f : scalFactor);
        mView.setScaleY(scalFactor > 1.0f ? 1f : scalFactor);//TODO 处理缩放
    }

    @Override
    public void moveRefresh() {
        //正在刷新中
        mTvRefresh.setText(mContext.getString(R.string.refreshing));

        mView.setScaleX(1.0f);
        mView.setScaleY(1.0f);
        startDrawable();
    }

    @Override
    public void moveEnd() {
        stopDrawable();
        mIvRefresh.setImageResource(R.mipmap.icon_loading_1);
    }

    @Override
    public void moveLoading() {

    }
}
