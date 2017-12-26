# RefreshRecyclerView
1.自定义下拉刷新 上啦加载更多的 RecyclerView
2.使用观察者模式自动检测RecyclerView没有数据的时候显示的EmptyView
3.Adapter的封装，便捷添加header／footer，以及减少adapter中的冗余代码
4.ViewHolder的封装，结构清晰，方便使用，代码简洁

先上图：

![icon](refresh.gif)


刷新控件：RefreshLayout 
刷新头部：VRefreshHeaderView 
刷新footer：VRefreshFooterView
刷新动画：VRefreshAnimation
自定义RecyclerView：AutoLoadRecyclerView

观察Empty代码实现：
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



    封装的Adapter：BaseRecyclerAdapter
    封装的ViewHolder：BaseViewHolder

    封装前后代码对比：


    前：
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


后：

public class MainFoundAdapter2 extends BaseRecyclerAdapter<String> {
    private Context mContext;

    MainFoundAdapter2(Context context, @NonNull List<String> datas) {
        super(context, R.layout.item_main_found, datas);
        mContext = context;
    }


    @Override
    protected void onChildBindViewHolder(BaseViewHolder holder, String data) {
        //逻辑处理
        holder.setText(R.id.item_content, data);
    }
}

####对比之后，也许 就有让你进去观看的动力了


##使用方法：

监听下拉 和 上啦
 mAutoLoadRecyclerView.setAllListener(mVRefreshLayout, new RefLoadListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
            }

            @Override
            public void onLoadMore() {
                //上啦加载更多
            }
        });


 ###添加header：

  mMainFoundAdapter.addHeader(View.inflate(getApplicationContext(), R.layout.item_header, null));

###添加footer：

 mMainFoundAdapter.addFooter(View.inflate(getApplicationContext(), R.layout.item_header, null));

 ###设置空页面：

 mAutoLoadRecyclerView.setEmptyView(mEmptyView);




