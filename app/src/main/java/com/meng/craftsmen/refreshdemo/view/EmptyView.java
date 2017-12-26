package com.meng.craftsmen.refreshdemo.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maogu.htclibrary.util.DensityUtils;
import com.meng.craftsmen.refreshdemo.R;

/**
 * Created by wangchengm
 * on 2017/12/21.
 * RecyclerView的空页面
 */

public class EmptyView extends LinearLayout {

    private Context mContext;
    private ImageView mIconImage;
    private TextView mEmptyText;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        View rootView = View.inflate(mContext, R.layout.empty_view, this);
        mIconImage = rootView.findViewById(R.id.iv_icon_image);
        mEmptyText = rootView.findViewById(R.id.tv_empty_content);
        DensityUtils.measure(mIconImage, 250, 250);
    }

    public void setEmptyImage(@DrawableRes int resID) {
        mIconImage.setImageResource(resID);
    }

    public void setEmptyText(String content) {
        mEmptyText.setText(content);
    }

    public void setEmptyTextRes(@StringRes int resID) {
        mEmptyText.setText(resID);
    }
}
