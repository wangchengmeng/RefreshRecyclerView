package com.maogu.htclibrary.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.maogu.htclibrary.app.HtcAppBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zeng.hh
 * @version 王先佑 2012-12-06 增加getPackageName方法，返回应用程序的包名<br>
 *          2013-03-20 xu.xb <br>
 *          1.修改获取macAddress，若获取WifiInfo对象为空时，返回空字符串<br>
 *          2.增加获取渠道信息的方法<br>
 */
public class PackageUtil {

    private static final String TAG = "PackageUtil";

    /**
     * 获取应用程序的包名
     *
     * @return 应用程序的包名
     */
    public static String getPackageName() {
        return HtcAppBase.getInstance().getBaseContext().getPackageName();
    }

    /**
     * 获取应用程序的版本号
     *
     * @return 版本号
     */
    public static int getVersionCode() {
        int verCode = -1;
        try {
            verCode = HtcAppBase.getInstance().getApplicationContext().getPackageManager().getPackageInfo(
                    HtcAppBase.getInstance().getApplicationContext().getPackageName(), 0).versionCode;
        } catch (NameNotFoundException exception) {
            EvtLog.d(TAG, exception.toString());
        }
        return verCode;
    }

    /**
     * 获取应用程序的外部版本号
     *
     * @return 外部版本号
     */
    public static String getVersionName() {
        String versionName = "";
        try {
            versionName = HtcAppBase.getInstance().getBaseContext().getPackageManager().getPackageInfo(
                    HtcAppBase.getInstance().getBaseContext().getPackageName(), 0).versionName;
        } catch (NameNotFoundException exception) {
            EvtLog.d(TAG, exception.toString());
        }
        return versionName;
    }

    /**
     * @return 获得手机型号
     */
    public static String getDeviceType() {
        return android.os.Build.MODEL;
    }

    /**
     * @return 获得手机deviceId
     */
    public static String getDeviceId() {
        String deviceId;
        deviceId = ((TelephonyManager) HtcAppBase.getInstance().getSystemService(Context.TELEPHONY_SERVICE))
                .getDeviceId();
        if (StringUtil.isNullOrEmpty(deviceId)) {
            deviceId = Secure.getString(HtcAppBase.getInstance().getContentResolver(), Secure.ANDROID_ID);
        }
        return deviceId;
    }

    /**
     * @return 获得操作系统版本号
     */
    public static String getSysVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 读取manifest.xml中application标签下的配置项，如果不存在，则返回空字符串
     *
     * @param key 键名
     * @return 返回字符串
     */
    public static String getConfigString(String key) {
        String val = "";
        try {
            ApplicationInfo appInfo = HtcAppBase.getInstance().getBaseContext().getPackageManager().getApplicationInfo(
                    HtcAppBase.getInstance().getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
            val = appInfo.metaData.getString(key);
            if (val == null) {
                EvtLog.e(TAG, "please set config value for " + key + " in manifest.xml first");
            }
        } catch (Exception e) {
            EvtLog.w(TAG, e);
        }
        return val;
    }

    /**
     * 读取manifest.xml中application标签下的配置项
     *
     * @param key 键名
     * @return 返回字符串
     */
    public static int getConfigInt(String key) {
        int val = 0;
        try {
            ApplicationInfo appInfo = HtcAppBase.getInstance().getBaseContext().getPackageManager().getApplicationInfo(
                    HtcAppBase.getInstance().getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
            val = appInfo.metaData.getInt(key);
        } catch (NameNotFoundException e) {
            EvtLog.e(TAG, e);
        }
        return val;
    }

    /**
     * 读取manifest.xml中application标签下的配置项
     *
     * @param key 键名
     * @return 返回字符串
     */
    public static boolean getConfigBoolean(String key) {
        boolean val = false;
        try {
            ApplicationInfo appInfo = HtcAppBase.getInstance().getBaseContext().getPackageManager().getApplicationInfo(
                    HtcAppBase.getInstance().getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
            val = appInfo.metaData.getBoolean(key);
        } catch (NameNotFoundException e) {
            EvtLog.e(TAG, e);
        }
        return val;
    }

    /**
     * 指定的activity所属的应用，是否是当前手机的顶级
     *
     * @param context activity界面或者application
     * @return 如果是，返回true；否则返回false
     */
    public static boolean isTopApplication(Context context) {
        if (context == null) {
            return false;
        }

        try {
            String packageName = context.getPackageName();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                // 应用程序位于堆栈的顶层
                if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            EvtLog.w(TAG, e);
        }
        return false;
    }

    /**
     * @return 友盟渠道号
     */
    public static String getUmengChannel() {
        String umengChannel = "";
        try {
            ApplicationInfo appInfo = HtcAppBase.getInstance().getBaseContext().getPackageManager().getApplicationInfo(
                    HtcAppBase.getInstance().getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
            umengChannel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (NameNotFoundException e) {
            EvtLog.e(TAG, e);
        }
        return umengChannel;
    }

    /**
     * 获取应用的VersionName
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 获取应用的VersionName
     */
    public static String getApkVersion(Context context, String packageName) {
        String version = "0.0.0";
        if (StringUtil.isNullOrEmpty(packageName)) {
            return version;
        }
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(packageName)) {
                return packageInfo.versionName;
            }
        }
        return version;
    }

    public static String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = HtcAppBase.getInstance().getBaseContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            EvtLog.e(TAG, e);
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }
}
