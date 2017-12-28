package com.maogu.htclibrary.widget.refresh;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.maogu.htclibrary.widget.refresh.base.BaseRecyclerAdapter;

public class AutoLoadRecyclerView extends RecyclerView {
    public static int CACHE_SIZE = 0; //缓存大小

    private boolean mHasMore; //是否可以加载更多数据
    private RefLoadListener mLoadListener; //下拉刷新和上啦加载更多的监听
    private RefreshLayout mSwipeRefreshLayout; //刷新控件

    protected View mEmptyView;//空页面的View
    private Context mContext;

    //观察者 观察列表中是否有数据
    protected AdapterDataObserver mEmptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            int count = 0;
            if (adapter instanceof BaseRecyclerAdapter) {
                if (((BaseRecyclerAdapter) adapter).hasFooter()) {
                    count = 1;
                } else {
                    count = 0;
                }
            }
            if (adapter.getItemCount() <= count) {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    AutoLoadRecyclerView.this.setVisibility(View.GONE);
                }
            } else {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                    AutoLoadRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    public AutoLoadRecyclerView(Context context) {
        this(context, null);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new AutoLoadScrollListener());
        this.mContext = context;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null && mEmptyObserver != null) {
            oldAdapter.unregisterAdapterDataObserver(mEmptyObserver);
        }
        if (adapter != null && mEmptyObserver != null) {
            adapter.registerAdapterDataObserver(mEmptyObserver);
            setItemViewCacheSize(CACHE_SIZE);
        }
        super.setAdapter(adapter);
        mEmptyObserver.onChanged();
    }

    /**
     * 手动调用该方法去观察是否还有数据
     */
    public void onChanged() {
        if (mEmptyObserver != null) {
            mEmptyObserver.onChanged();
        }
    }

    /**
     * 添加空布局
     *
     * @param emptyView 空布局的View
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }


    /**
     * 设置上拉加载更多的监听
     *
     * @param loadListener 上拉加载更多的监听
     */
    public void setAllListener(RefreshLayout layout, RefLoadListener loadListener) {
        this.mLoadListener = loadListener;
        mSwipeRefreshLayout = layout;
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (null != mLoadListener) {
                        mLoadListener.onRefresh();
                    }
                }

                @Override
                public void onLoading() {
                    if (null != mLoadListener) {
                        mLoadListener.onLoadMore();
                    }
                }
            });
        }
    }

    /**
     * 单独绑定当前的刷新layout
     *
     * @param layout VRefreshLayout
     */
    private void bindRefreshLayout(RefreshLayout layout) {
        mSwipeRefreshLayout = layout;
    }

    /**
     * 设置自动刷新
     *
     * @param layout 刷新控件
     */
    public void autoRefresh(RefreshLayout layout) {
        bindRefreshLayout(layout);
        mSwipeRefreshLayout.autoRefresh();
    }

    /**
     * 引导层弹出框
     *
     * @param autoRefreshCompleteListener 自动刷新完成监听器
     */
    public void addOnAutoRefreshCompleteListener(final RefreshLayout.AutoRefreshCompleteListener autoRefreshCompleteListener) {
        if (null == mSwipeRefreshLayout) {
            return;
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getLayoutManager().getChildCount() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    mSwipeRefreshLayout.addOnAutoRefreshCompleteListener(autoRefreshCompleteListener);
                }
            }
        });
    }

    /**
     * 是否还有更多的数据
     *
     * @param hasMore true有更多数据 显示加载状态
     *                false没有更多数据 显示没有更多状态
     */
    public void hasMoreData(boolean hasMore) {
        mHasMore = hasMore;

        //设置是否还有更多数据
        if (getAdapter() instanceof BaseRecyclerAdapter) {
            ((BaseRecyclerAdapter) getAdapter()).hasMoreData(mHasMore);
        }
    }

    /**
     * 加载数据完成调用
     */
    public void loadFinish() {
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.refreshComplete();
        }
    }

    //找到数组中的最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 滑动自动加载监听器
     */
    private class AutoLoadScrollListener extends OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            /**
             *RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
             * RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
             *
             * 原理：
             * if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
             >= recyclerView.computeVerticalScrollRange())
             return true;
             *
             * computeVerticalScrollExtent()是当前屏幕显示的区域高度，
             * computeVerticalScrollOffset() 是当前屏幕之前滑过的距离，
             * 而computeVerticalScrollRange()是整个View控件的高度。
             */

            if (!canScrollVertically(1)) {
                if (null != mSwipeRefreshLayout && mHasMore) {
                    mSwipeRefreshLayout.resetLoadingPosition();
                }
            }

            //当前RecyclerView显示出来的最后一个的item的position
//            int lastPosition = -1;
//
//            //当前状态为停止滑动状态SCROLL_STATE_IDLE时
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                if (layoutManager instanceof GridLayoutManager) {
//                    //通过LayoutManager找到当前显示的最后的item的position
//                    lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
//                } else if (layoutManager instanceof LinearLayoutManager) {
//                    lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
//                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//                    //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
//                    //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
//                    int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
//                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
//                    lastPosition = findMax(lastPositions);
//                }
//
//                //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
//                //如果相等则说明已经滑动到最后了
//                if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
//
//                    if (null != mSwipeRefreshLayout && mHasMore) {
//                        mSwipeRefreshLayout.resetLoadingPosition();
//                    }
//                }
//            }
        }
    }
}