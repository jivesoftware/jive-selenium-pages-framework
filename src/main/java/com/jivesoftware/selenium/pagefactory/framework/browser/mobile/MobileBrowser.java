package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import com.google.common.base.Optional;
//import com.jivesoftware.jenesis.test.shared.actions.AndroidSeleniumActions;
//import com.jivesoftware.jenesis.test.shared.actions.BaseMobileSeleniumActions;
//import com.jivesoftware.jenesis.test.shared.actions.IOSSeleniumActions;
//import com.jivesoftware.jenesis.test.shared.pages.PageUtils;
//import com.jivesoftware.jenesis.test.shared.pages.SubPage;
//import com.jivesoftware.jenesis.test.shared.pages.TopLevelPage;
import com.jivesoftware.selenium.pagefactory.framework.actions.mobile.AndroidSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.actions.mobile.BaseMobileSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.actions.mobile.IOSSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.browser.Browser;
import com.jivesoftware.selenium.pagefactory.framework.browser.CachedPage;
import com.jivesoftware.selenium.pagefactory.framework.browser.web.WebBrowserType;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import com.jivesoftware.selenium.pagefactory.framework.pages.PageUtils;
import io.appium.java_client.AppiumDriver;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Mobile Browser - Extends Selenium's Appium Driver functionality
 * Working on Android and iOS
 * Supports pages
 */
public abstract class MobileBrowser extends Browser<AppiumDriver> {
    private static Logger logger = LoggerFactory.getLogger(MobileBrowser.class);

    protected String appiumVersion;
    protected String platformName;
    protected String platformVersion;
    protected String deviceName;
    protected String app;

    protected BaseMobileSeleniumActions actions;

    protected MobileBrowser(String baseTestUrl,
                            TimeoutsConfig timeoutsConfig,
                            String appiumVersion,
                            String platformName, String platformVersion,
                            String deviceName,
                            String app) throws JiveWebDriverException {
        super(baseTestUrl, timeoutsConfig);
        this.appiumVersion = appiumVersion;
        this.platformName = platformName;
        this.platformVersion = platformVersion;
        this.deviceName = deviceName;
        this.app = app;
    }

    public void initializeBrowser() throws JiveWebDriverException {
        this.webDriver = createWebDriver();
        this.webDriver.manage().timeouts().implicitlyWait(getImplicitWaitTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    public Dimension getSize() {
        return this.webDriver.manage().window().getSize();
    }

    protected AppiumDriver createWebDriver() throws JiveWebDriverException {
        try {
            printCapabilities(getDesiredCapabilities());
            return new AppiumDriver(new URL(getBaseTestUrl()), getDesiredCapabilities());
        } catch (IOException e) {
            throw new JiveWebDriverException("Error starting appium driver service", e);
        }
    }

    private void printCapabilities(DesiredCapabilities desiredCapabilities) {
        logger.info("Loading capabilities..");
        for (Map.Entry<String, ?> desiredCapability : desiredCapabilities.asMap().entrySet()) {
            logger.info(desiredCapability.getKey() + "  -  " + desiredCapability.getValue());
        }
    }

    @Override
    public WebBrowserType getBrowserType() {
        return WebBrowserType.MOBILE;
    }

    public abstract BaseMobileSeleniumActions getActions();

    public String getPlatformName() {
        return platformName;
    }

    public String getAppiumVersion() {
        return appiumVersion;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getApp() {
        return app;
    }

    //**********~~~~~~~~~~~~~ Mobile Actions ~~~~~~~~~~~~~~~*************
    public void Shake() {
        webDriver.shake();
    }

    public void rotateToLandscape() {
        webDriver.rotate(ScreenOrientation.LANDSCAPE);
    }

    public void rotateToPortrait() {
        webDriver.rotate(ScreenOrientation.PORTRAIT);
    }

    public void swipeLeft() {
        webDriver.swipe(getSize().getWidth(), 50, 10, 50, 1000);
    }

    /**
     * Swipe from the left to right for a second
     */
    public void swipeRight() {
        webDriver.swipe(0, 50, getSize().getWidth(), 50, 1000);
    }

    /**
     * Swipe from the top to buttom for a second
     */
    public void dragDown() {
        int midScreen = webDriver.manage().window().getSize().getWidth() / 2;
        webDriver.swipe(midScreen, 50, midScreen, getSize().getHeight() - 20, 1000);
    }

    /**
     * Swipe from the down to up for a second
     */
    public void dragUp() {
        int midScreen = webDriver.manage().window().getSize().getWidth() / 2;
        webDriver.swipe(midScreen, getSize().getHeight(), midScreen, 50, 1000);
    }

    /**
     *
     * @param startX - 0 is the left side of the smart-phone
     * @param endX
     * @param startY - 0 is the upper side of the smart-phone
     * @param endY
     * @param duration - in milliseconds
     */
    public void swipe(int startX, int endX, int startY, int endY, int duration) {
        webDriver.swipe(startX, startY, endX, endY, duration);
    }

    public void putApplicationToBackground(int duration) {
        webDriver.runAppInBackground(duration);
    }

    public void lockMobile(int duration) {
        webDriver.lockScreen(duration);
    }

    public void tap(int fingersNum, WebElement webElement, int duration) {
        webDriver.tap(fingersNum, webElement, duration);
    }
    public void tap(int fingersNum, int xLocation, int yLocation, int duration) {
        webDriver.tap(fingersNum, xLocation, yLocation, duration);
    }
}
