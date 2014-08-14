package com.jivesoftware.selenium.pagefactory.framework.browser;

/**
 * Represents the type of Browser being created.
 * TODO: Add support for Safari by creating a SafariBrowser class and figure out the correct DesiredCapabilities for configuring Safari.
 */
public enum BrowserType {
    IE, CHROME, FIREFOX;

    public static BrowserType forName(String name) {
        for (BrowserType type: BrowserType.values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("BrowserType must be 'IE', 'CHROME', or 'FIREFOX'");
    }
}
