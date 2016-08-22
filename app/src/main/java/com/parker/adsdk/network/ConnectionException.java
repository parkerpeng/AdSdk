package com.parker.adsdk.network;

/**
 * Created by parker on 2016/8/9.completed
 */
public final class ConnectionException extends Exception {
    public ConnectionException(String str) {
        super(str);
    }

    public ConnectionException(Throwable tr) {
        super(tr);
    }
}
