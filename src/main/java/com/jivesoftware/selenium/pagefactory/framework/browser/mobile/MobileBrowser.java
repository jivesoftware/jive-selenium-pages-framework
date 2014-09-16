package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import com.jivesoftware.selenium.pagefactory.framework.browser.Browser;
import com.jivesoftware.selenium.pagefactory.framework.browser.web.WebBrowserType;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import com.jivesoftware.selenium.pagefactory.framework.pages.BaseTopLevelPage;
import com.jivesoftware.selenium.pagefactory.framework.pages.Page;
import com.jivesoftware.selenium.pagefactory.framework.pages.TopLevelPage;
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

    protected String browserName;
    protected String platformName;
    protected String platformVersion;
    protected String deviceName;
    protected String newCommandTimeout;
    protected String automationName;
    protected String version;
    protected String autoLaunch;
    protected String app;

    protected MobileBrowser(String baseTestUrl,
                            TimeoutsConfig timeoutsConfig, String browserName,
                            String platformName, String platformVersion,
                            String deviceName,
                            String newCommandTimeout,
                            String automationName,
                            String version,
                            String autoLaunch,
                            String app) throws JiveWebDriverException {
        super(baseTestUrl, timeoutsConfig);
        this.browserName = browserName;
        this.platformName = platformName;
        this.platformVersion = platformVersion;
        this.deviceName = deviceName;
        this.newCommandTimeout = newCommandTimeout;
        this.automationName = automationName;
        this.version = version;
        this.autoLaunch = autoLaunch;
        this.app = app;
    }

    public void initializeBrowser() throws JiveWebDriverException {
        this.webDriver = createWebDriver();
        this.webDriver.manage().timeouts().implicitlyWait(getImplicitWaitTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    public int getScreenWidth() { return this.webDriver.manage().window().getSize().getWidth();}

    public int getScreenHeight() { return this.webDriver.manage().window().getSize().getHeight();}

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

    /**
     * Refresh the current page, without giving back a newly initialized Page object.
     */
    @Override
    public void refreshPage() {
        runLeavePageHook();
        BaseTopLevelPage currentPage = PAGE_UTILS.loadCurrentPage(BaseTopLevelPage.class, webDriver, this.getActions());
        currentPage.refreshPage();
        if (optionalCachedPage.isPresent()) {
            TopLevelPage cachedPage = optionalCachedPage.get().getCachedPage();
            cachedPage.refreshElements();
        }
    }

    /**
     * @param pageClass - the class of the expected Page after refreshing.
     */
    @Override
    public <T extends TopLevelPage> T refreshPage(Class<T> pageClass) {
        runLeavePageHook();
        invalidateCachedPage();
        T page = loadTopLevelPage(pageClass);
        page.refreshPage();
        page = loadTopLevelPage(pageClass);
        setCachedPage(page);
        return page;
    }

    @Override
    public WebBrowserType getBrowserType() {
        return WebBrowserType.MOBILE;
    }

    public String getPlatformName() {
        return platformName;
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
    public void shake() {
        webDriver.shake();
    }

    public void rotateLandscape() {
        webDriver.rotate(ScreenOrientation.LANDSCAPE);
    }

    public void rotatePortrait() {
        webDriver.rotate(ScreenOrientation.PORTRAIT);
    }

    /**
     * Swipe from the right to left for a second
     */
    public void swipeLeft() {
        webDriver.swipe(getScreenWidth(), 50, 10, 50, 1000);
    }

    /**
     * Swipe from the left to right for a second
     */
    public void swipeRight() {
        webDriver.swipe(0, 50, getScreenWidth(), 50, 1000);
    }

    /**
     * Swipe from the top to bottom for a second
     */
    public void dragDown() {
        int midScreen = getScreenWidth() / 2;
        webDriver.swipe(midScreen, 160, midScreen, getScreenHeight() - 160, 1000);
    }

    /**
     * Swipe from the down to up for a second
     */
    public void dragUp() {
        int midScreen = webDriver.manage().window().getSize().getWidth() / 2;
        webDriver.swipe(midScreen, getScreenHeight() - 160, midScreen, 160, 1000);
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

    public abstract void scrollToTop();
}
