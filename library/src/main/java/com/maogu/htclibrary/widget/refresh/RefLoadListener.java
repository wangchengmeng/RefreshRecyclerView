package com.maogu.htclibrary.widget.refresh;

public interface RefLoadListener {
    /**
     * 下拉刷新回调
     */
    void onRefresh();

    /**
     * 加载更多数据回调
     */
    void onLoadMore();
}
