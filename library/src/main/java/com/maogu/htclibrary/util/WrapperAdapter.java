package com.maogu.htclibrary.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author wangchengm
 *         适配器封装
 */
public class WrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private enum ITEM_TYPE {
        HEADER, //头部
        FOOTER, //尾部
        NORMAL, //中间
    }

    private RecyclerView.Adapter mAdapter;
    private View mHeaderView;
    private View mFooterView;

    public WrapperAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    public void addFooter(View footerView) {
        mFooterView = footerView;
        notifyDataSetChanged();
    }

    public void removeFooter() {
        if (hasFooter()) {
            mFooterView = null;
            notifyDataSetChanged();
        }
    }

    public void addHeader(View headerView) {
        mHeaderView = headerView;
    }

    public boolean hasHeader() {
        return null != mHeaderView;
    }

    public boolean hasFooter() {
        return null != mFooterView;
    }

    @Override
    public int getItemViewType(int position) {
        int count = mAdapter.getItemCount();//获取除 中间部分的数据条数
        if (hasHeader()) {
            count++; //如果带了header 那就+1
        }
        if (hasHeader() && position == 0) {
            return ITEM_TYPE.HEADER.ordinal(); //返回header类型
        } else if (hasFooter() && position == count) {
            return ITEM_TYPE.FOOTER.ordinal(); //返回footer类型
        } else {
            return ITEM_TYPE.NORMAL.ordinal(); //返回normal类型
        }
    }

    @Override
    public int getItemCount() {
        int count = mAdapter.getItemCount();
        if (hasHeader()) {
            count++;
        }
        if (hasFooter()) {
            count++;
        }
        return count; //获取数据的数目，要加上对应的带header和footer的条数
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根绝不同的类型处理对应的ViewHolder
        if (viewType == ITEM_TYPE.HEADER.ordinal()) {
            return new RecyclerView.ViewHolder(mHeaderView) {
            };
        } else if (viewType == ITEM_TYPE.FOOTER.ordinal()) {
            return new RecyclerView.ViewHolder(mFooterView) {
            };
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (hasHeader() && position == 0) {
            //header的逻辑分离开来，在外部处理
            return;
        }
        int count = mAdapter.getItemCount();
        if (hasHeader()) {
            count++;
        }
        if (hasFooter()) {
            count++;
        }
        if (hasFooter() && position == count - 1) {
            //footer的逻辑分离开来，在外部处理
            return;
        }
        //normal的逻辑还是不变，跟原生的一样处理，在传递进来的adapter中
        if (hasHeader()) {
            mAdapter.onBindViewHolder(holder, position - 1);
        } else {
            mAdapter.onBindViewHolder(holder, position);
        }
    }
}
