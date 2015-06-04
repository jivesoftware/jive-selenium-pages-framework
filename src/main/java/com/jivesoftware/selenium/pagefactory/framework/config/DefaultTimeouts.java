package com.jivesoftware.selenium.pagefactory.framework.config;

/**
 * Created by charles.capps on 8/13/14.
 *
 * Default timeout constants, only for use within this package.
 * @see TimeoutType
 * @see TimeoutsConfig
 */
interface DefaultTimeouts {
    public static final int CLICK_TIMEOUT_SECONDS = 5;
    public static final int PRESENCE_TIMEOUT_SECONDS = 5;
    public static final int POLLING_WITH_REFRESH_TIMEOUT_SECONDS = 30;
    public static final int REFRESH_TIMEOUT_SECONDS = 5;
    public static final int SHORT_TIMEOUT_SECONDS = 1;
    public static final int MEDIUM_TIMEOUT_SECONDS = 5;
    public static final int LONG_TIMEOUT_SECONDS = 20;
    public static final int PAUSE_BETWEEN_KEYS_MILLIS = 50;
    public static final int PAUSE_BETWEEN_TRIES_MILLIS = 200;
    public static final int PAUSE_BETWEEN_REFRESH_SECONDS = 5;
    public static final int PAGE_LOAD_TIMEOUT_SECONDS = 80;
    public static final int PAGE_READY_TIMEOUT_SECONDS = 10;
    public static final int IMPLICIT_WAIT_TIMEOUT_MILLIS = 2000;
}
