package com.meng.craftsmen.refreshdemo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.maogu.htclibrary.widget.refresh.base.BaseRecyclerAdapter;
import com.maogu.htclibrary.widget.refresh.base.BaseViewHolder;

import java.util.List;

/**
 * 发现页适配器
 */
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
