package com.maogu.htclibrary.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author kai.wang
 *         自己的高度不受父view的影响
 */
public class UnspecifiedRelativeLayout extends RelativeLayout {
    public UnspecifiedRelativeLayout(Context context) {
        super(context);
    }

    public UnspecifiedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnspecifiedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
