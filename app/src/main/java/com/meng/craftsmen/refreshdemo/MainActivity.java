package com.meng.craftsmen.refreshdemo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.maogu.htclibrary.widget.refresh.AutoLoadRecyclerView;
import com.maogu.htclibrary.widget.refresh.RefLoadListener;
import com.maogu.htclibrary.widget.refresh.RefreshLayout;
import com.meng.craftsmen.refreshdemo.view.EmptyView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchengm
 * on 2017/12/19.
 */

public class MainActivity extends AppCompatActivity {

    private RefreshLayout mVRefreshLayout;
    private AutoLoadRecyclerView mAutoLoadRecyclerView;

    private MainFoundAdapter2 mMainFoundAdapter;
    private ArrayList<String> mFoundBeanList;

    private EmptyView mEmptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariables();
        initViews();
        initListener();
    }

    private void initListener() {
        mAutoLoadRecyclerView.setAllListener(mVRefreshLayout, new RefLoadListener() {
            @Override
            public void onRefresh() {
                //下啦刷新
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAutoLoadRecyclerView.loadFinish();
                                mFoundBeanList.clear();
                                for (int i = 0; i < 20; i++) {
                                    mFoundBeanList.add("kotlin-----" + i);
                                }
                                //设置有更多数据加载
                                mAutoLoadRecyclerView.hasMoreData(mFoundBeanList.size() >= 20);
                                mMainFoundAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onLoadMore() {
                //上啦加载更多
                //模拟超时
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAutoLoadRecyclerView.loadFinish();
                                List<String> data = new ArrayList<>();
                                for (int i = 0; i < 18; i++) {
                                    data.add("kotlin-" + i);
                                }
                                mFoundBeanList.addAll(data);
                                mAutoLoadRecyclerView.hasMoreData(data.size() >= 20);
                                mMainFoundAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void initVariables() {
        //数据
        mFoundBeanList = new ArrayList<>();
        mMainFoundAdapter = new MainFoundAdapter2(this, mFoundBeanList);
    }

    private void initViews() {
        mVRefreshLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        mAutoLoadRecyclerView = (AutoLoadRecyclerView) findViewById(R.id.rv_common_list);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);

        //设置header
        mMainFoundAdapter.addHeader(View.inflate(getApplicationContext(), R.layout.item_header, null));

        //设置list
        mAutoLoadRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoLoadRecyclerView.setAdapter(mMainFoundAdapter);
        mAutoLoadRecyclerView.setEmptyView(mEmptyView);

        //进来自动刷新数据
        mAutoLoadRecyclerView.autoRefresh(mVRefreshLayout);
    }
}
