package com.jivesoftware.selenium.pagefactory.framework.exception;

public class JiveWebDriverException extends Exception {
    public JiveWebDriverException(String msg) {
        super(msg);
    }

    public JiveWebDriverException(String msg, Exception e) {
        super(msg, e);
    }
}
