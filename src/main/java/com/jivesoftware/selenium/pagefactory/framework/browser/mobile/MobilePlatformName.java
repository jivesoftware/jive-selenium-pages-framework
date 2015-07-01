package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

/**
 * Created by amir on 8/13/14.
 */
public enum MobilePlatformName {
    IOS("iOS"),
    ANDROID("Android");

    private String platformName;

    private MobilePlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public static MobilePlatformName forName(String name) {
        for (MobilePlatformName platform : MobilePlatformName.values()) {
            if (platform.toString().equalsIgnoreCase(name)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("Mobile Platform Name must be 'ANDROID', 'IOS'");
    }
}
