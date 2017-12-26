package com.maogu.htclibrary.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 常用单位转换的辅助类
 */
public class DensityUtils {
    private static final Object mSync = new Object();
    private static final List<String> ACTION_LIST = new ArrayList<>();
    public static int screenWidth = 1080;
    private static final int DEFAULT_COOLING_TIME = 1000;

    static {
//        screenWidth = PackageUtil.getConfigInt("screen_Width");
    }

    /**
     * @param view   需要适配的view
     * @param width  高保真UI上的像素值，传0为不测量
     * @param height 高保真UI上的像素值，传0为不测量
     */
    public static void measure(View view, int width, int height) {
        if (0 != width) {
            setViewWidth(view, getScreenW() * width / screenWidth);
        }
        if (0 != height) {
            setViewHeight(view, getScreenW() * height / screenWidth);
        }
    }

    /**
     * 设置view的高度
     *
     * @param view   指定的view
     * @param height 指定的高度，以像素为单位
     */
    public static void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }

    /**
     * 设置view的宽度
     *
     * @param view  指定的view
     * @param width 指定的宽度，以像素为单位
     */
    public static void setViewWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        view.setLayoutParams(params);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue 尺寸dip
     * @return 像素值
     */
    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * =
     *
     * @param pxValue 尺寸像素
     * @return DIP值
     */
    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     *
     * @param pxValue 尺寸像素
     * @return SP值
     */
    public static int px2sp(float pxValue) {
        float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     *
     * @param spValue SP值
     * @return 像素值
     */
    public static int sp2px(float spValue) {
        float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    public static int getScreenW() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getMeasureValue(int px) {
        return getScreenW() * px / screenWidth;
    }

    /**
     * 获取屏幕高度
     *
     * @return 屏幕高度
     */
    public static int getScreenH() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @return 状态栏的高度
     */
    public static int getStatusHeight() {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = Resources.getSystem().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity 上下文
     * @return 当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenW();
        int height = getScreenH();
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity 上下文
     * @return 当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenW();
        int height = getScreenH();
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 移动光标到最后
     *
     * @param editText 输入框
     */
    public static void moveCursorToEnd(EditText editText) {
        if (editText == null) {
            return;
        }
        Editable text = editText.getText();
        if (text != null) {
            Selection.setSelection(text, text.length());
        }
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getParent() instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(left, top, right, bottom);
            view.setLayoutParams(lp);
        } else if (view.getParent() instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(left, top, right, bottom);
            view.setLayoutParams(lp);
        }
    }


    /**
     * 限制执行频率的方法。如按钮需要在指定的3000ms时间后才能再次执行，使用方式如：<br>
     *
     * @param id             方法的标识，可以使用按钮控件的id或者其他唯一标识方法的字符串
     * @param actionListener 方法的回调函数
     */
    public static void limitReClick(final String id, ActionListener actionListener) {
        if (StringUtil.isNullOrEmpty(id) || actionListener == null) {
            throw new NullPointerException();
        }

        synchronized (mSync) {
            if (ACTION_LIST.contains(id)) {
                return;
            } else {
                ACTION_LIST.add(id);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        removeAction(id);
                    }
                }, DEFAULT_COOLING_TIME);
            }
        }
        actionListener.doAction();
    }

    public static void removeAction(String id) {
        synchronized (mSync) {
            ACTION_LIST.remove(id);
        }
    }

    /**
     * @author zou.sq
     */
    public interface ActionListener {
        /**
         * 限制点击冻结接触方法
         */
        void doAction();
    }

}