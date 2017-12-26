package com.maogu.htclibrary.util.rx;

public class ResultBean<T> {

    /**
     * 接口正常
     */
    public static final int RESULT_CODE_SUCCESS      = 1;
    /**
     * 网络异常
     */
    public static final int RESULT_CODE_NET_ERROR    = 111;
    /**
     * 服务器错误
     */
    public static final int RESULT_CODE_SERVER_ERROR = -1;
    /**
     * 用户被挤掉
     */
    public static final int RESULT_CODE_OTHER_LOGIN  = 402;
    /**
     * token过期状态
     */
    public static final int RESULT_CODE_NO_LOGIN     = 403;

    private int status;
    private String msg;
    private T   result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
