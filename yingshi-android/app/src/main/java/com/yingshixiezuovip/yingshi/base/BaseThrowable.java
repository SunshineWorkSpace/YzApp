package com.yingshixiezuovip.yingshi.base;

/**
 * Created by Resmic on 2016/9/6.
 */

public class BaseThrowable extends Throwable {
    public BaseThrowable(String string, int errorCode) {
        super(string);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    private int errorCode;
}
