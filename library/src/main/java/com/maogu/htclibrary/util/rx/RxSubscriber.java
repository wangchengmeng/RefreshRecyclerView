package com.maogu.htclibrary.util.rx;

import android.app.Activity;

import com.maogu.htclibrary.util.EvtLog;

import rx.Subscriber;

/**
 * 封装Subscriber
 */
public class RxSubscriber<T> extends Subscriber<ResultBean<T>> {

    private Activity mActivity;

    public RxSubscriber(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        EvtLog.d(getClass().getName(), "RxSubscriber异常：" + e.toString());
        e.printStackTrace();
        //防止 解析出异常
        _error(0, "请求异常");
    }

    @Override
    public void onNext(ResultBean<T> t) {
        switch (t.getStatus()) {
            case ResultBean.RESULT_CODE_OTHER_LOGIN:
                //402传递到处理层，不要做任何处理，只是关闭加载动画而已

                break;
            case ResultBean.RESULT_CODE_NO_LOGIN:
                //403传递到处理层，不要做任何处理，只是关闭加载动画而已

                break;
            case ResultBean.RESULT_CODE_SERVER_ERROR:
                //服务器内部错误也做网络错误处理

                break;
            case ResultBean.RESULT_CODE_SUCCESS:
                _onNext(t.getResult());
                break;
            default:
                _error(t.getStatus(), t.getMsg());
                break;
        }
    }

    public void _onNext(T t) {

    }

    public void _error(int code, String msg) {

    }

    public void _onError(int code) {

    }


    private void onOtherLogin() {
        if (null != mActivity) {

        }
    }

    private void onNoAuth() {
        if (null != mActivity) {
            //token失效 去登陆
        }
    }
}