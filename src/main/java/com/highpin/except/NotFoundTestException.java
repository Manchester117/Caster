package com.highpin.except;

/**
 * Created by Administrator on 2015/12/10.
 */
public class NotFoundTestException extends Exception{
    public NotFoundTestException() {
    }

    public NotFoundTestException(String message) {
        super(message);
    }
}
