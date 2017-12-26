package com.maogu.htclibrary.widget.refresh.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maogu.htclibrary.widget.refresh.VRefreshFooterView;

import java.util.List;

/**
 * Created by wangchengm
 * on 2017/12/21.
 * 对RecyclerVIew的Adapter封装基类
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private enum ITEM_TYPE {
        HEADER, //头部
        FOOTER, //尾部
        NORMAL, //中间
    }

    private int mLayoutId;//item的布局id
    private Context mContext;
    private List<T> mDatas; //数据载体

    private View mHeaderView;
    private View mFooterView;

    /**
     * 添加尾部
     *
     * @param footerView 尾部的View
     */
    public void addFooter(View footerView) {
        if (!hasFooter()) {
            mFooterView = footerView;
        }
    }

    /**
     * 移除尾部
     */
    public void removeFooter() {
        if (hasFooter()) {
            mFooterView = null;
        }
    }

    /**
     * 添加头部
     *
     * @param headerView 头部的View
     */
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
        int count = mDatas.size();//获取除 中间部分的数据条数
        if (hasHeader()) {
            count++; //如果带了header 那就+1
        }
        if (hasHeader() && position == 0) {
            return BaseRecyclerAdapter.ITEM_TYPE.HEADER.ordinal(); //返回header类型
        } else if (hasFooter() && position == count) {
            return BaseRecyclerAdapter.ITEM_TYPE.FOOTER.ordinal(); //返回footer类型
        } else {
            return BaseRecyclerAdapter.ITEM_TYPE.NORMAL.ordinal(); //返回normal类型
        }
    }

    protected BaseRecyclerAdapter(Context context, int layoutId, @NonNull List<T> datas) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mDatas = datas;
    }

    public void hasMoreData(boolean hasMore) {
        if (hasMore) {
            removeFooter();
        } else {
            addFooter(new VRefreshFooterView(mContext, false));
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根绝不同的类型处理对应的ViewHolder
        if (viewType == BaseRecyclerAdapter.ITEM_TYPE.HEADER.ordinal()) {
            return new BaseViewHolder(mContext, mHeaderView);
        } else if (viewType == BaseRecyclerAdapter.ITEM_TYPE.FOOTER.ordinal()) {
            return new BaseViewHolder(mContext, mFooterView);
        } else {
            //使用子类的ViewHolder（为了方便在子类ViewHolder里面做适配测量）
            return new BaseViewHolder(mContext,LayoutInflater.from(mContext).inflate(mLayoutId, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        if (hasHeader() && position == 0) {
            //header的逻辑分离开来，在外部处理
            return;
        }
        int count = mDatas.size();
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
        //normal的逻辑 交给实现类去处理
        if (hasHeader()) {
            onChildBindViewHolder(holder, getData(position - 1));
        } else {
            onChildBindViewHolder(holder, getData(position));
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (null != mDatas) {
            count = mDatas.size();
            if (hasHeader()) {
                count++;
            }
            if (hasFooter()) {
                count++;
            }
        }
        return count; //获取数据的数目，要加上对应的带header和footer的条数
    }

    /**
     * 获取数据对象
     *
     * @param position 列表中的位置
     * @return 返回列表中位置对应的数据
     */
    public T getData(int position) {
        if (null != mDatas) {
            return mDatas.get(position);
        }
        return null;
    }

    /**
     * 返回数据的条目数量
     *
     * @return 数目
     */
    public int getDataCount() {
        if (null != mDatas) {
            return mDatas.size();
        }
        return 0;
    }

    /**
     * 子类必须实现的方法，在该方法中进行数据处理以及显示
     *
     * @param holder holder对象
     * @param data   数据
     */
    protected abstract void onChildBindViewHolder(BaseViewHolder holder, T data);
}
