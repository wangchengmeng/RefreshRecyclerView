package com.maogu.htclibrary.app;

import android.app.Application;

/**
 * 全局应用程序
 *
 * @author zou.sq
 * @version <br>
 */
public abstract class HtcAppBase extends Application {

    private static HtcAppBase instance;

    public static HtcAppBase getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 获取Token
     */
    public abstract String getToken();
}
