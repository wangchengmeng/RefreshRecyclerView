package com.meng.craftsmen.refreshdemo;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * 发现页适配器
 */
public class MainFoundAdapter extends RecyclerView.Adapter<MainFoundAdapter.ViewHolder> {

    private List<String> mFoundBeanList;
    private Activity mActivity;

    public MainFoundAdapter(Activity activity, List<String> dataList) {
        this.mActivity = activity;
        this.mFoundBeanList = dataList;
    }

    @Override
    public int getItemCount() {
        if (mFoundBeanList != null) {
            return mFoundBeanList.size();
        }
        return 0;
    }

    private String getItem(int position) {
        if (mFoundBeanList != null) {
            return mFoundBeanList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_main_found, null);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String foundBean = mFoundBeanList.get(position);
        holder.mTvProductDesc.setText(foundBean);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTvProductDesc;

        ViewHolder(View view) {
            super(view);
            mTvProductDesc = view.findViewById(R.id.item_content);
        }
    }
}