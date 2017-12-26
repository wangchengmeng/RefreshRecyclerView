package com.maogu.htclibrary.exception;

/**
 * 业务异常
 *
 * @author wang.xy
 */
public class MessageException extends Exception {
    private static final long serialVersionUID = 4521612743569217434L;
    public String mData;

    /**
     * @param message message info
     */
    public MessageException(String message) {
        super(message);
    }

    /**
     * @param message message info
     * @param data    data info
     */
    public MessageException(String message, String data) {
        super(message);
        this.mData = data;
    }
}
