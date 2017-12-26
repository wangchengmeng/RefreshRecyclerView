package com.maogu.htclibrary.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * @author alina
 */
public class AppUtil {
    private static final String TAG = "AppUtil";

    /**
     * @param context 上下文
     * @param key     meta data key
     * @return meta data value
     */
    public static String getMetaDataByKey(Context context, String key) {
        String result = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                String appSign = applicationInfo.metaData.getString(key);
                if (appSign != null) {
                    result = appSign;
                }
            }
        } catch (Exception ex) {
            com.maogu.htclibrary.util.EvtLog.e(TAG, "读app key 失败.");
        }
        return result;
    }
}
