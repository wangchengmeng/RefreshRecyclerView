package com.maogu.htclibrary.util.bus;

import com.maogu.htclibrary.util.EvtLog;

import org.greenrobot.eventbus.EventBus;


/**
 * @author wangchengmeng
 */
public class BusUtil {

    private static final String TAG = "BusUtil";

    public static void register(Object object) {
        if (!EventBus.getDefault().isRegistered(object)) {
            EvtLog.d(TAG, object.getClass().getName() + ":register");
            EventBus.getDefault().register(object);
        }
    }

    public static void unregister(Object object) {
        if (EventBus.getDefault().isRegistered(object)) {
            EvtLog.d(TAG, object.getClass().getName() + ":unregister");
            EventBus.getDefault().unregister(object);
        }
    }

    public static void post(EventBusModel model) {
        if (null == model) {
            return;
        }
        EvtLog.d(TAG, model.toString());
        EventBus.getDefault().post(model);
    }
}
