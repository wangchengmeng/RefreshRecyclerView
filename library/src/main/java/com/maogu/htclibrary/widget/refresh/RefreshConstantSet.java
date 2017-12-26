package com.maogu.htclibrary.widget.refresh;


import android.support.v7.widget.RecyclerView;


public class RefreshConstantSet {

    public static void setSpace(RecyclerView recyclerView, int left, int top, int right, int bottom, int headerCount, boolean isWrapContent) {
        SpacesItemDecoration itemDecoration = new SpacesItemDecoration(left, top, right, bottom, isWrapContent);
        itemDecoration.hasHeader(headerCount);
        recyclerView.addItemDecoration(itemDecoration);
    }

    public static void setSpace(RecyclerView recyclerView, int left, int top, int right, int bottom) {
        setSpace(recyclerView, left, top, right, bottom, 0, false);
    }

    public static void setSpace(RecyclerView recyclerView, int left, int top, int right, int bottom, boolean isWrapContent) {
        setSpace(recyclerView, left, top, right, bottom, 0, isWrapContent);
    }
}