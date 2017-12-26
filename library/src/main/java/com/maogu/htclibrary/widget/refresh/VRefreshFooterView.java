package com.maogu.htclibrary.widget.refresh;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maogu.htclibrary.R;
import com.maogu.htclibrary.util.DensityUtils;
import com.maogu.htclibrary.widget.CircularProgress;


/**
 * @author wangchengm
 *         刷新控件footer
 */
public class VRefreshFooterView extends LinearLayout {

    private CircularProgress mPbLoading;
    private TextView mTvLoading;

    private boolean mIsLoadMore;//是否是加载更多footer true是 false是没有更多的footer

    public VRefreshFooterView(Context context, boolean isLoadMore) {
        this(context, null, isLoadMore);
    }

    public VRefreshFooterView(Context context, AttributeSet attrs, boolean isLoadMore) {
        super(context, attrs);
        mIsLoadMore = isLoadMore;
        LayoutInflater.from(context).inflate(R.layout.view_refresh_footer, this, true);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        setBackgroundColor(ContextCompat.getColor(context, R.color.content_bg));
        mPbLoading = (CircularProgress) findViewById(R.id.pb_loading);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);

        //测量加载圈的大小
        DensityUtils.measure(mPbLoading, 50, 50);

        //设置整个footer的大小
        setPadding(0, DensityUtils.getMeasureValue(10), 0, DensityUtils.getMeasureValue(10));
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.getMeasureValue(160)));
        if (mIsLoadMore) {
            setLoadingLayout();
        } else {
            setNoMore();
        }
    }

    /**
     * 设置没有更多数据了
     */
    private void setNoMore() {
        mPbLoading.setVisibility(View.GONE);
        mTvLoading.setText(getContext().getResources().getString(R.string.is_loading_no_more));
        mTvLoading.setTextColor(ContextCompat.getColor(getContext(), R.color.main_black));
    }

    /**
     * 设置加载数据
     */
    private void setLoadingLayout() {
        mPbLoading.setVisibility(View.VISIBLE);
        mTvLoading.setText(getContext().getResources().getString(R.string.refresh_header_text));
        mTvLoading.setTextColor(ContextCompat.getColor(getContext(), R.color.main_black));
    }
}
