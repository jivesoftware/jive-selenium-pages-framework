package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

/**
 * Created by amir on 8/13/14.
 */
public enum MobilePlatformType {
    IOS("ios"),
    ANDROID("android");

    private String platformName;

    private MobilePlatformType(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }
}
